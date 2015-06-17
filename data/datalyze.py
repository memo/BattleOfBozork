
import numpy as np
import csv as csv
import os
import matplotlib.pyplot as plt
import pandas as pd
from pandas import DataFrame, Series



db = pd.read_csv('Game_0_player0.csv')

plt.plot(db[' pos_x'], db[' pos_y'], 'b' )