import sys
import glfw
import numpy as np

from mat4 import *
from OpenGL.GL import *
from OpenGL.arrays.vbo import VBO
from OpenGL.GL.shaders import *

EXIT_FAILURE = -1
EXIT_SUCCESS = 1

shader_program  = None
vertex_array    = None
window          = None
angle           = 0
angle_increment = 1
width, height   = 0, 0


def main():
    global width, height, window
    # initialize GLFW
    if not glfw.init():
        sys.exit(EXIT_FAILURE)

    # request window with old OpenGL 3.2
    glfw.window_hint(glfw.OPENGL_PROFILE, glfw.OPENGL_CORE_PROFILE)
    glfw.window_hint(glfw.OPENGL_FORWARD_COMPAT, glfw.TRUE)
    glfw.window_hint(glfw.CONTEXT_VERSION_MAJOR, 3)
    glfw.window_hint(glfw.CONTEXT_VERSION_MINOR, 2)

    window = glfw.create_window(640, 480, "Hello Triangle", None, None)
    if not window:
        glfw.terminate()
        sys.exit(EXIT_FAILURE)

    # Make the window's context current
    glfw.make_context_current(window)

    # register our keyboard callback
    glfw.set_key_callback(window, on_keyboard)

    # initialize opengl
    initialize()

    # run event loop
    while not glfw.window_should_close(window):
        # setup viewport
        width, height = glfw.get_framebuffer_size(window)
        glViewport(0, 0, width, height)

        # call the rendering function
        draw()

        # swap front and back buffer
        glfw.swap_buffers(window)
        
        # Poll for and process events
        glfw.poll_events()

    # finish application
    glDeleteBuffers(1, pos_buffer)
    glDeleteBuffers(1, col_buffer)
    glDeleteVertexArrays(1, vertex_array)
    glfw.destroy_window(window)
    glfw.terminate()
    sys.exit(EXIT_SUCCESS)


def on_keyboard(win, key, scancode, action, mods):
    global angle_increment
    if action == glfw.PRESS:
        # ESC to quit application
        if key == glfw.KEY_ESCAPE:
            glfw.set_window_should_close(window, glfw.TRUE)
        # increase angle increment
        if key == glfw.KEY_RIGHT:
            angle_increment *= 2.0
        # decrease angle increment
        if key == glfw.KEY_LEFT:
            angle_increment *= 0.5


def draw():
    global angle
    #clear framebuffer
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)

    # increment rotation angle in each frame
    angle += angle_increment
 
    # setup matrices
    projection = perspective(60.0, width/height, 1.0, 5.0)
    view       = look_at(0,0,2, 0,0,0, 0,1,0)
    model      = rotate_y(angle)
    mvp_matrix = projection @ view @ model

    # enable shader & set uniforms
    glUseProgram(shader_program)

    # determine location of uniform variable varName
    varLocation = glGetUniformLocation(shader_program, 'modelview_projection_matrix')
	# pass value to shader
    glUniformMatrix4fv(varLocation, 1, GL_TRUE, mvp_matrix)

    # enable vertex array & draw triangle(s)
    glBindVertexArray(vertex_array)
    glDrawArrays(GL_TRIANGLES, 0, 3)

    # Lets unbind the shader and vertex array state
    glUseProgram(0)
    glBindVertexArray(0)

	
def initialize():
    global vertex_array, pos_buffer, col_buffer, shader_program
    # debug: print GL and GLS version
    # print('Vendor         : %s' % glGetString(GL_VENDOR))
    # print('Opengl version : %s' % glGetString(GL_VERSION))
    # print('GLSL Version   : %s' % glGetString(GL_SHADING_LANGUAGE_VERSION))
    # print('Renderer       : %s' % glGetString(GL_RENDERER))

    # set background color to black
    glClearColor(0, 0, 0, 0)     
  
    # generate vertex array object
    vertex_array = glGenVertexArrays(1)
    glBindVertexArray(vertex_array)
 
    # generate and fill buffer with vertex positions (attribute 0)
    positions = np.array([0.0,  0.58, 0.0,   
                         -0.5, -0.29, 0.0,   
                          0.5, -0.29, 0.0], dtype=np.float32)
    pos_buffer = glGenBuffers(1)
    glBindBuffer(GL_ARRAY_BUFFER, pos_buffer)
    size_of_float = 4
    glBufferData(GL_ARRAY_BUFFER, 9*size_of_float, positions, GL_STATIC_DRAW)
    glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 0, None)
    glEnableVertexAttribArray(0)
 
    # generate and fill buffer with vertex colors (attribute 1)
    colors = np.array([1.0, 0.0, 0.0,  
                        0.0, 1.0, 0.0,  
                        0.0, 0.0, 1.0
                        ], dtype=np.float32)
    col_buffer = glGenBuffers(1)
    glBindBuffer(GL_ARRAY_BUFFER, col_buffer)
    glBufferData(GL_ARRAY_BUFFER, 9*size_of_float, colors, GL_STATIC_DRAW)
    glVertexAttribPointer(1, 3, GL_FLOAT, GL_FALSE, 0, None)
    glEnableVertexAttribArray(1)
 
    # setup shader
    vertex_shader = open("shader.vert","r").read()
    fragment_shader = open("shader.frag","r").read()
    vertex_prog = compileShader(vertex_shader, GL_VERTEX_SHADER)
    frag_prog = compileShader(fragment_shader, GL_FRAGMENT_SHADER)
    shader_program = compileProgram(vertex_prog, frag_prog)

    glBindBuffer(GL_ARRAY_BUFFER, 0)
    glBindVertexArray(0)



if __name__ == "__main__":
    main()
