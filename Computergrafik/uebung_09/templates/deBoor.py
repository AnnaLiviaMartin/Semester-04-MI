from tkinter import *
import numpy as np
import sys

WIDTH = 400
HEIGHT = 400

HPSIZE = 2
CCOLOR = "#0000FF"

BCOLOR = "#000000"
BWIDTH = 2

pointList = []
elementList = []

order_slider_scale = None

numpoints_slider_scale = None
order = 1
numpoints = 1


def drawPoints():
    """ draw (control-)points """
    for p in pointList:
        element = can.create_oval(p[0] - HPSIZE, p[1] - HPSIZE,
                                  p[0] + HPSIZE, p[1] + HPSIZE,
                                  fill=CCOLOR, outline=CCOLOR)
        elementList.append(element)


def drawPolygon():
    """ draw (control-)polygon conecting (control-)points """
    if len(pointList) > 1:
        for i in range(len(pointList) - 1):
            element = can.create_line(pointList[i][0], pointList[i][1],
                                      pointList[i + 1][0], pointList[i + 1][1],
                                      fill=CCOLOR)
            elementList.append(element)


def draw_bspline_curve():
    """ points to curve and draw """
    global numpoints
    curve_points = []

    knotvector = calc_T(pointList)

    for i in np.arange(knotvector[0], knotvector[-1], (knotvector[-1] - knotvector[0]) / numpoints):
        point = deboor(order - 1, np.array(pointList), knotvector, i)[0]
        curve_points.append([int(point[0]), int(point[1])])

    for i in range(len(curve_points) - 1):
        element = can.create_oval(curve_points[i][0] - BWIDTH / 2, curve_points[i][1] - BWIDTH / 2,
                                  curve_points[i][0] + BWIDTH / 2, curve_points[i][1] + BWIDTH / 2, fill=BCOLOR)
        elementList.append(element)


def find_index(knotvector, t):
    last_knot = 0
    for i in range(len(knotvector)):
        if knotvector[i] > t:
            return i - 1


def calc_T(points):
    knotvector = []
    knotvector.extend([0 for x in range(order)])
    last_entry = len(points) - (order - 2)
    knotvector.extend([x for x in range(1, last_entry)])
    knotvector.extend([last_entry for x in range(order)])
    return knotvector


def deboor(order, controlpoints, knotvector, t):
    """ Calculate point on bespline curve of order k """
    r = find_index(knotvector, t)
    point_span = controlpoints[r - order: r - order + 1 + order]

    if point_span.any() > 0:
        return reduce(order, point_span, knotvector, r, t)
    else:
        return pointList[0]


def reduce(order, controlpoints, knotvector, r, t):
    """ get points for order k - 1 """
    if len(controlpoints) <= 1:
        return controlpoints

    interval = [knotvector[x] for x in range(r - order + 1, r + order + 1)]
    new_points = []

    for i in range(len(controlpoints) - 1):
        small_interval = interval[i: i + order + 1]
        dist = calc_dist_factor(small_interval[0], small_interval[-1], t)
        p = controlpoints[i] * (1 - dist) + controlpoints[i + 1] * dist
        new_points.append(p)

    return reduce(order - 1, new_points, knotvector, r, t)


def calc_dist_factor(min, max, t):
    """ calculate influence of point """
    return (t - min) / (max - min)


def quit(root=None):
    """ quit programm """
    if root == None:
        sys.exit(0)
    root._root().quit()
    root._root().destroy()


def draw():
    """ draw elements """
    can.delete(*elementList)
    drawPoints()
    drawPolygon()
    if len(pointList) >= int(order):
        draw_bspline_curve()


def update_order(new):
    """ update level of order """
    global order
    order = int(new)
    draw()


def update_numpoints(new):
    """ update number of points """
    global numpoints
    numpoints = int(new)
    draw()


def clearAll():
    """ clear all (point list and canvas) """
    can.delete(*elementList)
    del pointList[:]


def mouseEvent(event):
    """ process mouse events """
    pointList.append([event.x, event.y])
    draw()


if __name__ == "__main__":
    # global numpoints_slider_scale, order_slider_scale
    # create main window
    mw = Tk()

    # create and position canvas and buttons
    cFr = Frame(mw, width=WIDTH, height=HEIGHT, relief="sunken", bd=1)
    cFr.pack(side="top")
    can = Canvas(cFr, width=WIDTH, height=HEIGHT)
    can.bind("<Button-1>", mouseEvent)
    can.pack()
    cFr = Frame(mw)
    cFr.pack(side="left")
    bClear = Button(cFr, text="Clear", command=clearAll)
    bClear.pack(side="left")
    eFr = Frame(mw)
    eFr.pack(side="right")
    bExit = Button(eFr, text="Quit", command=(lambda root=mw: quit(root)))
    bExit.pack()

    order_slider_label = Label(mw, text="Ordnung: ")
    order_slider_label.pack(side="left")
    order_slider_scale = Scale(mw, from_=2, to=20, orient=HORIZONTAL, command=update_order)
    order_slider_scale.pack(side="left")

    numpoints_slider_label = Label(mw, text="Anzahl Kurvenpunkte: ")
    numpoints_slider_label.pack(side="left")
    numpoints_slider_scale = Scale(mw, from_=1, to=1000, orient=HORIZONTAL, command=update_numpoints)
    numpoints_slider_scale.pack(side="left")

    # start
    mw.mainloop()
