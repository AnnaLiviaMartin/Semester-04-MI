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
from rt3 import RaytracerForPic, vec3
from rendering import Scene, RenderWindow
import numpy as np


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
        pass
        """
        Rotate a vector around a given point in 3D space.

        Parameters:
        vector : np.array
            The vector to be rotated.
        point : np.array
            The point around which to rotate the vector.
        angle : float
            The angle of rotation in radians.
        axis : str
            The axis of rotation ('x', 'y', 'z').

        Returns:
        np.array
            The rotated vector.
        """
        point = self.rt.schwerpunkt()
        vector = self.E
        vector = np.array([vector.x, vector.y, vector.z])
        angle = np.pi / 2
        axis = 'z'

        # Translate the vector so that the point is at the origin
        vector_translated = vector - point

        # Rotation matrices
        if axis == 'x':
            R = np.array([[1, 0, 0],
                          [0, np.cos(angle), -np.sin(angle)],
                          [0, np.sin(angle), np.cos(angle)]])
        elif axis == 'y':
            R = np.array([[np.cos(angle), 0, np.sin(angle)],
                          [0, 1, 0],
                          [-np.sin(angle), 0, np.cos(angle)]])
        elif axis == 'z':
            R = np.array([[np.cos(angle), -np.sin(angle), 0],
                          [np.sin(angle), np.cos(angle), 0],
                          [0, 0, 1]])
        else:
            raise ValueError("Axis must be 'x', 'y' or 'z'")

        # Rotate the translated vector
        vector_rotated = np.dot(R, vector_translated)

        # Translate the vector back
        vector_final = vector_rotated + point
        vector_final = vec3(vector_final[0], vector_final[1], vector_final[2])
        self.rt.E = vector_final
        self.E = vector_final
        ray_tracer.render()

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
