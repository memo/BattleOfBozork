import numpy as np
import pandas as pd

import re

import os
from os.path import isfile, join

ID_RE = "^[a-zA-Z0-9]+_[0-9]+_pSet[0-9]+"
# player 1 and 2 
P1_RE = "p1[a-zA-Z0-9]+"
P2_RE = "p2[a-zA-Z0-9]+"

# run
RUN_RE = "run[0-9]+"

# player
RUN_RE = "player[0-9]+"

def find_pattern( r, txt ):
    pat = re.search(r, txt)
    if not pat:
        return None
    return pat.group(0)
    
def files_in_directory(path):
    ''' Returns a list of files in a directory'''
    return [ f for f in os.listdir(path) if isfile(join(path,f)) ]
    
def find_strings(S, txt, exception=None):
    ''' Returns true if all strings in the 
        list S are found in txt'''
    for s in S:
        if txt.find(s) < 0:
            return False
        if exception!=None and txt.find(exception) > -1:
            return False
    return True
    
def get_files_with_keys(files, keys, exception=None):
    ''' Gives a list of all files that have all keys in their name
    '''
    # make sure it is a list
    if type(keys) != list:
        keys = [keys]
    return [ f for f in files if find_strings(keys, f, exception) ]

def get_all_identifiers(files):
    IDs = []
    for f in files:
        pat = find_pattern(ID_RE, f)
        if pat:
            IDs.append(pat)
    return set(IDs)
        
# TODO remove this
def load_data(game_index):
    db1 = pd.read_csv('Game_' + str(game_index) + '_player0.csv')
    db2 = pd.read_csv('Game_' + str(game_index) + '_player1.csv')
    return db1, db2
    
def wrap(X, w, wrapamt=0.5):
    """ return a copy of the array with wrapped values,
    """
    Xw = X[:]
    Xw[Xw < -wrapamt*w] += w
    Xw[Xw > wrapamt*w] -= w
    return Xw
    
def toroid_diff(X, w, n=1):
    def td(X, w):
        Xi = np.diff(X)
        Xi = wrap(Xi, w)
        return Xi
        
    Xi = td(X, w)
    if n > 1:
        for i in range(1, n):
            Xi = np.diff(Xi)
    
    return Xi

def jerk(X, w):
    return toroid_diff(X, w, 3)

def mag2(X, Y):
    return X*X + Y*Y

def mag(X, Y):
    return np.sqrt(mag2(X, Y))          
    
def diff_mag(db, order=1):
    X = np.array(db['pos_x'])
    Y = np.array(db['pos_y'])
    Xj = toroid_diff(X, width, order)
    Yj = toroid_diff(Y, height, order)
    return mag(Xj, Yj)
    
def integrate(x0, X):
    X = np.concatenate(([x0], X))
    return np.cumsum(X)
    
def re_integrate_velocity(X,Y):
    ''' Given a series of coordinates X,Y
        computes velocity (toroid) and integrates
        This takes care of fixing 
        wrapping problems'''
    Xv = toroid_diff(X, width)
    Yv = toroid_diff(Y, height)

    X = integrate( X[0], Xv )
    Y = integrate( Y[0], Yv )
    return X,Y
    