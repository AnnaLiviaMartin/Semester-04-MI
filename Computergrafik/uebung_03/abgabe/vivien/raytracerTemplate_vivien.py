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
import numpy as np
import rt3_vivien
from rendering import Scene, RenderWindow


class RayTracer:

    def __init__(self, width, height):
        self.width = width
        self.height = height
        self.renderer = rt3_vivien.SceneRenderer(width, height)

    def resize(self, new_width, new_height):
        self.width = new_width
        self.height = new_height
        self.renderer = rt3_vivien.SceneRenderer(new_width, new_height)

    def rotate_pos(self):
        # Winkel um den die Rotation erfolgen soll
        ang = np.pi / 10
        rotation_matrix = np.array([
            [np.cos(ang), 0, np.sin(ang)],
            [0, 1, 0],
            [-np.sin(ang), 0, np.cos(ang)]
        ])

        # Anwendung auf die Sphären
        for obj in self.renderer.scene:
            if isinstance(obj, rt3_vivien.Sphere):
                obj.c = rt3_vivien.vec3(
                    rotation_matrix[0, 0] * obj.c.x + rotation_matrix[0, 1] * obj.c.y + rotation_matrix[0, 2] * obj.c.z,
                    rotation_matrix[1, 0] * obj.c.x + rotation_matrix[1, 1] * obj.c.y + rotation_matrix[1, 2] * obj.c.z,
                    rotation_matrix[2, 0] * obj.c.x + rotation_matrix[2, 1] * obj.c.y + rotation_matrix[2, 2] * obj.c.z
                )

    def rotate_neg(self):
        # TODO: modify scene accordingly
        pass

    def render(self):
        self.renderer.initialize_scene()
        rendered_image = self.renderer.render()
        return rendered_image


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
