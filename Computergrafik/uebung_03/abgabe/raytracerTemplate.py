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
from rt3 import RaytracerForPic, vec3, Sphere, Triangle
from rendering import Scene, RenderWindow
import numpy as np
from typing import List, Tuple, Dict, Set, Optional, Union, Any, Callable, Iterable, Iterator


class RayTracer:

    def __init__(self, widthPic, heightPic):
        self.widthPic = widthPic
        self.heightPic = heightPic
        self.rt = RaytracerForPic(self.widthPic, self.heightPic)

    def resize(self, new_width, new_height):
        self.widthPic = new_width
        self.heightPic = new_height
        self.rt = RaytracerForPic(self.widthPic, self.heightPic)

    def rotate_pos(self):
        # angle around which to rotate
        ang = np.pi / 8
        rotation = np.array([
            [np.cos(ang), 0, -np.sin(ang), 0],
            [0, 1, 0, 0],
            [np.sin(ang), 0, np.cos(ang), 0],
            [0, 0, 0, 1]
        ])

        # combined transformation
        M = rotation

        # application to the spheres
        for i in range(len(self.rt.scene_objects)):
            obj = self.rt.scene_objects[i]

            if isinstance(obj, Sphere):
                obj: Sphere
                # homogeneous center of each object (sphere)
                ch = np.array([obj.c.x, obj.c.y, obj.c.z, 1])

                # calculate new center after rotation
                ch_new = M @ ch

                # set new value
                obj.c = vec3(ch_new[:3][0], ch_new[:3][1], ch_new[:3][2])
                self.rt.scene_objects[i] = obj
            elif isinstance(obj, Triangle):
                obj: Triangle

                # calculate new points after rotation
                ch_A = M @ np.array([obj.A.x, obj.A.y, obj.A.z, 1])
                ch_B = M @ np.array([obj.B.x, obj.B.y, obj.B.z, 1])
                ch_C = M @ np.array([obj.C.x, obj.C.y, obj.C.z, 1])

                # set new value
                obj.A = vec3(ch_A[0], ch_A[1], ch_A[2])
                obj.B = vec3(ch_B[0], ch_B[1], ch_B[2])
                obj.C = vec3(ch_C[0], ch_C[1], ch_C[2])
                self.rt.scene_objects[i] = obj

        # rerender
        self.rt.raytrace_image()

    def rotate_neg(self):
        # angle around which to rotate
        ang = np.pi / 8
        rotation = np.array([
            [np.cos(ang), 0, np.sin(ang), 0],
            [0, 1, 0, 0],
            [-np.sin(ang), 0, np.cos(ang), 0],
            [0, 0, 0, 1]
        ])

        # combined transformation
        M = rotation

        # application to the spheres
        for i in range(len(self.rt.scene_objects)):
            obj = self.rt.scene_objects[i]

            if isinstance(obj, Sphere):
                obj: Sphere
                # homogeneous center of each object (sphere)
                ch = np.array([obj.c.x, obj.c.y, obj.c.z, 1])

                # calculate new center after rotation
                ch_new = M @ ch

                # set new value
                obj.c = vec3(ch_new[:3][0], ch_new[:3][1], ch_new[:3][2])
                self.rt.scene_objects[i] = obj
            elif isinstance(obj, Triangle):
                obj: Triangle

                # calculate new points after rotation
                ch_A = M @ np.array([obj.A.x, obj.A.y, obj.A.z, 1])
                ch_B = M @ np.array([obj.B.x, obj.B.y, obj.B.z, 1])
                ch_C = M @ np.array([obj.C.x, obj.C.y, obj.C.z, 1])

                # set new value
                obj.A = vec3(ch_A[0], ch_A[1], ch_A[2])
                obj.B = vec3(ch_B[0], ch_B[1], ch_B[2])
                obj.C = vec3(ch_C[0], ch_C[1], ch_C[2])
                self.rt.scene_objects[i] = obj

        # rerender
        self.rt.raytrace_image()

    def render(self):
        return self.rt.raytrace_image()


# main function
if __name__ == '__main__':
    # set size of render viewport
    width, height = 400, 600

    # instantiate a ray tracer
    ray_tracer = RayTracer(width, height)

    # instantiate a scene
    scene = Scene(width, height, ray_tracer, "Raytracing Template")

    # pass the scene to a render window
    rw = RenderWindow(scene)

    # ... and start main loop
    rw.run()
