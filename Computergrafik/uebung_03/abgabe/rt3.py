from PIL import Image
from functools import reduce
import numpy as np
import numbers


def extract(cond, x):
    if isinstance(x, numbers.Number):
        return x
    else:
        return np.extract(cond, x)


class vec3():
    def __init__(self, x, y, z):
        (self.x, self.y, self.z) = (x, y, z)

    def __mul__(self, other):
        return vec3(self.x * other, self.y * other, self.z * other)

    def __add__(self, other):
        return vec3(self.x + other.x, self.y + other.y, self.z + other.z)

    def __sub__(self, other):
        return vec3(self.x - other.x, self.y - other.y, self.z - other.z)

    def dot(self, other):
        return (self.x * other.x) + (self.y * other.y) + (self.z * other.z)

    def cross(self, other):
        return vec3(
            self.y * other.z - self.z * other.y,
            self.z * other.x - self.x * other.z,
            self.x * other.y - self.y * other.x)

    def __abs__(self):
        return self.dot(self)

    def norm(self):
        mag = np.sqrt(abs(self))
        return self * (1.0 / np.where(mag == 0, 1, mag))

    def components(self):
        return (self.x, self.y, self.z)

    def extract(self, cond):
        return vec3(extract(cond, self.x),
                    extract(cond, self.y),
                    extract(cond, self.z))

    def place(self, cond):
        r = vec3(np.zeros(cond.shape), np.zeros(cond.shape), np.zeros(cond.shape))
        np.place(r.x, cond, self.x)
        np.place(r.y, cond, self.y)
        np.place(r.z, cond, self.z)
        return r


rgb = vec3

L = vec3(5, 5, -10)  # Point light position
E = vec3(0, 0.4, -1)  # Eye position
FARAWAY = 1.0e39  # an implausibly huge distance


def raytrace(O, D, scene, bounce=0):
    # O is the ray origin, D is the normalized ray direction
    # scene is a list of Sphere objects (see below)
    # bounce is the number of the bounce, starting at zero for camera rays

    distances = [s.intersect(O, D) for s in scene]
    nearest = reduce(np.minimum, distances)
    color = rgb(0, 0, 0)
    for (s, d) in zip(scene, distances):
        hit = (nearest != FARAWAY) & (d == nearest)
        if np.any(hit):
            dc = extract(hit, d)
            Oc = O.extract(hit)
            Dc = D.extract(hit)
            cc = s.light(Oc, Dc, dc, scene, bounce)
            color += cc.place(hit)
    return color


class Sphere:
    def __init__(self, center, r, diffuse, width, height, mirror):
        self.c = center
        self.r = r
        self.diffuse = diffuse
        self.mirror = mirror
        self.w = width
        self.h = height

    def intersect(self, O, D):
        b = 2 * D.dot(O - self.c)
        c = abs(self.c) + abs(O) - 2 * self.c.dot(O) - (self.r * self.r)
        disc = (b ** 2) - (4 * c)
        sq = np.sqrt(np.maximum(0, disc))
        h0 = (-b - sq) / 2
        h1 = (-b + sq) / 2
        h = np.where((h0 > 0) & (h0 < h1), h0, h1)
        pred = (disc > 0) & (h > 0)
        return np.where(pred, h, FARAWAY)

    def diffusecolor(self, M):
        return self.diffuse

    def light(self, O, D, d, scene, bounce):
        M = (O + D * d)  # intersection point
        N = (M - self.c) * (1. / self.r)  # normal
        toL = (L - M).norm()  # direction to light
        toO = (E - M).norm()  # direction to ray origin
        nudged = M + N * .0001  # M nudged to avoid itself

        # Shadow: find if the point is shadowed or not.
        # This amounts to finding out if M can see the light
        light_distances = [s.intersect(nudged, toL) for s in scene]
        light_nearest = reduce(np.minimum, light_distances)
        seelight = light_distances[scene.index(self)] == light_nearest

        # Ambient
        color = rgb(0.05, 0.05, 0.05)

        # Lambert shading (diffuse)
        lv = np.maximum(N.dot(toL), 0)
        color += self.diffusecolor(M) * lv * seelight

        # Reflection
        if bounce < 2:
            rayD = (D - N * 2 * D.dot(N)).norm()
            color += raytrace(nudged, rayD, scene, bounce + 1) * self.mirror

        # Blinn-Phong shading (specular)
        phong = N.dot((toL + toO).norm())
        color += rgb(1, 1, 1) * np.power(np.clip(phong, 0, 1), 50) * seelight
        return color


class CheckeredSphere(Sphere):
    def diffusecolor(self, M):
        checker = ((M.x * 2).astype(int) % 2) == ((M.z * 2).astype(int) % 2)
        return self.diffuse * checker


class Triangle:
    def __init__(self, A, B, C, diffuse, mirror):
        self.A = A
        self.B = B
        self.C = C
        self.diffuse = diffuse
        self.mirror = mirror

    def intersect(self, O, D):
        u = self.B - self.A
        v = self.C - self.A
        w = O - self.A
        dXv = D.cross(v)
        wXu = w.cross(u)

        t = 1 / dXv.dot(u) * wXu.dot(v)
        r = 1 / dXv.dot(u) * dXv.dot(w)
        s = 1 / dXv.dot(u) * wXu.dot(D)

        # point e is in the triangle if r, s [0, 1] AND r + s <= 1
        return np.where(((r <= 1) & (r >= 0) & (s <= 1) & (s >= 0) & (r + s <= 1) & (t > 0)), t, FARAWAY)

    def diffusecolor(self):
        return self.diffuse

    def light(self, O, D, d, scene, bounce):
        M = (O + D * d)  # intersection point
        N = (self.A - self.B).cross(self.C - self.A).norm()  # normal
        toL = (L - M).norm()  # direction to light
        toO = (E - M).norm()  # direction to ray origin
        nudged = M + N * .0001  # M nudged to avoid itself

        # Shadow: find if the point is shadowed or not.
        # This amounts to finding out if M can see the light
        light_distances = [s.intersect(nudged, toL) for s in scene]
        light_nearest = reduce(np.minimum, light_distances)
        seelight = light_distances[scene.index(self)] == light_nearest

        # Ambient
        color = rgb(0.05, 0.05, 0.05)

        # Lambert shading (diffuse)
        lv = abs(N.dot(toL))
        color += self.diffusecolor() * lv * seelight

        # Reflection
        if bounce < 2:
            rayD = (D - N * 2 * D.dot(N)).norm()
            color += raytrace(nudged, rayD, scene, bounce + 1) * self.mirror

        # Blinn-Phong shading (specular)
        phong = N.dot((toL + toO).norm())
        color += rgb(1, 1, 1) * np.power(np.clip(phong, 0, 1), 50) * seelight
        return color


class Plane:
    def __init__(self, P, N, diffuse, mirror):
        self.P = P
        self.N = N.norm()
        self.diffuse = diffuse
        self.mirror = mirror

    # ermittelt von jedem Objekt in der Szene die Entfernung von der Kamera
    def intersect(self, O, D):
        # Berechnung der Distanz t
        t = self.N.dot(self.P - O) / self.N.dot(D)
        return np.where(t > 0, t, FARAWAY)

    def diffusecolor(self, M):
        return self.diffuse

    def light(self, O, D, d, scene, bounce):
        M = (O + D * d)  # intersection point
        toL = (L - M).norm()  # direction to light
        toO = (E - M).norm()  # direction to ray origin
        nudged = M + self.N * .0001  # M nudged to avoid itself

        # Shadow: find if the point is shadowed or not.
        # This amounts to finding out if M can see the light
        light_distances = [s.intersect(nudged, toL) for s in scene]
        light_nearest = reduce(np.minimum, light_distances)
        seelight = light_distances[scene.index(self)] == light_nearest

        # Ambient
        color = rgb(0.05, 0.05, 0.05)

        # Lambert shading (diffuse)
        lv = np.maximum(self.N.dot(toL), 0)
        color += self.diffusecolor(M) * lv * seelight

        # Reflection
        if bounce < 2:
            rayD = (D - self.N * 2 * D.dot(self.N)).norm()
            color += raytrace(nudged, rayD, scene, bounce + 1) * self.mirror

        # Blinn-Phong shading (specular)
        phong = self.N.dot((toL + toO).norm())
        color += rgb(1, 1, 1) * np.power(np.clip(phong, 0, 1), 50) * seelight
        return color


class CheckeredPlane(Plane):
    def diffusecolor(self, M):
        checker = (np.floor(M.x * 2).astype(int) % 2) == (np.floor(M.z * 2).astype(int) % 2)
        return self.diffuse * checker


class RaytracerForPic:
    """this is the raytracer which generates one image"""

    def __init__(self, width, height):
        self.scene_objects = None
        self.w = width
        self.h = height
        self.scene_objects = [
            Sphere(vec3(.5, .5, 0), .25, vec3(0, 0, 1), self.w, self.h, 0.8),  # Rechte Kugel with different materials/mirrors
            Sphere(vec3(0, 1, 0), .25, vec3(1, 0, 0), self.w, self.h, 0.5),  # Obere Kugel
            Sphere(vec3(-.5, .5, 0), .25, vec3(0, 1, 0), self.w, self.h, 0.2),  # Linke Kugel
            Triangle(vec3(.5, .5, 0), vec3(0, 1, 0), vec3(-.5, .5, 0), rgb(1, 1, 0), 0.25),
            CheckeredPlane(vec3(0, 0, 0), vec3(0, 0.1, 0), vec3(1.5, 1.5, 1.5), 0.25)
        ]

    def raytrace_image(self):
        r = float(self.w) / self.h
        # Screen coordinates: x0, y0, x1, y1.
        S = (-1, 1 / r + .25, 1, -1 / r + .25)
        x = np.tile(np.linspace(S[0], S[2], self.w), self.h)
        y = np.repeat(np.linspace(S[1], S[3], self.h), self.w)

        Q = vec3(x, y, 0)
        color = raytrace(E, (Q - E).norm(), self.scene_objects)

        rgb_channels = [Image.fromarray((255 * np.clip(c, 0, 1).reshape((self.h, self.w))).astype(np.uint8), "L") for c
                        in color.components()]
        im = Image.merge("RGB", rgb_channels)  # .save("./pictures/rt3.png")
        im = np.array(im)
        return im
