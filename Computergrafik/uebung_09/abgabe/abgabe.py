import glfw
import numpy as np
import moderngl as mgl
import os

class Scene:
    def __init__(self, width, height, scene_title="2D Scene"):
        self.width = width
        self.height = height
        self.scene_title = scene_title
        self.points = []        # controlpoints
        self.ctx = None
        self.bg_color = (0.1, 0.1, 0.1)
        self.point_size = 7
        self.point_color = (1.0, 0.5, 0.5)
        self.line_color = (0.5, 0.5, 1.0)

        self.elementList = []
        self.order_slider_scale = None
        self.numpoints_slider_scale = None
        self.order = 1
        self.numpoints = 1

    def init_gl(self, ctx):
        self.ctx = ctx
        self.shader = ctx.program(
            vertex_shader="""
                #version 330
                uniform mat4 m_proj;
                uniform int m_point_size;
                uniform vec3 color;
                in vec2 v_pos;
                out vec3 f_col;
                void main() {
                    gl_Position = m_proj * vec4(v_pos, 0.0, 1.0);
                    gl_PointSize = m_point_size;
                    f_col = color;
                }
            """,
            fragment_shader="""
                #version 330
                in vec3 f_col;
                out vec4 color;
                void main() {
                    color = vec4(f_col, 1.0);
                }
            """
        )
        self.shader['m_point_size'] = self.point_size
        self.update_projection()

    def update_projection(self):
        """called when resizing window - calculating new projection"""
        m_proj = np.array([
            [2 / self.width, 0, 0, -1],
            [0, -2 / self.height, 0, 1],
            [0, 0, -1, 0],
            [0, 0, 0, 1]
        ], dtype=np.float32)
        m_proj = np.ascontiguousarray(m_proj.T)
        self.shader['m_proj'].write(m_proj)

    def resize(self, width, height):
        """called when resizing window"""
        self.width = width
        self.height = height
        self.update_projection()

    def add_point(self, point):
        """add point on click"""
        self.points.append(point)

    def clear(self):
        """clear list of points"""
        self.points = []

    def render(self):
        """ draw (control-)polygon connecting (control-)points """
        self.ctx.clear(*self.bg_color)
        if len(self.points) > 0:
            vbo = self.ctx.buffer(np.array(self.points, np.float32))
            vao = self.ctx.vertex_array(self.shader, [(vbo, '2f', 'v_pos')])
            self.shader['color'] = self.line_color
            vao.render(mgl.LINE_STRIP)
            self.shader['color'] = self.point_color
            vao.render(mgl.POINTS)

class RenderWindow:
    def __init__(self, scene):
        self.scene = scene
        glfw.init()
        self.window = glfw.create_window(scene.width, scene.height, scene.scene_title, None, None)
        glfw.make_context_current(self.window)
        self.ctx = mgl.create_context()
        self.ctx.enable(mgl.PROGRAM_POINT_SIZE)
        self.scene.init_gl(self.ctx)
        glfw.set_mouse_button_callback(self.window, self.onMouseButton)
        glfw.set_key_callback(self.window, self.onKeyboard)
        glfw.set_window_size_callback(self.window, self.onSize)
        self.exitNow = False

    def onMouseButton(self, win, button, action, mods):
        if action == glfw.PRESS:
            x, y = glfw.get_cursor_pos(win)
            self.scene.add_point([int(x), int(y)])

    def onKeyboard(self, win, key, scancode, action, mods):
        if action == glfw.PRESS:
            if key == glfw.KEY_ESCAPE:
                self.exitNow = True
            elif key == glfw.KEY_C:
                self.scene.clear()
            elif key == glfw.KEY_K:
                print("Ordnung erhöhen")
            elif key == glfw.KEY_L:
                print("Ordnung erniedrigen")
            elif key == glfw.KEY_M:
                print("Anzahl zu berechnender Kurvenpunkte erhöhen")
            elif key == glfw.KEY_N:
                print("Anzahl zu berechnender Kurvenpunkte niedriger")

    def onSize(self, win, width, height):
        self.width = width
        self.height = height
        self.ctx.viewport = (0, 0, width, height)
        self.scene.resize(width, height)

    def run(self):
        while not glfw.window_should_close(self.window) and not self.exitNow:
            glfw.poll_events()
            self.scene.render()
            glfw.swap_buffers(self.window)
        glfw.terminate()

if __name__ == '__main__':
    print("abgabe deBoor.py")
    print("pressing 'C' should clear everything")
    print("K: Ordnung erhöhen")
    print("L: Ordnung erniedrigen")
    print("M: Anzahl zu berechnender Kurvenpunkte erhöhen")
    print("N: Anzahl zu berechnender Kurvenpunkte niedriger")

    scene = Scene(640, 480, "deBoor")
    rw = RenderWindow(scene)
    rw.run()
