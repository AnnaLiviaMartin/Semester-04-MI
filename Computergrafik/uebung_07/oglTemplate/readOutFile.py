import re
import numpy as np


def load_from_file(filename):
    vertices = []
    faces = []

    with open(filename, 'r') as file:
        for line in file:
            if line.startswith('v '):       # vertices (Punkte)
                vertices.append(np.array(list(map(float, line.strip().split()[1:]))))
            elif line.startswith('f '):     # faces (Polygon)
                faces.append(np.array(list(map(int, line.strip().split()[1:]))))

    return vertices, faces


def calc_normal(P1, P2, P3):
    """berechnet Normale für P1"""
    v1 = P2 - P1
    v2 = P3 - P1
    normal = np.cross(v1, v2)
    return normal / np.linalg.norm(normal)

def compute_normals(vertices, faces):
    normals_for_faces = []
    for face in faces:
        P1_index = face[0]
        P2_index = face[1]
        P3_index = face[2]
        P1 = vertices[P1_index - 1]
        P2 = vertices[P2_index - 1]
        P3 = vertices[P3_index - 1]

        P1_normal = calc_normal(P1, P2, P3)     # Normale für P1
        P2_normal = calc_normal(P2, P1, P3)     # Normale für P2
        P3_normal = calc_normal(P3, P2, P1)     # Normale für P3

        normals_for_faces.append([P1_normal, P2_normal, P3_normal])

    return normals_for_faces


def main():
    input_file = './models/cow.obj'

    vertices, faces = load_from_file(input_file)
    normals = compute_normals(vertices, faces)
