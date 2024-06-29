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
 *              @author:   Prof. Dr. Ulrich Schwanecke
 *             @version:   0.91
 *                @date:   07.06.2022
 ******************************************************************************/
/**         ogl.py
 *
 *          Simple Python OpenGL program that uses PyOpenGL + GLFW to get an
 *          OpenGL 3.2 core profile context and animate a colored triangle.
 ****
"""
import sys
import glfw
import numpy as np
from OpenGL.GL import *
from OpenGL.arrays.vbo import VBO
from OpenGL.GL.shaders import *
from mat4 import *
from readOutFile import load_from_file, compute_normals
from sys import argv
from OpenGL.GLU import *

EXIT_FAILURE = -1


class Scene:
    """
        OpenGL scene class that render a RGB colored tetrahedron.
    """

    def __init__(self, width, height, scenetitle="Hello Object!"):
        self.prev_mouse_pos = None
        self.scenetitle = scenetitle
        self.width = width
        self.height = height
        self.angle_rotation_increment = 15
        self.angle = 0
        self.angleX = 0
        self.angleY = 0
        self.angleZ = 0
        self.angle_increment = 1
        self.animate = False
        self.fovy = 45.0
        self.translation_x = 0
        self.p1_arcball = np.array([1, 1, 1])   # Punkt P1 für archball
        self.p2_arcball = np.array([1, 1, 1])   # Punkt P2 für archball
        self.rotation_direction_vector = np.array([1, 1, 1])   # Richtungsvektor
        self.rotation_angle = 0.0               # Winkel um den man dreht
        self.first_click_done = False
        self.projection = True                  # Projektionsart die aktiviert ist
        self.horizontal_shift_amount = 0.002    # Wie schnell das Objekt von links nach rechts gezogen wird
        self.rotation_model = rotate(0, np.array([1,1,1]))


    def init_GL(self):
        # setup buffer (vertices, colors, normals, ...)
        self.gen_buffers()

        # setup shader
        glBindVertexArray(self.vertex_array)
        vertex_shader = open("shader.vert", "r").read()
        fragment_shader = open("shader.frag", "r").read()
        vertex_prog = compileShader(vertex_shader, GL_VERTEX_SHADER)
        frag_prog = compileShader(fragment_shader, GL_FRAGMENT_SHADER)
        self.shader_program = compileProgram(vertex_prog, frag_prog)

        # unbind vertex array to bind it again in method draw
        glBindVertexArray(0)

    def gen_buffers(self):
        """generiert die Punkte, Farben und Dreiecke je nach Kommandozeilenargument"""
        if len(argv) > 1:
            input_file = argv[1]
        else:
            input_file = "./models/elephant.obj"       
        vertices, faces, normals, has_normals, colors = load_from_file(input_file)
        #if not has_normals:
        #    normals = compute_normals(vertices, faces)

        # generate vertex array object
        self.vertex_array = glGenVertexArrays(1)
        glBindVertexArray(self.vertex_array)

        # generate and fill buffer with vertex positions (attribute 0) -> vertices??
        positions = np.array(vertices, dtype=np.float32).flatten()
        pos_buffer = glGenBuffers(1)
        glBindBuffer(GL_ARRAY_BUFFER, pos_buffer)
        glBufferData(GL_ARRAY_BUFFER, positions.nbytes, positions, GL_STATIC_DRAW)
        glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 0, None)
        glEnableVertexAttribArray(0)

        # generate and fill buffer with vertex colors (attribute 1)
        colors = np.array(colors, dtype=np.float32).flatten()
        col_buffer = glGenBuffers(1)
        glBindBuffer(GL_ARRAY_BUFFER, col_buffer)
        glBufferData(GL_ARRAY_BUFFER, colors.nbytes, colors, GL_STATIC_DRAW)
        glVertexAttribPointer(1, 3, GL_FLOAT, GL_FALSE, 0, None)
        glEnableVertexAttribArray(1)

        # generate index buffer (for triangle strip)) -> faces??
        self.indices = np.array(faces, dtype=np.int32).flatten()
        ind_buffer_object = glGenBuffers(1)
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ind_buffer_object)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, self.indices.nbytes, self.indices, GL_STATIC_DRAW)

        # unbind buffers to bind again in draw()
        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glBindVertexArray(0)

    def set_size(self, width, height):
        self.width = width
        self.height = height

    def projectOnSphere(self, x, y, r):
        x, y = x - width / 2.0, height / 2.0 - y

        a = min(r * r, x ** 2 + y ** 2)
        z = np.sqrt(r * r - a)
        l = np.sqrt(x ** 2 + y ** 2 + z ** 2)
        if l == 0:
            return x, y, z
        else:
            return x / l, y / l, z / l

    def draw_scene_when_mouse_used(self, window):
        """Bei Mouseuse wird entweder die Arch-Ball berechnet oder horizontal verschoben"""
        x, y = glfw.get_cursor_pos(window)
        if glfw.get_mouse_button(window, glfw.MOUSE_BUTTON_RIGHT) == glfw.PRESS:        # Rechts Klick: parallel verschieben
            dx = x - self.prev_mouse_pos
            self.translation_x += dx * self.horizontal_shift_amount
            self.prev_mouse_pos = x
            self.draw()

        # if glfw.get_mouse_button(window, glfw.MOUSE_BUTTON_LEFT) == glfw.PRESS:         # Links Klick: Arcball-Metapher-Rotation
        #     px, py, pz = self.projectOnSphere(x, y, 500)
        #     if not self.first_click_done:  # Erster Klick festlegen
        #         self.p1_arcball = np.array([px, py, pz]) / np.linalg.norm(self.p1_arcball)
        #         self.first_click_done = True
        #     else:  # Weitere Bewegungen nach dem ersten Klick
        #         self.p2_arcball = np.array([px, py, pz]) / np.linalg.norm(self.p2_arcball)
        #         cross_p1_p2 = np.cross(self.p1_arcball, self.p2_arcball)
        #         if not np.allclose(cross_p1_p2, [0, 0, 0]):
        #             self.rotation_direction_vector = cross_p1_p2
        #         dot_product = np.dot(self.p1_arcball, self.p2_arcball)
        #         if dot_product > -1 and dot_product < 1:
        #             alpha = np.arccos(dot_product)
        #             if not np.isnan(alpha):
        #                 self.rotation_angle = alpha * 100
        #         self.p2_arcball = np.array([px, py, pz])

        # if glfw.get_mouse_button(window, glfw.MOUSE_BUTTON_LEFT) == glfw.RELEASE:
        #     px, py, pz = self.projectOnSphere(x, y, 1.0)
        #     self.p1_arcball = np.array([px, py, pz]) / np.linalg.norm(self.p1_arcball)
        #     self.first_click_done = False


    def draw(self):
        """ 1. Render geometry 
            (a) just as a wireframe model and 
            with 
            (b) a shader that realize Gouraud Shading
            (c) a shader that realize Phong Shading
         2. Rotate object around the x, y, z axis using the keys x, y, z
         3. Rotate object with the mouse by realizing the arcball metaphor as 
            well as scaling an translation
         4. Realize Shadow Mapping"""
        
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)

        if self.animate:
            # increment rotation angle in each frame
            self.angleX += self.angle_increment

        # switching projections
        if self.projection:
            projection = perspective(self.fovy, self.width / self.height, 1.0, 5.0)
        else:
            projection = ortho(-1.0, 1.0, -1.0, 1.0, -1.0, 5.0)

        # setup matrices
        view = look_at(0, 0, 2, 0, 0, 0, 0, 1, 0)
        model_rotation_x_y_z = rotate_x(self.angleX) @ rotate_y(self.angleY) @ rotate_z(self.angleZ)
        model = translate(self.translation_x, 0, 0) @ model_rotation_x_y_z @ self.rotation_model  #matrixmultiplikation 
        mvp_matrix = projection @ view @ model

        # enable shader & set uniforms
        glUseProgram(self.shader_program)

        # determine location of uniform variable varName
        varLocation = glGetUniformLocation(self.shader_program, 'modelview_projection_matrix')
        # pass value to shader
        glUniformMatrix4fv(varLocation, 1, GL_TRUE, mvp_matrix)

        # enable vertex array & draw triangle(s)
        glBindVertexArray(self.vertex_array)

        glDrawElements(GL_TRIANGLES, self.indices.nbytes // 4, GL_UNSIGNED_INT, None)
        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE)  # GL_FILL || GL_LINE -> je nachdem wie das Objekt aussehen soll

        # unbind the shader and vertex array state
        glUseProgram(0)
        glBindVertexArray(0)


class RenderWindow:
    """
        GLFW Rendering window class
    """

    def __init__(self, scene):
        # initialize GLFW
        if not glfw.init():
            sys.exit(EXIT_FAILURE)

        # request window with old OpenGL 3.2
        glfw.window_hint(glfw.OPENGL_PROFILE, glfw.OPENGL_CORE_PROFILE)
        glfw.window_hint(glfw.OPENGL_FORWARD_COMPAT, glfw.TRUE)
        glfw.window_hint(glfw.CONTEXT_VERSION_MAJOR, 3)
        glfw.window_hint(glfw.CONTEXT_VERSION_MINOR, 2)

        # make a window
        self.width, self.height = scene.width, scene.height
        self.aspect = self.width / self.height
        self.window = glfw.create_window(self.width, self.height, scene.scenetitle, None, None)
        if not self.window:
            glfw.terminate()
            sys.exit(EXIT_FAILURE)

        # Make the window's context current
        glfw.make_context_current(self.window)

        # initialize GL
        self.init_GL()

        # set window callbacks
        glfw.set_mouse_button_callback(self.window, self.on_mouse_pressed)
        glfw.set_key_callback(self.window, self.on_keyboard_pressed)
        glfw.set_window_size_callback(self.window, self.on_size)
        glfw.set_scroll_callback(self.window, self.on_mouse_scroll_zoom)
        glfw.set_cursor_pos_callback(self.window, self.on_mouse_move)

        # create scene
        self.scene = scene
        if not self.scene:
            glfw.terminate()
            sys.exit(EXIT_FAILURE)

        self.scene.init_GL()

        # exit flag
        self.exitNow = False

    def init_GL(self):
        # debug: print GL and GLS version
        # print('Vendor       : %s' % glGetString(GL_VENDOR))
        # print('OpenGL Vers. : %s' % glGetString(GL_VERSION))
        # print('GLSL Vers.   : %s' % glGetString(GL_SHADING_LANGUAGE_VERSION))
        # print('Renderer     : %s' % glGetString(GL_RENDERER))

        # set background color to black
        glClearColor(0, 0, 0, 0)        # white: 1, 1, 1, 1

        # Enable depthtest
        glEnable(GL_DEPTH_TEST)

    def zoom_in_object(self, zoomFactor):
        """Reinzoomen um Faktor"""
        if (self.scene.fovy - zoomFactor > 0):
            self.scene.fovy -= zoomFactor
        self.scene.draw()

    def zoom_out_object(self, zoomFactor):
        """Rauszoomen um Faktor"""
        self.scene.fovy += zoomFactor
        self.scene.draw()

    def on_mouse_scroll_zoom(self, win, xOffset, yOffset):
        if (yOffset == -1):
            self.zoom_out_object(1)
        else:
            self.zoom_in_object(1)

    def calc_p1_archball(self, x, y):
        px, py, pz = scene.projectOnSphere(x, y, 1000)
        scene.p1_arcball = np.array([px, py, pz])
        scene.p1_arcball /= np.linalg.norm(scene.p1_arcball)
        return scene.p1_arcball
    
    def calc_p2_archball(self, x, y):
        px, py, pz = scene.projectOnSphere(x, y, 1000)
        scene.p2_arcball = np.array([px, py, pz])
        scene.p2_arcball /= np.linalg.norm(scene.p2_arcball)
        return scene.p2_arcball

    def on_mouse_pressed(self, window, button, action, mods):
        if button == glfw.MOUSE_BUTTON_RIGHT:  # horizontal verschieben
            x, y = glfw.get_cursor_pos(window)
            if action == glfw.PRESS:
                scene.prev_mouse_pos = x

        if button == glfw.MOUSE_BUTTON_LEFT:   # archball drehen
            if action == glfw.PRESS:
                x, y = glfw.get_cursor_pos(window)
                scene.p1_arcball = self.calc_p1_archball(x, y)
                scene.first_click_done = True   # 1. Klick sich merken
            
            if action == glfw.RELEASE:
                scene.first_click_done = False  # Rotieren über Archball ist fertig

    def on_mouse_move(self, window, x, y):
        """Aufgerufen während sich die Mouse noch bewegt (davor: 1. Klick, danach: loslassen)"""
        if scene.first_click_done:
            # updaten der aktuellen Position beim Maus bewegen
            px, py, pz = scene.projectOnSphere(x, y, 1000)
            scene.p2_arcball = self.calc_p2_archball(x, y)
            normal_p1_p2 = np.cross(scene.p1_arcball, scene.p2_arcball)

            if not np.allclose(normal_p1_p2, [0, 0, 0]):       # zum rotieren 1x Rotationswinkel und 1x Rotationsmatrix berechnen (rotation_direction_vector, rotation_angle)
                # Rotationsrichtung berechnen
                scene.rotation_direction_vector = normal_p1_p2
                dot_product = np.dot(scene.p1_arcball, scene.p2_arcball)
                # Rotationswinkel berechnen
                scene.rotation_angle = np.arccos(dot_product) * 10
                scene.p2_arcball = np.array([px, py, pz])
                # rotieren
                scene.rotation_model =  rotate(scene.rotation_angle, scene.rotation_direction_vector) @ scene.rotation_model

    def on_keyboard_pressed(self, win, key, scancode, action, mods):
        print("keyboard: ", win, key, scancode, action, mods)
        if action == glfw.PRESS:
            # ESC to quit
            if key == glfw.KEY_ESCAPE:
                self.exitNow = True
            if key == glfw.KEY_A:
                scene.animate = not scene.animate
            if key == glfw.KEY_P:
                scene.projection = not scene.projection
                print("toggle projection: orthographic / perspective ")
            if key == glfw.KEY_S:
                # TODO:
                print("toggle shading: wireframe, grouraud, phong")
            if key == glfw.KEY_X:
                scene.angleX += scene.angle_rotation_increment
                scene.draw()
                print("rotate: around x-axis")
            if key == glfw.KEY_Y:
                scene.angleY += scene.angle_rotation_increment
                scene.draw()
                print("rotate: around y-axis")
            if key == glfw.KEY_Z:
                scene.angleZ += scene.angle_rotation_increment
                scene.draw()
                print("rotate: around z-axis")
            #zoom statt mit Maus auch mit N und M möglich
            if key == glfw.KEY_N:
                self.zoom_in_object(5)
            if key == glfw.KEY_M:
                self.zoom_out_object(5)

    def on_size(self, win, width, height):
        scene.set_size(width, height)

    def run(self):
        while not glfw.window_should_close(self.window) and not self.exitNow:
            # poll for and process events
            glfw.poll_events()

            # setup viewport
            width, height = glfw.get_framebuffer_size(self.window)
            glViewport(0, 0, width, height)

            # Update the scene based on mouse movement
            scene.draw_scene_when_mouse_used(self.window)

            # call the rendering function
            scene.draw()

            # swap front and back buffer
            glfw.swap_buffers(self.window)

        # end
        glfw.terminate()


# main function
if __name__ == '__main__':
    print("presse 'a' to toggle animation...")

    # set size of render viewport
    width, height = 640, 480

    # instantiate a scene
    scene = Scene(width, height)

    # pass the scene to a render window ...
    rw = RenderWindow(scene)

    # ... and start main loop
    rw.run()
