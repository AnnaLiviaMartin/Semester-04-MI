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
/**         rendering.py
 *
 *          This module is used to create a ModernGL context using GLFW.
 *          It provides the functionality necessary to execute and visualize code
 *          specified by students in the according template.
 *          ModernGL is a high-level modern OpenGL wrapper package.
 ****
"""

import glfw
import imgui
import numpy as np
import moderngl as mgl
import os

from imgui.integrations.glfw import GlfwRenderer


class Scene:
    """
        OpenGL 2D scene class
    """
    # initialization
    def __init__(self,
                width,
                height,
                points,
                vertex_transformation_pipeline,
                scene_title         = "2D Scene",
                interpolation_fct   = None):

        self.width              = width
        self.height             = height
        self.points             = points
        self.vertex_transformation_pipeline = vertex_transformation_pipeline
        self.scene_title        = scene_title

        # Rotation angles X/Y/Z
        self.alpha              = 0
        self.beta               = 0
        self.gamma              = 0
        self.proj_type          = 'orthographic'
        self.step               = 5               # Degree Offset when rotating

        # Animation
        self.t                  = 0
        self.dt                 = 0.01
        self.animate            = False

        # Rendering
        self.ctx                = None              # Assigned when calling init_gl()
        self.bg_color           = (0.1, 0.1, 0.1)
        self.point_size         = 3
        self.point_color        = (1.0, 0.5, 0.5)
        self.line_color         = (0.5, 0.5, 1.0)


    def init_gl(self, ctx):
        self.ctx        = ctx

        # Create Shaders
        self.shader = ctx.program(
            vertex_shader = """
                #version 150 core

                uniform mat4    m_proj;
                uniform int     m_point_size;

                in vec2 vert;

                void main() {
                    gl_Position     = m_proj * vec4(vert, 0.0, 1.0);
                    gl_PointSize    = m_point_size;
                }
            """,
            fragment_shader = """
                #version 150 core

                uniform vec3 in_color;
                out vec4 color;

                void main() {
                    color = vec4(in_color, 1.0);
                }
            """
        )
        self.shader['m_point_size'] = self.point_size

        # Set projection matrix
        l, r = 0, self.width
        b, t = 0, self.height
        n, f = -1000, 1000
        m_proj = np.array([
            [2/(r-l),   0,          0,          -(l+r)/(r-l)],
            [0,         2/(t-b),    0,          -(b+t)/(t-b)],
            [0,         0,          -2/(f-n),    -(n+f)/(f-n)],
            [0,         0,          0,          1]
        ], dtype=np.float32)
        m_proj = np.ascontiguousarray(m_proj.T)
        self.shader['m_proj'].write(m_proj)


    def resize(self, width, height):
        self.width  = width
        self.height = height

        # Set projection matrix
        l, r = 0, self.width
        b, t = 0, self.height
        n, f = -1000, 1000
        m_proj = np.array([
            [2/(r-l),   0,          0,          -(l+r)/(r-l)],
            [0,         2/(t-b),    0,          -(b+t)/(t-b)],
            [0,         0,          -2/(f-n),    -(n+f)/(f-n)],
            [0,         0,          0,          1]
        ], dtype=np.float32)
        m_proj = np.ascontiguousarray(m_proj.T)
        self.shader['m_proj'].write(m_proj)


    def animation(self):
        if self.animate:
            self.beta += self.step
            if self.beta > 360:
                self.beta = 0


    def render(self):
        self.animation()

        # Fill Background
        self.ctx.clear(*self.bg_color)

        # Apply Model View Projection and Viewport Transformation
        pix_coords = self.vertex_transformation_pipeline(self.points, self.alpha, self.beta, self.gamma, self.width, self.height, self.proj_type)
        pix_coords = np.ascontiguousarray(pix_coords)

        # Buffer Vertex Data on GPU
        vbo = self.ctx.buffer(pix_coords.astype(np.float32))
        vao = self.ctx.vertex_array(self.shader, [(vbo, '2f', 'vert')])

        # Render Points
        self.shader['in_color'] = self.point_color
        vao.render(mgl.POINTS)




class RenderWindow:
    """
        GLFW Rendering window class
        YOU SHOULD NOT EDIT THIS CLASS!
    """
    def __init__(self, scene):

        self.scene = scene

        # save current working directory
        cwd = os.getcwd()

        # Initialize the library
        if not glfw.init():
            return

        # restore cwd
        os.chdir(cwd)

        # buffer hints
        glfw.window_hint(glfw.DEPTH_BITS, 32)

        # define desired frame rate
        self.frame_rate = 60

        # OS X supports only forward-compatible core profiles from 3.2
        glfw.window_hint(glfw.CONTEXT_VERSION_MAJOR, 3)
        glfw.window_hint(glfw.CONTEXT_VERSION_MINOR, 3)
        glfw.window_hint(glfw.OPENGL_PROFILE, glfw.OPENGL_CORE_PROFILE)
        glfw.window_hint(glfw.OPENGL_FORWARD_COMPAT, True)
        glfw.window_hint(glfw.COCOA_RETINA_FRAMEBUFFER, False)

        # make a window
        self.width, self.height = scene.width, scene.height
        self.window = glfw.create_window(self.width, self.height, scene.scene_title, None, None)
        if not self.window:
            self.impl.shutdown()
            glfw.terminate()
            return

        # Make the window's context current
        glfw.make_context_current(self.window)

        # initializing imgui
        imgui.create_context()
        self.impl = GlfwRenderer(self.window)

        # set window callbacks
        glfw.set_mouse_button_callback(self.window, self.onMouseButton)
        glfw.set_key_callback(self.window, self.onKeyboard)
        glfw.set_window_size_callback(self.window, self.onSize)

        # create modernGL context and initialize GL objects in scene
        self.ctx = mgl.create_context()
        self.ctx.enable(flags=mgl.PROGRAM_POINT_SIZE)
        self.scene.init_gl(self.ctx)
        mgl.DEPTH_TEST = True

        # exit flag
        self.exitNow = False


    def onMouseButton(self, win, button, action, mods):
        # Don't react to clicks on UI controllers
        if not imgui.get_io().want_capture_mouse:
            #print("mouse button: ", win, button, action, mods)
            pass


    def onKeyboard(self, win, key, scancode, action, mods):
        #print("keyboard: ", win, key, scancode, action, mods)
        if action == glfw.PRESS:
            # press ESC to quit
            if key == glfw.KEY_ESCAPE:
                self.exitNow = True
            # press 'A' to toggle animation
            if key == glfw.KEY_A:
                self.scene.animate = not self.scene.animate
            if key == glfw.KEY_P:
                if self.scene.proj_type == 'orthographic':
                    self.scene.proj_type = 'perspective'
                else:
                    self.scene.proj_type = 'orthographic'
            if mods == glfw.MOD_SHIFT: # upper case keys
                if key == 88: #glfw.KEY_X:
                    # increase angle alpha (rotation around x-axis)
                    self.scene.alpha += self.scene.step
                if key == 90: #glfw.KEY_Y:
                    # increase angle beta (rotation around y-axis)
                    self.scene.beta += self.scene.step
                if key == 89: #glfw.KEY_Z:
                    # increase angle gamma (rotation around z-axis)
                    self.scene.gamma += self.scene.step
            else: # lower case keys
                if key == 88: #glfw.KEY_X:
                    # decrease angle alpha (rotation around x-axis)
                    self.scene.alpha -= self.scene.step
                if key == 90: #glfw.KEY_Y:
                    # decrease angle beta (rotation around y-axis)
                    self.scene.beta -= self.scene.step
                if key == 89: #glfw.KEY_Z:
                    # decrease angle gamma (rotation around z-axis)
                    self.scene.gamma -= self.scene.step


    def onSize(self, win, width, height):
        self.width          = width
        self.height         = height
        self.ctx.viewport   = (0, 0, self.width, self.height)
        self.scene.resize(width, height)


    def run(self):
        # initializer timer
        glfw.set_time(0.0)
        t = 0.0
        while not glfw.window_should_close(self.window) and not self.exitNow:
            # update every x seconds
            currT = glfw.get_time()
            if currT - t > 1.0 / self.frame_rate:
                # update time
                t = currT

                # == Frame-wise IMGUI Setup ===
                imgui.new_frame()                   # Start new frame context
                imgui.begin("Controller")     # Start new window context

                # Define UI Elements
                if imgui.button("Rotate X -"):
                    self.scene.alpha -= self.scene.step
                imgui.same_line()
                if imgui.button("Rotate X +"):
                    self.scene.alpha += self.scene.step
                if imgui.button("Rotate Y -"):
                    self.scene.beta -= self.scene.step
                imgui.same_line()
                if imgui.button("Rotate Y +"):
                    self.scene.beta += self.scene.step
                if imgui.button("Rotate Z -"):
                    self.scene.gamma -= self.scene.step
                imgui.same_line()
                if imgui.button("Rotate Z +"):
                    self.scene.gamma += self.scene.step
                if imgui.button("Toggle Animation (a)"):
                    self.scene.animate = not self.scene.animate
                if imgui.button("Change Projection (p)"):
                    if self.scene.proj_type == 'orthographic':
                        self.scene.proj_type = 'perspective'
                    else:
                        self.scene.proj_type = 'orthographic'

                imgui.end()                         # End window context
                imgui.render()                      # Run render callback
                imgui.end_frame()                   # End frame context
                self.impl.process_inputs()          # Poll for UI events

                # == Rendering GL ===
                glfw.poll_events()                  # Poll for GLFW events
                self.ctx.clear()                    # clear viewport
                self.scene.render()                 # render scene
                self.impl.render(imgui.get_draw_data()) # render UI
                glfw.swap_buffers(self.window)      # swap front and back buffer

        # end
        self.impl.shutdown()
        glfw.terminate()
