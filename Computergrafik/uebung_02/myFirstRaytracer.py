import numpy as np
from PIL import Image
import math


class Obj:
    """Base Class"""

    def color_at(self, dist):
        if dist > 255 or dist is None:
            return getColor(255, 255, 255)
        else:
            dist = int(dist)
            return getColor(dist, dist, dist)


class Sphere(Obj):
    def __init__(self, c, r):
        self.c = c
        self.r = r

    def intersectionParamater(self, o, d):
        p2 = getTForRaySpereIntersection(self.c, o, d, self.r)
        if p2 is None:
            return None
        return getDistance(o, p2)


class Triangle(Obj):
    def __init__(self, a, b, c):
        self.a = a
        self.b = b
        self.c = c

    def intersectionParamater(self, o, d):
        p2 = getTForRayTriangleIntersection(d, o, self.a, self.b, self.c)
        if p2 is None:
            return None
        return getDistance(o, p2)


class Plane(Obj):
    def __init__(self, n, c):
        self.n = n
        self.c = c
        self.d = n * c

    def intersectionParamater(self, o):
        p2 = getTForRayPlaneIntersection(self.n, self.c, o, self.d)
        if p2 is None:
            return None
        return getDistance(o, p2)


def generateGreyImage():
    # image width , height
    width, height = 400, 400

    # generate a gray scale (one channel) noise image
    gs_img1 = np.random.rand(width, height)
    gs_img2 = np.random.rand(width, height)
    gs_img3 = np.random.rand(width, height)

    # scale image values to [0,250]
    gs_img1 *= 255
    gs_img2 *= 255
    gs_img3 *= 255

    # generate 8 Bit PIL image. these are transposed compared to numpy arrays
    r = Image.fromarray(gs_img1.astype(np.uint8).T)
    g = Image.fromarray(gs_img2.astype(np.uint8).T)
    b = Image.fromarray(gs_img3.astype(np.uint8).T)

    # make RGB image (here all three channels are the same)
    rgb = [r, g, b]

    # merge channels and save image
    Image.merge("RGB", rgb).save("noise_image.png")


def getGeometricLength(x):
    """ || x || """
    return math.sqrt(x[2] ** 2 + x[1] ** 2 + x[0] ** 2)


def getF(c, e):
    upper = c - e
    lower = getGeometricLength(c - e)
    return upper / lower


def getS(f, up):
    upper = np.cross(f, up)  # Kreuzprodukt
    lower = getGeometricLength(upper)
    return upper / lower


def getU(s, f):
    return np.cross(s, f)


def getAlpha(FOW):
    return FOW / 2


def getH(alpha):
    # evtl. direkt ohne Konvertierung?
    # alpha_rad = np.deg2rad(alpha)
    return 2 * np.tan(alpha)


def getW(r, h):
    return r * h


def getPositionOfPixelP(x, y, c, e, up, FOW, r, image_width, image_height):
    f = getF(c, e)
    s = getS(f, up)
    u = getU(s, f)
    alpha = getAlpha(FOW)
    h = getH(alpha)
    w = getW(r, h)
    return f + w * ((x / image_width) - 0.5) * s + h * ((y / image_height) - 0.5) * u


def getD(p):
    return p / getGeometricLength(p)


def primary_ray(x, y, c, e, up, FOW, r, image_width, image_height, t):
    # ray equation is r(t) = o + t * d
    p = getPositionOfPixelP(x, y, c, e, up, FOW, r, image_width, image_height)
    d = getD(p)
    return e + t * d


def getColor(r, g, b):
    return (r, g, b)


def getWurzel(x):
    return math.sqrt(x)


def getTForRaySpereIntersection(c, o, d, r):
    """Strahl schneidet Kugel"""
    cod = np.dot(c - o, d)
    oc = o - c

    wurzelInhalt = (cod ** 2) - (np.dot(oc, oc) - (r ** 2))
    if wurzelInhalt < 0:
        return None  # schneidet kreis nicht

    t1 = cod + getWurzel(wurzelInhalt)
    t2 = cod - getWurzel((cod * cod) - (np.dot(oc, oc) - (r * r)))

    if t1 == 0 and t2 == 0:
        return 0  # berührt kreis an einem punkt

    if t1 < 0 < t2:
        return t2  # berührt kreis an zwei punkten
    elif t1 > 0 > t2:
        return t1  # berührt kreis an zwei punkten
    else:
        return None


def getTForRayPlaneIntersection(n, c, o, d):
    """Strahl schneidet Ebene"""
    t = (n * (c - o)) / (n * d)
    return t if t > 0 else None


def getTForRayTriangleIntersection(d, o, a, b, c):
    """Strahl schneidet Dreieck"""
    u = b - a
    v = c - a
    w = o - a
    dv = np.cross(d, v)
    wu = np.cross(w, u)
    trs = (1 / np.dot(dv, u)) * np.array([np.dot(wu, v), np.dot(dv, w), np.dot(wu, d)])
    t = trs[0]
    r = trs[1]
    s = trs[2]
    return trs if r + s <= 1 else None


def getDistance(p1, p2):
    """Strahl (origin, p1) shneidet Sache in Punkt p2 -> Abstand"""
    x1 = (p1[2] - p2[2]) ** 2
    x2 = (p1[1] - p2[1]) ** 2
    x3 = (p1[0] - p2[0]) ** 2
    return math.sqrt(x1 + x2 + x3)


def raytracer(image_width, image_height):
    # variables
    c = np.array([0, 3, 0])
    e = np.array([0, 1.8, 10])
    up = np.array([0, 1, 0])
    FOW = np.pi / 4

    # t = 0  # ??
    r = 1  # ??
    objectlist = list()
    sphere = Sphere(np.array([20, 2, 34]), 23)
    triangle = Triangle(np.array([1, 2, 4]), np.array([2.3, 0.4, 4]), np.array([1.3, 5, 3]))
    #triangle2 = Triangle(np.array([1, 2, 4]), np.array([1, 1, 1]), np.array([5, 5, 5]))
    triangle2 = Triangle(np.array([2, 2, 6]), np.array([1, 1, 1]), np.array([0.8, 0.8, 0.8]))
    #objectlist.append(sphere)
    #objectlist.append(triangle)
    objectlist.append(triangle2)
    image = Image.new("RGB", (image_width, image_height))

    for x in range(image_width):
        for y in range(image_height):
            # generate primary ray through pixel (x,y)
            # ray = primary_ray(x, y, c, e, up, FOW, r, image_width, image_height, t)
            maxdist = float('inf')
            color = getColor(255, 255, 255)     # background color
            for object in objectlist:
                # hitdist = object.intersectionParameter(ray)
                o = e
                d = getD(getPositionOfPixelP(x, y, c, e, up, FOW, r, image_width, image_height))
                hitdist = object.intersectionParamater(o, d)
                if hitdist is not None and hitdist < maxdist:
                    maxdist = hitdist
                    # color = object.color_at(ray)
                    color = object.color_at(hitdist)
            image.putpixel((x, y), color)
    image.save("output.png")


# run raytracer
raytracer(400, 400)
