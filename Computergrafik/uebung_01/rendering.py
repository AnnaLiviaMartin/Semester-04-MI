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
import OpenGL.GL as gl
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
                polygon_A           = np.array([0, 1]),
                polygon_B           = np.array([1, 0]),
                scene_title         = "2D Scene",
                interpolation_fct   = None):

        self.width              = width
        self.height             = height
        self.polygon_A          = polygon_A
        self.polygon_B          = polygon_B
        self.scene_title        = scene_title
        self.interpolation_fct  = interpolation_fct

        # Animation
        self.t                  = 0
        self.dt                 = 0.01
        self.forward_animation  = False
        self.backward_animation = False
        self.rendered_polygon   = polygon_A

        # Rendering
        self.ctx                = None              # Assigned when calling init_gl()
        self.bg_color           = (0.1, 0.1, 0.1)
        self.point_size         = 7
        self.point_color        = (1.0, 0.5, 0.5)
        self.line_width         = 1
        self.line_color         = (0.5, 0.5, 1.0)


    def init_gl(self, ctx):
        self.ctx        = ctx
        ctx.point_size  = self.point_size
        ctx.line_width  = self.line_width
        ctx.viewport    = (0, 0, self.width, self.height)

        # Create Shaders
        self.shader = ctx.program(
            vertex_shader = """
                #version 330

                uniform mat4 m_proj;
                in vec2 vert;

                void main() {
                    gl_Position = m_proj * vec4(vert, 0.0, 1.0);
                }
            """,
            fragment_shader = """
                #version 330

                uniform vec3 in_color;
                out vec4 color;

                void main() {
                    color = vec4(in_color, 1.0);
                }
            """
        )

        # Set projection matrix
        l, r = -self.width, self.width
        b, t = -self.height, self.height
        n, f = -2, 2
        m_proj = np.array([
            [2/(r-l),   0,          0,          -(l+r)/(r-l)],
            [0,         2/(t-b),    0,          -(b+t)/(t-b)],
            [0,         0,          -2/(f-n),    -(n+f)/(f-n)],
            [0,         0,          0,          1]
        ], dtype=np.float32)
        m_proj = np.ascontiguousarray(m_proj.T)
        self.shader['m_proj'].write(m_proj)


    def animation(self):
        if self.forward_animation:
            self.t += self.dt
            if self.t>=1:
                self.t = 1
                self.forward_animation = False

        if self.backward_animation:
            self.t -= self.dt
            if self.t<=0:
                self.t = 0
                self.backward_animation = False

        # Use the callback function to interpolate between the polygons (if given)
        if self.interpolation_fct is not None:
            self.rendered_polygon = self.interpolation_fct(self.polygon_A, self.polygon_B, self.t)


    def render(self):
        self.animation()

        # Fill Background
        self.ctx.clear(*self.bg_color)

        # Create necessary modernGL objects
        vbo = self.ctx.buffer(self.rendered_polygon.astype(np.float32))
        vao = self.ctx.vertex_array(self.shader, [(vbo, '2f', 'vert')])

        # Render Lines
        self.shader['in_color'] = self.line_color
        vao.render(mgl.LINE_LOOP)

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
        glfw.window_hint(glfw.OPENGL_FORWARD_COMPAT, gl.GL_TRUE)

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
            # ESC to quit
            if key == glfw.KEY_ESCAPE:
                self.exitNow = True
            if key == glfw.KEY_F:
                # Forward animation
                self.scene.backward_animation   = False
                self.scene.forward_animation    = True
            if key == glfw.KEY_B:
                # Backward animation
                self.scene.forward_animation    = False
                self.scene.backward_animation   = True


    def onSize(self, win, width, height):
        #print("onsize: ", win, width, height)
        self.width          = width
        self.height         = height
        self.ctx.viewport   = (0, 0, self.width, self.height)


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
                if imgui.button("Forward (F)"):
                    self.scene.backward_animation   = False
                    self.scene.forward_animation    = True
                if imgui.button("Backward (B)"):
                    self.scene.forward_animation    = False
                    self.scene.backward_animation   = True
                _, self.scene.t = imgui.slider_float("t", self.scene.t, 0, 1)

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
