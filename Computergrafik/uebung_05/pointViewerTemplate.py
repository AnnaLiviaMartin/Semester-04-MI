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
/**         pointViewerTemplate.py
 *
 *          Simple Python template to load vertices, transform them to pixel
 *          coordinates and display results in a 2D scene using OpenGL.
 ****
"""

import numpy as np
import sys
from rendering import Scene, RenderWindow


if __name__ == '__main__':
    if len(sys.argv) != 2:
       print("pointViewer.py <path_to_obj>")
       print("'a' to toggle animation")
       print("'p' to switch between orthographic and perspective projection")
       print("'ESC' to quit")
       print("'x' : rotate the pointset clockwise around the x-axis ")
       print("'X' : rotate the pointset counter clockwise around the x-axis ")
       print("'y' : rotate the pointset clockwise around the y-axis ")
       print("'Y' : rotate the pointset counter clockwise around the y-axis ")
       print("'z' : rotate the pointset clockwise around the z-axis ")
       print("'Z' : rotate the pointset counter clockwise around the z-axis ")
       sys.exit(-1)

    # set size of render viewport
    width, height = 640, 480


    # TODO :
    # - read in points and replace dummy data
    x = np.random.uniform(0, width, 50)
    y = np.random.uniform(0, height, 50)
    z = np.random.uniform(-5, 5, 50)
    vertices = np.stack([x, y, z], axis=1)

    # TODO :
    # - calculate bounding box of point set
    # - determine matrix for translation of center of bounding box to origin
    # - determine matrix to scale to [-1,1]^2


    def vertex_transformation_pipeline(vertices, alpha, beta, gamma, width, height, proj_type='orthographic'):
        """
        :param      List    vertices:   The list of normalized vertex positions given to the scene
        :param      float   alpha:      Rotation around the x-axis in degrees
        :param      float   beta:       Rotation around the y-axis in degrees
        :param      float   gamma:      Rotation around the z-axis in degrees
        :param      int     width:      Scene height in pixels (may change with window resizing)
        :param      int     height:     Scene height in pixels (may change with window resizing)
        :param      str     proj_type:  The projection type to use (Either 'orthographic' or 'perspective')

        :return     List    pix_coords: A list of 2D pixel coordinates to render
        """
        # TODO :
        # - define view transformation matrix
        # - define projection matrix
        # - combine matrices to model view projection matrix
        # - apply model view projection matrix to each point
        # - transform from homogeneous to euclidian point coordinates
        # - perform view port transformation
        return vertices[:,:2]


    # instantiate a scene
    scene = Scene(width, height, vertices, vertex_transformation_pipeline, "pointViewer Template")

    rw = RenderWindow(scene)
    rw.run()
