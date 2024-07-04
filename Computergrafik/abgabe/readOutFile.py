import re
import numpy as np

def calculate_center_for_object(vertices):
    total_x = 0
    total_y = 0
    total_z = 0
    amount_vertices = len(vertices)
    # Schwerpunkt der Figur
    for vertice in vertices:
        total_x += vertice[0]
        total_y += vertice[1]
        total_z += vertice[2]

    return [total_x/amount_vertices, total_y/amount_vertices, total_z/amount_vertices]


def translate_obj_to_center(vertices, center):
    for i in range(len(vertices)):
        vertice = vertices[i]
        x_moved = vertice[0] - center[0]
        y_moved = vertice[1] - center[1]
        z_moved = vertice[2] - center[2]
        vertices[i] = [x_moved, y_moved, z_moved]

    return vertices

def scale_into_box(vertices):
    # Größten Punkt bestimmen
    total_x = max(vertice[0] for vertice in vertices)
    total_y = max(vertice[1] for vertice in vertices)
    total_z = max(vertice[2] for vertice in vertices)
    greatest = max(total_x, total_y, total_z) * 1.6
    # alle Werte teilen
    for vertice in vertices:
        vertice[0] /= greatest
        vertice[1] /= greatest
        vertice[2] /= greatest

    return vertices


def load_from_file(filename):
    vertices = []
    faces = []
    normals = []
    colors = []
    has_normals = False

    with open(filename, 'r') as file:
        for line in file:
            if line.startswith('v '):  # vertices (Punkte)
                vertex = list(map(float, line.strip().split()[1:]))
                vertices.append(vertex)
                colors.append([1.0, 0.0, 0.0,
                           1.0, 0.0, 0.0,
                           1.0, 0.0, 0.0])
            elif line.startswith('f '):  # faces (Polygon)
                face = line.strip().split()[1:]
                face_indices = [int(index.split('/')[0]) - 1 for index in face]
                faces.append(face_indices)
            elif line.startswith('vn '):    # vertice normals (Normalen)
                normal = list(map(float, line.strip().split()[1:]))
                normals.append(normal)
                has_normals = True

    center = calculate_center_for_object(vertices)

    vertices = translate_obj_to_center(vertices, center)    # translate to center

    vertices = scale_into_box(vertices)     # put into box of size 0-1

    return vertices, faces, normals, has_normals, colors

def compute_normals(vertices, faces):
    # todo check
    normals = []
    for face in faces:
        p1_index, p2_index, p3_index = face
        P1 = vertices[p1_index]
        P2 = vertices[p2_index]
        P3 = vertices[p3_index]
        normal = calc_normal(P1, P2, P3)
        normals.append(normal)
    return normals

def calc_normal(P1, P2, P3):
    """Berechnet die Normale für das Dreieck definiert durch P1, P2, P3."""
    v1 = P2 - P1
    v2 = P3 - P1
    normal = np.cross(v1, v2)
    return np.array(normal / np.linalg.norm(normal))