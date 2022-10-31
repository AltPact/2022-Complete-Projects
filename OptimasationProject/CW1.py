import sys
# !pip install numpy
import numpy as np
print(sys.executable)
# line plot of input vs result for a 1d objective function
# from matplotlib import pyplot
# !pip install matplotlib
# import matplotlib.pyplot as plt
import math
import matplotlib.pyplot as plt
from numpy import arange

def f(x):
    return (1.10471*pow(x[0],2)*x[1])+(0.04811*x[2]*x[3]*(14.0+x[1]))

def T1(x):
    return 6000/math.sqrt(2*x[0]*x[1])
    
def T2(x):
    return ((6000*(14+0.5*x[1])*math.sqrt(0.25*(pow(x[1],2)+(x[0]+pow(x[2],2)))))/(2*(0.707*x[0]*x[1]*(((pow(x[1],2))/12)+0.25*(pow((x[1]+x[2]),2))))))

def g1(x):
    T = math.sqrt((T1(x) + (pow(T2(x),2))) + ((x[0]*T1(x)*T2(x))/math.sqrt(0.25*((pow(x[1],2)) + (x[0]+x[2])**2))))
    return (13600-T)
    # if ((13600-T)>=0):
    #     return True
    # else:
    #     return False

def g2(x):
    Sigma = 504000/((pow(x[2],2))*x[3])
    return (30000-Sigma)
    # if((30000-Sigma) >= 0):
    #     return True
    # else:
    #     return False

def g3(x):
    return (x[3]-x[0])
    # if((x[3]-x[0]) >=0):
    #     return True
    # else:
    #     return False
    
def g4(x):
    Pc = 64746.022*(1-0.0282346*x[2])*x[2]*(pow(x[2],3))
    return (Pc-6000)
    # if ((Pc-6000) >=0):
    #     return True
    # else:
    #     return False

x = np.array([1.05, 3.15,4.43,7.87])
print("Objective Function output: ", f(x))
print("First constaint function output: ",g1(x))
print("Second constaint function output: ",g2(x))
print("Third constaint function output: ",g3(x))
print("Fourth constaint function output: ",g4(x))