# -*- coding: utf-8 -*-
"""
Test diffing with wrap
"""

import numpy as np

def diff_with_wrap(input_array, size):
    a = np.diff(input_array)
    a[a < -(size/2)] += size
    a[a > (size/2)] -= size
    return a

size = 10
x1 = np.linspace(1, size, size)
x1 = (x1 + 5) % size
x1 *= x1

x2 = np.linspace(size, 1, size)
x2 = (x2 + 5) % size
x2 *= x2

size *= size

v1 = diff_with_wrap(x1, size)
v2 = diff_with_wrap(x2, size)

a1 = np.diff(v1)
a2 = np.diff(v2)

