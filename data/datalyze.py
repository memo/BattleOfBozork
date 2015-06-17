
import numpy as np
import csv as csv
import os
import matplotlib.pyplot as plt
import pandas as pd
from pandas import DataFrame, Series

width = 1280.0
height = 800.0


def wrap(X, w, wrapamt=0.5):
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

db = pd.read_csv('Game_0_player0.csv')
#db = pd.read_csv('Game_0_player0.csv')

#plt.plot(db['pos_x'], db['pos_y'], 'r-' )

# frames
F = [f for f in db['frame']]

# xpos 
X = [x for x in db['pos_x']]
Y = [y for y in db['pos_y']]
X = np.linspace(0,1000,200)
Y = np.linspace(0,1000,200)
X = wrap(X, width, 1.0)
Y = wrap(Y, height, 1.0)
plt.plot(X, Y, 'r-' )

Xi = toroid_diff(X, width, 1)
Yi = toroid_diff(Y, height, 1)

Vsq = Xi*Xi + Yi*Yi
plt.plot(Vsq, 'r-')
print Vsq
plt.plot(Xi, Yi, 'r-' )

Xj = jerk(X)
Yj = jerk(Y)    
plt.plot(Xj, Yj, 'r-' )
#np.diff( np.diff ( np.diff( )) )
        
    
vel = np.diff(pos);
vel[vel[0] < -width]  += width;
vel[vel[0] > width] -= width;
vel[vel[1] < height] -= height;
vel