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
        self.ctx = None         # OpenGL context which must exists
        self.bg_color = (0.1, 0.1, 0.1)     # background color of window
        self.point_size = 5         # determines size of a point in pixel
        self.point_color = (1.0, 0.5, 0.5)      # color for points
        self.line_color = (0.5, 0.5, 1.0)       # color for line between lines
        self.b_spline_color = (0.5, 1.0, 0.5)       # b spline color
        self.order = 2      # order of the curve  
        self.numpoints = 10       # number of curve points to be calculated (for b spline curve)
        self.b_spline_point_size = 3    # point size of b spline points

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
        self.ctx.clear(*self.bg_color)

        # Draw control points and polygon
        if len(self.points) > 0:
            vbo = self.ctx.buffer(np.array(self.points, np.float32))
            vao = self.ctx.vertex_array(self.shader, [(vbo, '2f', 'v_pos')])
            
            self.shader['color'] = self.line_color
            vao.render(mgl.LINE_STRIP)
            
            self.shader['color'] = self.point_color
            vao.render(mgl.POINTS)

        # Draw b spline curve
        if len(self.points) >= self.order:
            # calculate b spline points
            curve_points = []
            knotvector = self.calc_T(self.points)
            for i in np.linspace(knotvector[0], knotvector[-1], self.numpoints):
                point = self.deboor(self.order - 1, np.array(self.points), knotvector, i)
                curve_points.append(point)

            vbo2 = self.ctx.buffer(np.array(curve_points, np.float32))
            vao2 = self.ctx.vertex_array(self.shader, [(vbo2, '2f', 'v_pos')])

            self.shader['color'] = self.b_spline_color      # set color of b spline
            #vao2.render(mgl.POINTS)        # show points of b spline
            #vao2.render(mgl.LINE_STRIP)     # show curve of points of b spline
            vao2.render(mgl.LINE_STRIP, vertices=len(curve_points) - 1)     # show curve of points of b spline without connection of first and last point


    # de Boor calculation
    def calc_T(self, points):
        """Calculated knotvector for b spline curve"""
        knotvector = []
        knotvector.extend([0 for x in range(self.order)])
        last_entry = len(points) - (self.order - 2)
        knotvector.extend([x for x in range(1, last_entry)])
        knotvector.extend([last_entry for x in range(self.order)])
        return knotvector
    
    def find_index(self, knotvector, t):
        """searches in a given node vector for the index of 
        the first node that is greater than t"""
        for i in range(len(knotvector)):
            if knotvector[i] > t:
                return i - 1
        return len(knotvector) - 1
    
    def calc_dist_factor(self, min_val, max_val, t):
        """describes the relative position of a point 
        within a certain interval"""
        return (t - min_val) / (max_val - min_val)

    def reduce(self, order, controlpoints, knotvector, r, t):
        """calculates a new point on the B-spline curve by 
        gradually reducing the control points"""
        if len(controlpoints) <= 1:
            return controlpoints

        interval = [knotvector[x] for x in range(r - order + 1, r + order + 1)]
        new_points = []

        for i in range(len(controlpoints) - 1):
            small_interval = interval[i : i + order + 1]
            dist = self.calc_dist_factor(small_interval[0], small_interval[-1], t)
            p = controlpoints[i] * (1 - dist) + controlpoints[i + 1] * dist
            new_points.append(p)

        return self.reduce(order - 1, new_points, knotvector, r, t)

    def deboor(self, order, controlpoints, knotvector, t):
        """calculate de boor"""
        r = self.find_index(knotvector, t)
        point_span = controlpoints[r - order : r - order + 1 + order]

        if len(point_span) > 0:
            result = self.reduce(order, point_span, knotvector, r, t)
            return result[0] if isinstance(result[0], np.ndarray) else result
        else:
            return self.points[0]

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
                if mods & glfw.MOD_SHIFT:
                    print("Shift + K: Ordnung erniedrigen")
                    if(self.scene.order < 2):
                        self.scene.order = 1
                    else:
                        self.scene.order -= 1
                    print("Ordnung: ", self.scene.order)
                else:
                    print("K: Ordnung erhöhen")
                    self.scene.order += 1
                    print("Ordnung: ", self.scene.order)
            elif key == glfw.KEY_M:
                if mods & glfw.MOD_SHIFT:
                    print("M: Anzahl zu berechnender Kurvenpunkte niedriger")
                    if(self.scene.numpoints < 2):
                        self.scene.numpoints = 1
                    else:
                        self.scene.numpoints -= 1
                    print("Anzahl Kurvenpunkte: ", self.scene.numpoints)
                else:
                    print("Shift + M: Anzahl zu berechnender Kurvenpunkte erhöhen")
                    self.scene.numpoints += 1
                    print("Anzahl Kurvenpunkte: ", self.scene.numpoints)

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
    print("Shift + K: Ordnung erniedrigen")
    print("M: Anzahl zu berechnender Kurvenpunkte erhöhen")
    print("Shift + M: Anzahl zu berechnender Kurvenpunkte niedriger")

    scene = Scene(640, 480, "deBoor")
    rw = RenderWindow(scene)
    rw.run()
