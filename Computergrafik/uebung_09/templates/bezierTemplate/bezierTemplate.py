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
/**         bezierTemplate.py
 *
 *          Simple Python template to generate curve points based given a list
 *          of control points. Results are displayed in a 2D scene using OpenGL.
 ****
"""

import numpy as np
import copy

from rendering import Scene, RenderWindow


def determine_points_on_bezier_curve(points, curve_type):
    """Calculates curve points based on a list of control points.

    :param  str                         curve_type: The curve type, is either 'casteljau' or 'subdivide'
    :param 	List[Tuple[float, float]]   points:     A list of controll points
    :return	List[Tuple[float, float]]   points:     A list of curve points
    """

    # TODO: Implement two different algorithms to determine points on a bezier curve
    #   1. Implementing de Casteljaus algorithm
    #   2. Implementing repeated subdivision

    return [p + 50 * (np.random.random(2) - .5) for p in points]




# call main
if __name__ == '__main__':
    print("bezierTemplate.py")
    print("pressing 'C' should clear the everything")

    # set size of render viewport
    width, height = 640, 480

    # instantiate a scene
    scene = Scene(width, height, determine_points_on_bezier_curve, "Bezier Curve Template")

    rw = RenderWindow(scene)
    rw.run()
