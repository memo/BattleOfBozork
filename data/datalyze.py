import numpy as np
import csv as csv
import os
import matplotlib.pyplot as plt
import pylab as pl
import math

import pandas as pd
from pandas import DataFrame, Series

# Tmp, need to find best location for file
os.chdir('/Users/colormotor/data/cleaned')
files = files_in_directory('/Users/colormotor/data/cleaned')


width = 1280.0
height = 800.0




dani_controller_files = get_files_with_keys(files, ["p1DaniController","player0"])
dani_controller_files += get_files_with_keys(files, ["p2DaniController","player1"])

piers_controller_files = get_files_with_keys(files, ["p1PiersMCTS","player0"])
piers_controller_files += get_files_with_keys(files, ["p2PiersMCTS","player1"])

#db1, db2 = load_data(game_index=1)


###############################################
## Measure computation

def compute_exploration(X, Y, step):
    ''' Finds occurences of (x,y) in a 
        discretized grid'''
    # discretize
    Xr = np.round(X/step)*step
    Yr = np.round(Y/step)*step
    # make tuples
    U = set([(x,y) for x,y in zip(Xr, Yr)])
    # cardinality of set gives use the measue 
    return float(len(U))

def duration_measure(db, tick_duration=1.0/30.0):
    return tick_duration * len(db['frame'])

def exploration_measure(db, step=20):
    X, Y = np.array(db['pos_x']), np.array(db['pos_y'])
    return compute_exploration(X, Y, step)
    
def accel_measures(db):
    A = diff_mag(db, 2)
    return np.mean(A), np.var(A)

def jerk_measures(db):
    J = diff_mag(db, 3)
    return np.mean(J), np.var(J)
    

def get_measures(db):
    ''' Returns a dictionary with all measures 
        for db'''
    duration = duration_measure(db)
    exploration = exploration_measure(db)
    acc_mean, acc_var = accel_measures(db)
    jerk_mean, jerk_var = jerk_measures(db)
    return { 'duration':duration,
             'exploration':exploration,
             'acc_mean':acc_mean,
             'acc_var':acc_var,
             'jerk_mean':jerk_mean,
             'jerk_var':jerk_var}

def get_measure_keys():
    return [ 'duration',
           'exploration',
           'acc_mean',
           'acc_var',
           'jerk_mean',
            'jerk_var' ]
     
def get_empty_measure_array_dict():
    M = {}
    K = get_measure_keys()
    for k in K:
        M[k] = []
    return M
    
def get_measures_for_files( files ):
    ''' Gets measures for a series of files'''
    M = get_empty_measure_array_dict()
    
    for f in files:
        db = pd.read_csv(f)
        m = get_measures(db)
        for key, value in m.iteritems():
            M[key].append(value)
    
    return M
    
def get_mean_measures(M):
    ''' Returns a dict with the means for each measure'''
    m = {}
    for key, value in M.iteritems():
        m[key] = np.mean(value)
    return m

def get_stdev_measures(M):
    ''' Returns a dict with the means for each measure'''
    m = {}
    for key, value in M.iteritems():
        m[key] = np.std(value)
    return m


M = get_measures_for_files(dani_controller_files)
M_dani = M
m_dani = get_mean_measures( M )
sigma_dani = get_stdev_measures( M )
print m_dani

M = get_measures_for_files(piers_controller_files)
M_piers = M
m_piers = get_mean_measures( M )
sigma_piers =  get_stdev_measures( M )
print m_piers

##########################################
# THE JUICE IS HERE
# find all identifiers for files in folder
os.chdir('/Users/colormotor/data/cleaned')
files = files_in_directory('/Users/colormotor/data/cleaned')
ids = get_all_identifiers(files)
# now for each identifier find the mean measures
print ids

# prepare dict 
Mids = get_empty_measure_array_dict()
Mids['key'] = []
columns = [key for key in Mids.keys()]

print columns
print Mids

n = len(ids)
i = 1

for id in ids:
    print "Processing id " + str(i) + " of " + str(n)
    
    # Find all files (names) that have this identifier, and are not param files
    F = get_files_with_keys(files, [id], 'params')    
    # get measures for all files
    M = get_measures_for_files(F)    
    # get means for this id
    m = get_mean_measures(M)
    
    # add value to key entry
    for key, value in m.iteritems():
        Mids[key].append(value)
    # also add id as an entry
    Mids['key'].append(id)
    
    print "done.."
    i += 1

df = pd.DataFrame(Mids, columns=columns)
df.describe()

def normalized_dataframe(d):
    df_norm = d.copy()
    for key in df_norm:
        if df_norm[key].dtype != object:
            lo = np.min(df_norm[key])
            hi = np.max(df_norm[key])
            df_norm[key] = (df_norm[key] - lo) / (hi - lo)
    return df_norm

def compute_costs_and_sort(df):
    df = df.copy()
    # Get costs 
    def costs(df):
        D = df['duration']
        E = df['exploration']
        return np.array([ d+e for d,e in zip(D, E) ])
        
        
    df['cost'] = pd.Series(costs(df), df.index)
    df.head()
    df_sorted = df.sort('cost')
    return df_sorted
    
    
df_norm = normalized_dataframe(df)    
df_norm.describe()

df_sorted = compute_costs_and_sort(df_norm)
df_sorted.head()



print df_sorted['key'][0]
labels = np.array(df_sorted['key'])
scores = np.array(df_sorted['cost'])


print "Best (highest score)"
print labels[-1], scores[-1]

print "Worse (lowert score)"
print labels[0], scores[0]

n = len(labels)
print "Median (middle score)"
print labels[n/2], scores[n/2]

             
# print labels and scores and data sorted 
print "label,score"
for label, score in zip(labels, scores):
    print str(label) + "," + str(score)
    
df_sorted.head()
print df['acc_var']

##########################################
# Bar graph stuff
    
def map_to_lists(m, skip=[]):
    keys = []
    values = []
    for key, value in m.iteritems():
        if key in skip:
            print "Skipping " + key
            continue
        
        keys.append(key)
        values.append(value)
    return keys, values

    
def measures_bar_graphs( bars, labels, colors=['r','g','b','y'], skip=[] ):
    fig, ax = plt.subplots()

    bar_width = 0.35
    
    opacity = 0.4
    error_config = {'ecolor': '0.3'}
    
    i = 0
    
    for M, label, clr in zip(bars, labels, colors):  

        m = get_mean_measures( M )
        s = get_stdev_measures( M )
        keys, m = map_to_lists(m, skip)
        keys, s = map_to_lists(s, skip)
                
        index = np.arange(len(keys))
                        
        rects1 = plt.bar(index + (i*bar_width), 
                 m, bar_width,
                 alpha=opacity,
                 color=clr,
                 yerr=s,
                 error_kw=error_config,
                 label=label)
        
        i+=1
    
    plt.xticks( index + bar_width, keys  )
    plt.legend()
    
measures_bar_graphs( [M_dani, M_piers], ['Dani','Piers'], skip=['duration','exploration'])
print "Player1:"
print get_measures(db1)
print "Player2:"
print get_measures(db2)

###############################################
## Plotting
             
def begin_plot():
    pl.figure(figsize=(8,6), dpi=90)
    pl.subplot(1,1,1)

def end_plot():
    pl.show()
    
def plot_pos(dbs, colors ):
    begin_plot()
    pl.title('Position')
    i = 1
    for db, color in zip(dbs, colors):
        
        X, Y = np.array(db['pos_x']), np.array(db['pos_y'])
        
        explore = compute_exploration(X, Y, 20)
        pl.plot( X, 
                Y, 
              color=color, 
              linewidth=1.0,
              linestyle='-',
              label="Player " + str(i) + " expl: " + str(explore))
        i += 1
        
    pl.legend(loc='upper right')
    end_plot()      
 
def plot_diff(order, dbs, colors):
    begin_plot()
    titles = ['Arghh','Velocity', 'Acceleration', 'Jerk', 'Snap', 'Crackle', 'Pop']
    
    title = titles[order]
    title += " (" + str(order) + "th order d.)"
        
    pl.title(title)    
    
    for db, color in zip(dbs, colors):
        J = diff_mag(db, order)

        pl.plot( J, 
              color=color, 
              linewidth=1.0,
              linestyle='-')
          
    end_plot()
    
plot_pos([db1, db2],['red','green'])

#for i in range(1, 5):
#    plot_diff(i, [db1,db2], ['red','green'])

