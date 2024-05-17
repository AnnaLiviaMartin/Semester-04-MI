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

from rendering import Scene, RenderWindow
import numpy as np


class RayTracer:

    def __init__(self, width, height):
        self.width  = width
        self.height = height

        # TODO: setup your ray tracer

    def resize(self, new_width, new_height):
        self.width  = new_width
        self.height = new_height
        # TODO: modify scene accordingly

    def rotate_pos(self):
        # TODO: modify scene accordingly
        pass

    def rotate_neg(self):
        # TODO: modify scene accordingly
        pass

    def render(self):
        # TODO: Replace Dummy Data with Ray Traced Data
        return np.random.randint(0, 255, (self.height, self.width, 3), dtype=np.uint8)



# main function
if __name__ == '__main__':

    # set size of render viewport
    width, height = 640, 480

    # instantiate a ray tracer
    ray_tracer = RayTracer(width, height)

    # instantiate a scene
    scene = Scene(width, height, ray_tracer, "Raytracing Template")

    # pass the scene to a render window
    rw = RenderWindow(scene)

    # ... and start main loop
    rw.run()
