import numpy as np
import csv as csv
import os
import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d import Axes3D
import matplotlib.pyplot as plt
import matplotlib.cm as cm 
from sklearn.decomposition import PCA
from sklearn.preprocessing import normalize


import pylab as pl
import math

import pandas as pd
from pandas import DataFrame, Series

import numpy as np



from utils import *

from itertools import combinations

os.chdir('/Users/colormotor/data')

res = pd.read_csv('results_mcts.csv')
res.head()

scores = vec(res['score'])
num_asteroids = vec(res['num_asteroids'])
trail_lengths = vec(res['trail_length'])
ship_momentums = vec(res['ship_momentum'])
missile_ttls = vec(res['missile_ttl'])
trail_momentums = vec(res['trail_momentum'])

I = [0,1,2,3,4]
V = [num_asteroids, trail_lengths, ship_momentums, missile_ttls, trail_momentums]
cols = ['num_asteroids', 'trail_length', 'ship_momentum', 'missile_ttl', 'trail_momentum']
labels = ["Num. Asteroids", "Trail Length", "Ship Momentum", "Missile TTTL", "Trail Momentum"]


def normalized_dataframe(d):
    df_norm = d.copy()
    for key in df_norm:
        if df_norm[key].dtype != object:
            lo = np.min(df_norm[key])
            hi = np.max(df_norm[key])
            df_norm[key] = (df_norm[key] - lo) / (hi - lo)
    return df_norm


print "OK"

def scatter_3dscalar(X, Y, Z, S, xlab, ylab, zlab,saving=False):    
    fig = plt.figure()
    C = S/2
    ax = fig.add_subplot(111, projection='3d')
    cmap = cm.jet
    ax.scatter(X, Y, Z, c=C, cmap=cmap, s= S*S*20)
    ax.set_xlabel(xlab)
    ax.set_ylabel(ylab)
    ax.set_zlabel(zlab)
    
    if saving:
        f = plt.gcf()
        fname = "plots/" + xlab + "_" + ylab + "_" + zlab + ".png"
        print "Saving file " + fname
        f.savefig(fname, dpi=90)

    plt.show()
    
def scatter_2dscalar(X,Y,S,xlab,ylab,saving=False, regression=False):    
    ''' Visualize a 2d scalar field wherer X,Y are the coordinates
        and S is the scalar value'''
    C = S/2
    cmap = cm.jet
    plt.scatter(X, Y, s=S*S*20, c=C, cmap=cmap)
    
    if regression:
        m, b = np.polyfit(X, Y, 1)
        plt.plot(X, m*X + b, '-')
        
    plt.xlabel(xlab)
    plt.ylabel(ylab)

    if saving:
        f = plt.gcf()
        fname = "plots/" + xlab + "_" + ylab + ".png"
        print "Saving file " + fname
        f.savefig(fname, dpi=150)

    plt.show()

# creates 2d scatter plots for all combinations of 2 parameters        
for c in combinations(I, 2):
    X = V[c[0]]
    Y = V[c[1]]
    xlab = labels[c[0]]
    ylab = labels[c[1]]
    scatter_2dscalar(X, Y, scores, xlab=xlab, ylab=ylab, saving=True)
    
    
# creates 3d scatter plots for all combinations of 3 parameters
for c in combinations(I, 3):
    X = V[c[0]]
    Y = V[c[1]]
    Z = V[c[2]]
        
    xlab = labels[c[0]]
    ylab = labels[c[1]]
    zlab = labels[c[2]]
        
    scatter_3dscalar(X, Y, Z, scores, xlab=xlab, ylab=ylab, zlab=zlab)
        
    
def begin_plot():
    pl.figure(figsize=(8,6), dpi=90)
    pl.subplot(1,1,1)

def end_plot():
    pl.show()
    
def plot_sorted(db, label):
    db = db.sort(label)
    plt.plot(db[label], db['score'])
    plt.xlabel(label)
    plt.ylabel("score")
    plt.show()
'''
num_asteroids = vec(res['num_asteroids'])
trail_lengths = vec(res['trail_length'])
ship_momentums = vec(res['ship_momentum'])
missile_ttls = vec(res['missile_ttl'])
trail_momentums = vec(res['trail_momentum'])
'''

plot_sorted(res, 'num_asteroids')
plot_sorted(res, 'trail_length')
plot_sorted(res, 'ship_momentum')
plot_sorted(res, 'missile_ttl')
plot_sorted(res, 'trail_momentum')

    # V = [num_asteroids, trail_lengths, ship_momentums, missile_ttls, trail_momentums]
X = np.copy(vec(V))
X = X.transpose()
Y = scores 
m, c, rank, s = np.linalg.lstsq(X,Y)

rn = normalized_dataframe(res)

desc = rn.describe()
thresh_score = desc['score']['75%']
print "Thresh:"
print thresh_score
pruned = rn[rn.score > thresh_score]
pruned.describe()

def do_leastsq(db):
    Xp = vec([ vec(db[col]) for col in cols])
    Y = vec(db['score'])
    Xp = Xp.transpose()
    return np.linalg.lstsq(Xp,Y)
    
def do_pca(db, n=2):
    
    Xp = vec([ vec(db[col]) for col in cols])
    # Transpose so rows are samples
    Xp = Xp.transpose()
    # normalize (not sure this is necessary )
    #Xp = normalize(Xp)
    
    # do pca
    pca = PCA(n_components=n)
    pca.fit(Xp)
    
    # project data 
    X_projected = pca.fit_transform(Xp)
    
    # transpose for visualization
    X_pt = X_projected.transpose()
    
    # print 
    # eigenvalues
    print "Explained Variance Ratios"
    print pca.explained_variance_ratio_

    print "Explained Variance Pecentage"
    print pca.explained_variance_ratio_*100
    # eigenvectors
    print "Principal components"
    print pca.components_
    
    # plot   
    S_pt = vec(db['score'])
    if n == 2:
        scatter_2dscalar(X_pt[0], X_pt[1], S_pt*2, xlab="Comp 1", ylab="Comp 2")
    elif n == 3:
        scatter_3dscalar(X_pt[0], X_pt[1], X_pt[2], S_pt*10, xlab="Comp 1", ylab="Comp 2", zlab="Comp 3")
    
    return pca

pca = do_pca(pruned, n=5)    

print np.transpose(labels)

m,c,rank,s = do_leastsq(pruned)