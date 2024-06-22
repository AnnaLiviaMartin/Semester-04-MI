import re
import numpy as np


def calculate_center_for_object(vertices):
    total_x = 0
    total_y = 0
    total_z = 0
    amount = len(vertices)
    # Schwerpunkt der Figur
    for vertice in vertices:
        total_x += vertice[0]
        total_y += vertice[1]
        total_z += vertice[2]

    return [total_x/amount, total_y/amount, total_z/amount]


def translate_obj_to_center(vertices, center):
    moved_vector_x = -center[0]
    moved_vector_y = -center[1]
    moved_vector_z = -center[2]

    for i in range(len(vertices)):
        vertice = vertices[i]
        x_moved = vertice[0] - moved_vector_x
        y_moved = vertice[1] - moved_vector_y
        z_moved = vertice[2] - moved_vector_z
        vertices[i] = [x_moved, y_moved, z_moved]

    return vertices


def load_from_file(filename):
    vertices = []
    faces = []
    normals = []
    has_normals = False

    with open(filename, 'r') as file:
        for line in file:
            if line.startswith('v '):  # vertices (Punkte)
                vertex = list(map(float, line.strip().split()[1:]))
                vertices.append(vertex)
            elif line.startswith('f '):  # faces (Polygon)
                face = line.strip().split()[1:]
                face_indices = [int(index.split('/')[0]) - 1 for index in face]
                faces.append(face_indices)
            elif line.startswith('vn '):
                normal = list(map(float, line.strip().split()[1:]))
                normals.append(normal)
                has_normals = True

    #center = calculate_center_for_object(vertices)
    #vertices = translate_obj_to_center(vertices, center)

    return vertices, faces, normals, has_normals


def compute_faces(faces):
    pass


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

        P1_normal = calc_normal(P1, P2, P3)  # Normale

        normals_for_faces.append([P1_normal])

    return normals_for_faces


def compute_normals_original(vertices, faces):
    normals = np.zeros((len(vertices), 3), dtype=np.float32)
    for face in faces:
        face_vertices = [vertices[int(f.split('/')[0]) - 1] for f in face]
        v1, v2, v3 = face_vertices[:3]
        normal = np.cross(np.array(v2) - np.array(v1), np.array(v3) - np.array(v1))
        normal = normal / np.linalg.norm(normal)
        for f in face:
            vertex_index = int(f.split('/')[0]) - 1
            normals[vertex_index] += normal
    normals = [list(normal / np.linalg.norm(normal)) for normal in normals]
    return normals


def loadMesh(filepath):
    v = []
    vt = []
    vn = []
    vertices = []
    hasNormals = False

    with open(filepath, 'r') as f:
        line = f.readline()
        while line:
            firstSpace = line.find(" ")
            flag = line[0:firstSpace]

            if flag == "v":
                line = line.replace("v ", "").split()
                # x, y, z as list in v
                v.append([float(x) for x in line])
            elif flag == "vt":
                line = line.replace("vt ", "").split()
                # s, t as list in vt
                vt.append([float(x) for x in line])
            elif flag == "vn":
                line = line.replace("vn ", "").split()
                # nx, ny, nz as list in vn
                vn.append([float(x) for x in line])
                hasNormals = True
            elif flag == "f":
                # face, three vertices -> v/vt/vn or v//vn
                line = line.replace("f ", "").replace("\n", "").split()
                # [../../.., ../../.., ../../..]
                faceVertices = []
                faceTextures = []
                faceNormals = []
                # ../../.. or ..//.. or .. .. ..
                for ele in line:
                    if "//" in ele:
                        indices = ele.split("//")
                        position = int(indices[0]) - 1
                        faceVertices.append(v[position])
                        position = int(indices[1]) - 1
                        faceNormals.append(vn[position])
                    elif "/" in ele:
                        indices = ele.split("/")
                        position = int(indices[0]) - 1
                        faceVertices.append(v[position])
                        position = int(indices[1]) - 1
                        faceTextures.append(vt[position])
                        position = int(indices[2]) - 1
                        faceNormals.append(vn[position])
                    else:
                        indices = ele.split(" ")
                        position = int(indices[0]) - 1
                        faceVertices.append(v[position])
                # unpack [0,1,2,3] into [0,1,2,0,2,3]
                trianglesInFace = len(line) - 2
                vertexOrder = []
                for i in range(trianglesInFace):
                    vertexOrder.append(0)
                    vertexOrder.append(i + 1)
                    vertexOrder.append(i + 2)
                for i in vertexOrder:
                    for x in faceVertices[i]:
                        vertices.append(x)
                    #for x in faceTextures[i]:
                        #vertices.append(x)
                    for x in faceNormals[i]:
                        vertices.append(x)
            line = f.readline()
    return v, vt, vn, vertices, hasNormals

def main():
    input_file = './models/cow.obj'

    vertices, faces = load_from_file(input_file)
    normals = compute_normals(vertices, faces)
