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
 *              @author:   Prof. Dr. Ulrich Schwanecke, Fabian Stahl
 *             @version:   2.0
 *                @date:   01.04.2023
 ******************************************************************************/
/**         raytracerTemplate.py
 *
 *          Simple Python template to generate ray traced images and display
 *          results in a 2D scene using OpenGL.
 ****
"""
from rt3 import RaytracerForPic, vec3, Sphere
from rendering import Scene, RenderWindow
import numpy as np
from typing import List, Tuple, Dict, Set, Optional, Union, Any, Callable, Iterable, Iterator


class RayTracer:

    # TODO change Materialeigenschaften of Kugeln
    def __init__(self, widthPic, heightPic):
        self.widthPic = widthPic
        self.heightPic = heightPic
        self.L = vec3(5, 5, -10)
        self.E = vec3(0, 0.35, -1)
        self.rt = RaytracerForPic(self.widthPic, self.heightPic, self.L, self.E, 1.0e39)

    def resize(self, new_width, new_height):
        self.widthPic = new_width
        self.heightPic = new_height
        self.rt = RaytracerForPic(self.widthPic, self.heightPic, vec3(5, 5, -10), vec3(0, 0.35, -1), 1.0e39)

    def rotate_pos(self):
        # TODO: modify scene accordingly
        # pass

        # translate center to origin
        # point around which we want to rotate
        center_of_mass = self.rt.schwerpunkt()
        xp = center_of_mass[0]
        yp = center_of_mass[1]
        zp = center_of_mass[2]
        translation = np.array([
            [1, 0, 0, -xp],
            [0, 1, 0, -yp],
            [0, 0, 1, -zp],
            [0, 0, 0, 1]
        ])

        # rotate object
        # angle around which to rotate
        ang = np.pi / 4
        rotation = np.array([
            [np.cos(ang), 0, np.sin(ang), 0],
            [0, 1, 0, 0],
            [-np.sin(ang), 0, np.cos(ang), 0],
            [0, 0, 0, 1]
        ])

        # translate center back
        translation_back = np.array([
            [1, 0, 0, xp],
            [0, 1, 0, yp],
            [0, 0, 1, zp],
            [0, 0, 0, 1]
        ])

        # combined transformation
        M = translation_back @ rotation @ translation

        # application to the spheres
        for i in range(len(self.rt.scene_objects)):
            obj = self.rt.scene_objects[i]
            obj: Sphere
            # homogeneous center of each object (sphere)
            ch = np.array([obj.c.x, obj.c.y, obj.c.z, 1])
            # calculate new center after rotation
            ch_new = M @ ch
            # set new value
            obj.c = vec3(ch_new[:3][0], ch_new[:3][1], ch_new[:3][2])
            self.rt.scene_objects[i] = obj

        # rerender
        self.rt.raytracing_Scene()

    def rotate_neg(self):
        # TODO: modify scene accordingly
        pass

    def render(self):
        return self.rt.raytracing_Scene()


# main function
if __name__ == '__main__':
    # set size of render viewport
    width, height = 400, 300

    # instantiate a ray tracer
    ray_tracer = RayTracer(width, height)

    # instantiate a scene
    scene = Scene(width, height, ray_tracer, "Raytracing Template")

    # pass the scene to a render window
    rw = RenderWindow(scene)

    # ... and start main loop
    rw.run()
