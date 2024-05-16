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
                ray_tracer,
                scene_title         = "2D Scene"):

        self.width              = width
        self.height             = height
        self.scene_title        = scene_title

        # Scene specific
        self.ray_tracer         = ray_tracer
        self.gl_texture         = None

        # Rendering
        self.ctx                = None              # Assigned when calling init_gl()
        self.point_size         = 1
        self.bg_color           = (0.1, 0.1, 0.1)


    def init_gl(self, ctx):
        self.ctx        = ctx

        # Create Shaders
        self.shader = ctx.program(
            vertex_shader = """
                #version 330

                uniform mat4    m_proj;
                uniform int     m_point_size;

                in      vec2    vert;
                in      vec2    tex_coords;

                out     vec2    tex_coords_f;

                void main() {
                    gl_Position     = m_proj * vec4(vert, 0.0, 1.0);
                    gl_PointSize    = m_point_size;
                    tex_coords_f    = tex_coords;
                }
            """,
            fragment_shader = """
                #version 330

                uniform usampler2D  tex;

                in      vec2        tex_coords_f;

                out     vec3        color;

                void main() {
                    color = texture(tex, tex_coords_f).rgb / 255.0;
                }
            """
        )
        self.shader['m_point_size'] = self.point_size

        # Set Texture unit
        self.shader['tex'].value = 0

        # Initialize a moderngl_texture object that can be written and store the ray traced results
        self.initialize_gl_texture()
        self.update_ray_tracer_image()

        # Set projection matrix
        l, r = -1, 1
        b, t = -1, 1
        n, f = -2, 2
        m_proj = np.array([
            [2/(r-l),   0,          0,          -(l+r)/(r-l)],
            [0,         2/(t-b),    0,          -(b+t)/(t-b)],
            [0,         0,          -2/(f-n),    -(n+f)/(f-n)],
            [0,         0,          0,          1]
        ], dtype=np.float32)
        m_proj = np.ascontiguousarray(m_proj.T)
        self.shader['m_proj'].write(m_proj)

        # Construct simple a simple unit plane, map texture coordinates to it and store it on the GPU
        vertices        = [(-1, -1), (-1, 1), (1, 1), (1, -1)]
        texture_coords  = [(0, 0), (0, 1), (1, 1), (1, 0)]
        triangles       = [(0, 1, 2), (0, 2, 3)]
        vertex_data     = np.array([vertices[i] for tri in triangles for i in tri])
        texture_data    = np.array([texture_coords[i] for tri in triangles for i in tri])
        combined_data   = np.hstack([vertex_data, texture_data]).ravel()
        combined_data   = np.ascontiguousarray(combined_data.astype(np.float32))
        vbo             = self.ctx.buffer(combined_data)
        self.vao        = self.ctx.vertex_array(self.shader, [(vbo, '2f 2f', *'vert tex_coords'.split())])


    def initialize_gl_texture(self):
        """
        Creates a moderngl texture object. Note that a 1Byte-RGB data format is used.
        """

        # Release previous Texture (i.e. after resize)
        if self.gl_texture is not None:
            self.gl_texture.release()

        image_data          = np.zeros((self.height, self.width, 3), dtype=np.uint8)
        gl_texture          = self.ctx.texture((self.width, self.height), components=3, data=image_data, dtype='u1')
        gl_texture.repeat_x = False
        gl_texture.repeat_y = False
        self.gl_texture     = gl_texture



    def resize(self, width, height):
        self.width  = width
        self.height = height

        # Prepaire gl_texture for a new texture size
        self.initialize_gl_texture()

        # Ask ray tracer to resize
        self.ray_tracer.resize(width, height)

        self.update_ray_tracer_image()


    def update_ray_tracer_image(self):

        # Get Image fram Ray Tracer and write it to the GPU
        image = self.ray_tracer.render()

        # Flip y-axis (OpenGL y-Axis starts at Bottom)
        image = np.flip(image, 0)

        # Re-Write and Re-Bind texture
        self.gl_texture.write(np.ascontiguousarray(image))


    def render(self):

        # Fill Background
        self.ctx.clear(*self.bg_color)

        self.gl_texture.use(0)

        # Render Mesh
        self.vao.render(mgl.TRIANGLES)





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
            # ESC to quit
            if key == glfw.KEY_ESCAPE:
                self.exitNow = True
            if key == glfw.KEY_N:
                self.scene.ray_tracer.rotate_neg()
                self.scene.update_ray_tracer_image()
            if key == glfw.KEY_P:
                self.scene.ray_tracer.rotate_pos()
                self.scene.update_ray_tracer_image()


    def onSize(self, win, width, height):
        #print("onsize: ", win, width, height)
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

                if imgui.button("Rotate + (p)"):
                    self.scene.ray_tracer.rotate_pos()
                    self.scene.update_ray_tracer_image()

                if imgui.button("Rotate - (n)"):
                    self.scene.ray_tracer.rotate_neg()
                    self.scene.update_ray_tracer_image()

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
