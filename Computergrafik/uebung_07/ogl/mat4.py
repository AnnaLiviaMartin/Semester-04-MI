"""
/*******************************************************************************
 *
 *            #, #,         CCCCCC  VV    VV MM      MM RRRRRRR
 *           %  %(  #%%#   CC    CC VV    VV MMM    MMM RR    RR
 *           %    %## #    CC        V    V  MM M  M MM RR    RR
 *            ,%      %    CC        VV  VV  MM  MM  MM RRRRRR
 *            (%      %,   CC    CC   VVVV   MM      MM RR   RR
 *              #%    %*    CCCCCC     VV    MM      MM RR    RR
 *             .%    %/
 *                (%.      Computer Vision & Mixed Reality Group
 *
 ******************************************************************************/
/**          @copyright:   Hochschule RheinMain,
 *                         University of Applied Sciences
 *              @author:   Prof. Dr. Ulrich Schwanecke
 *             @version:   0.91
 *                @date:   07.06.2022
 ******************************************************************************/
/**         mat4.py
 *
 *          Some convenient matrix functions
 ****
"""

import numpy as np


def rotate_x(angle):
    angle = np.radians(angle)
    return np.array([[1,       0      ,           0    , 0],
                     [0, np.cos(angle),  -np.sin(angle), 0],
                     [0, np.sin(angle),   np.cos(angle), 0],
                     [0,    0         ,      0         , 1]])


def rotate_y(angle):
    angle = np.radians(angle)
    return np.array([[np.cos(angle)   ,  0 , np.sin(angle), 0],
                     [      0         ,  1 ,          0    , 0],
                     [-np.sin(angle)  ,  0 , np.cos(angle) , 0],
                     [   0            ,  0 ,    0          , 1]])


def rotate_z(angle):
    angle = np.radians(angle)
    return np.array([[np.cos(angle), -np.sin(angle), 0, 0],
                     [np.sin(angle),  np.cos(angle), 0, 0],
                     [   0         ,      0        , 1, 0],
                     [   0         ,      0        , 0, 1]])


def rotate(angle, axis):
    angle = np.radians(angle)
    c, mc, s = np.cos(angle), 1-np.cos(angle), np.sin(angle)
    x, y, z = list(np.array(axis) / np.linalg.norm(np.array(axis)))
    return  np.array([[x*x*mc + c    , x*y*mc - z*s , x*z*mc + y*s  , 0],
                      [x*y*mc + z*s  , y*y*mc + c   , y*z*mc - x*s  , 0], 
                      [x*z*mc - y*s  , y*z*mc + x*s , z*z*mc + c    , 0], 
                      [     0        ,      0       ,      0        , 1]])


def scale(sx, sy, sz):
    return np.array([[sx, 0 , 0 , 0],
                     [0 , sy, 0 , 0],
                     [0 , 0 , sz, 0],
                     [0 , 0 , 0 , 1]])


def translate(x, y, z):
    return np.array([[1, 0, 0, x],
                     [0, 1, 0, y],
                     [0, 0, 1, z],
                     [0, 0, 0, 1]])


def look_at(ex, ey, ez, cx, cy, cz, ux, uy, uz):
    e = np.array([ex, ey, ez]) # eye position
    c = np.array([cx, cy, cz]) # center
    up = np.array([ux, uy, uz]) # up vector
    # normalize up vector
    up = up / np.linalg.norm(up)
    # get view direction
    f = (c - e) / np.linalg.norm(c - e)
    # calculate s and u
    s = np.cross(f, up) / np.linalg.norm(np.cross(f, up))
    u = np.cross(s,f)
    # create lookAt matrix
    return np.array([[ s[0] ,  s[1] ,  s[2] ,  s@e],
                     [ u[0] ,  u[1] ,  u[2] , -u@e], 
                     [-f[0] , -f[1] , -f[2] ,  f@e],
                     [  0   ,   0   ,   0   ,   1]])


def ortho(l, r, b, t, n, f):
    return np.array([[2/(r-l), 0        , 0         , -(r+l)/(r-l)],
                     [0      , 2/(t-b)  , 0         , -(t+b)/(t-b)],
                     [0      ,    0     , -2/(f-n)  , -(f+n)/(f-n)],
                     [0      ,      0   ,          0, 1]])


def frustum(l, r, b, t, n, f):
    return np.array([[2*n/(r-l) ,       0   , (r+l)/(r-l)   ,       0       ],
                     [    0     , 2*n/(t-b) , (t+b)/(t-b)   ,       0       ],
                     [    0     ,      0    , -(f+n)/(f-n)  , -2*f*n/(f-n)  ],
                     [    0     ,      0    ,    -1         ,       0       ]])


def perspective(fovy, aspect, zNear, zFar):
    f = 1.0 / np.tan(np.radians(fovy/2.0)) # cotan(fovy/2)
    return np.array([[f/aspect ,   0,          0               ,       0],
                     [ 0       ,   f,          0               ,       0],
                     [ 0       ,   0, (zFar+zNear)/(zNear-zFar), (2*zFar*zNear)/(zNear-zFar)],
                     [ 0       ,   0,          -1              , 0]])



