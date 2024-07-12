import glfw
import moderngl as mgl
import numpy as np
import imgui
from imgui.integrations.glfw import GlfwRenderer

WIDTH = 400
HEIGHT = 400

HPSIZE = 2
CCOLOR = (0.0, 0.0, 1.0)  # Blue
BCOLOR = (0.0, 0.0, 0.0)  # Black
BWIDTH = 2

pointList = []
order = 2
numpoints = 100

def calc_T(points):
    knotvector = []
    knotvector.extend([0 for x in range(order)])
    last_entry = len(points) - (order - 2)
    knotvector.extend([x for x in range(1, last_entry)])
    knotvector.extend([last_entry for x in range(order)])
    return knotvector

def find_index(knotvector, t):
    for i in range(len(knotvector)):
        if knotvector[i] > t:
            return i - 1
    return len(knotvector) - 1

def calc_dist_factor(min_val, max_val, t):
    return (t - min_val) / (max_val - min_val)

def reduce(order, controlpoints, knotvector, r, t):
    if len(controlpoints) <= 1:
        return controlpoints

    interval = [knotvector[x] for x in range(r - order + 1, r + order + 1)]
    new_points = []

    for i in range(len(controlpoints) - 1):
        small_interval = interval[i : i + order + 1]
        dist = calc_dist_factor(small_interval[0], small_interval[-1], t)
        p = controlpoints[i] * (1 - dist) + controlpoints[i + 1] * dist
        new_points.append(p)

    return reduce(order - 1, new_points, knotvector, r, t)

def deboor(order, controlpoints, knotvector, t):
    r = find_index(knotvector, t)
    point_span = controlpoints[r - order : r - order + 1 + order]

    if len(point_span) > 0:
        result = reduce(order, point_span, knotvector, r, t)
        return result[0] if isinstance(result[0], np.ndarray) else result
    else:
        return pointList[0]


class Scene:
    def __init__(self, width, height):
        self.width = width
        self.height = height
        self.ctx = None
        self.prog = None
        self.vbo = None
        self.vao = None

    def init_gl(self, ctx):
        self.ctx = ctx
        self.prog = self.ctx.program(
            vertex_shader='''
                #version 330
                in vec2 in_vert;
                uniform mat4 proj;
                void main() {
                    gl_Position = proj * vec4(in_vert, 0.0, 1.0);
                }
            ''',
            fragment_shader='''
                #version 330
                uniform vec3 color;
                out vec4 f_color;
                void main() {
                    f_color = vec4(color, 1.0);
                }
            '''
        )
        self.vbo = self.ctx.buffer(reserve=WIDTH * HEIGHT * 8)
        self.vao = self.ctx.vertex_array(self.prog, [(self.vbo, '2f', 'in_vert')])

    def render(self):
        proj = np.eye(4, dtype='f4')
        proj[0, 0] = 2 / self.width
        proj[1, 1] = -2 / self.height
        proj[3, 0] = -1
        proj[3, 1] = 1

        self.ctx.clear(1.0, 1.0, 1.0)
        self.prog['proj'].write(proj)

        # Draw control points and polygon
        if len(pointList) > 0:
            self.prog['color'].write(np.array(CCOLOR, dtype='f4'))
            self.vbo.write(np.array(pointList, dtype='f4'))
            self.vao.render(mgl.POINTS, vertices=len(pointList))
            if len(pointList) > 1:
                self.vao.render(mgl.LINE_STRIP, vertices=len(pointList))

        # Draw B-spline curve
        if len(pointList) >= order:
            curve_points = []
            knotvector = calc_T(pointList)
            for i in np.linspace(knotvector[0], knotvector[-1], numpoints):
                point = deboor(order - 1, np.array(pointList), knotvector, i)
                curve_points.append(point)

            self.prog['color'].write(np.array(BCOLOR, dtype='f4'))
            self.vbo.write(np.array(curve_points, dtype='f4'))
            self.vao.render(mgl.LINE_STRIP, vertices=len(curve_points))


def main():
    global order, numpoints

    if not glfw.init():
        return

    window = glfw.create_window(WIDTH, HEIGHT, "B-Spline Curve", None, None)
    if not window:
        glfw.terminate()
        return

    glfw.make_context_current(window)

    ctx = mgl.create_context()
    scene = Scene(WIDTH, HEIGHT)
    scene.init_gl(ctx)

    imgui.create_context()
    impl = GlfwRenderer(window)

    def mouse_button_callback(window, button, action, mods):
        if button == glfw.MOUSE_BUTTON_LEFT and action == glfw.PRESS:
            x, y = glfw.get_cursor_pos(window)
            pointList.append([x, HEIGHT - y])

    glfw.set_mouse_button_callback(window, mouse_button_callback)

    while not glfw.window_should_close(window):
        glfw.poll_events()
        impl.process_inputs()

        imgui.new_frame()

        if imgui.begin_main_menu_bar():
            if imgui.begin_menu("File", True):
                clicked_quit, selected_quit = imgui.menu_item("Quit", 'Cmd+Q', False, True)
                if clicked_quit:
                    exit(1)
                imgui.end_menu()
            imgui.end_main_menu_bar()

        imgui.begin("Controls")
        changed, order = imgui.slider_int("Order", order, 2, 20)
        changed, numpoints = imgui.slider_int("Curve Points", numpoints, 1, 1000)
        if imgui.button("Clear"):
            pointList.clear()
        imgui.end()

        scene.render()

        imgui.render()
        impl.render(imgui.get_draw_data())

        glfw.swap_buffers(window)

    impl.shutdown()
    glfw.terminate()

if __name__ == "__main__":
    main()