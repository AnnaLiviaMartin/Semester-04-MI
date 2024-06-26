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
/**         morphTemplate.py
 *
 *          Simple Python template to morph between two polygons and display
 *          results in a 2D scene using OpenGL.
 ****
"""

import numpy as np
import sys

from rendering import Scene, RenderWindow


# main
if __name__ == '__main__':
    if len(sys.argv) != 3:
        print("Not enough arguments! Usage:")
        print("\tpython3 morphTemplate.py  <firstPolygon> <secondPolygon>")
        sys.exit(-1)

    # set size of render viewport
    width, height = 640, 480

    no_rand_points = 100
    polygon_A = np.random.uniform(-min(width, height)/2.5, min(width, height)/2.5, (no_rand_points, 2))
    polygon_B = np.random.uniform(-min(width, height)/2.5, min(width, height)/2.5, (no_rand_points, 2))

    # TODO 1:
    # - parse polygons from files to numpy arrays with shape [n, 2] and replace polygon_A and polygon_B
    def read_polygon_from_file(filename):
        with open(filename, 'r') as file:
            lines = file.readlines()
            points = []
            for line in lines:
                x, y = map(float, line.strip().split())
                points.append([x, y])
            return np.array(points)

    # Read polygons from files
    polygon_A = read_polygon_from_file(sys.argv[1])
    polygon_B = read_polygon_from_file(sys.argv[2])
    print(polygon_A)
    print(polygon_B)

    # TODO 2:
    # - make both polygons contain the same number of points
    num_points = max(len(polygon_A), len(polygon_B))
    #np.concatenate()
    polygon_A = np.resize(polygon_A, (num_points, 2))
    polygon_B = np.resize(polygon_B, (num_points, 2))
    polygon_copy = polygon_A
    print(polygon_A)
    print(polygon_B)

    # TODO 3:
    # - transform from local into global coordinate system
    scale = 500
    polygon_A = polygon_A * scale
    polygon_B = polygon_B * scale

    # TODO 4:
    # - define the interpolation method given both polygons and an interpolation parameter t in [0, 1]
    def interpolate(p1, p2, t):
        """
        :param      np.array[n, 2]  p1:     n * (x, y) points of polygon A
        :param      np.array[n, 2]  p2:     n * (x, y) points of polygon B
        :param      float           t:      Interpolation parameter

        :return     np.array[n, 2]  p_out:  Interpolated output points
        """
        return p1 * (1 - t) + p2 * t

    # instantiate a scene
    scene = Scene(width, height, polygon_A, polygon_B, "Morphing Template", interpolation_fct=interpolate)

    # pass the scene to a render window
    rw = RenderWindow(scene)
    rw.run()
