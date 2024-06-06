import re
import numpy as np


def parse_obj(filename):
    vertices = []
    textures = []
    normals = []
    faces = []
    has_texture = False
    has_normals = False
    max_corners = 0

    with open(filename, 'r') as file:
        for line in file:
            if line.startswith('v '):
                vertices.append(list(map(float, line.strip().split()[1:])))
            elif line.startswith('vt '):
                textures.append(list(map(float, line.strip().split()[1:])))
                has_texture = True
            elif line.startswith('vn '):
                normals.append(list(map(float, line.strip().split()[1:])))
                has_normals = True
            elif line.startswith('f '):
                face = line.strip().split()[1:]
                faces.append(face)
                max_corners = max(max_corners, len(face))

    return vertices, textures, normals, faces, has_texture, has_normals, max_corners


def compute_normals(vertices, faces):
    normals = np.zeros((len(vertices), 3), dtype=np.float32)
    for face in faces:
        face_vertices = [vertices[int(f.split('/')[0]) - 1] for f in face]
        v1, v2, v3 = face_vertices[:3]
        normal = np.cross(np.array(v2) - np.array(v1), np.array(v3) - np.array(v1))
        normal /= np.linalg.norm(normal)
        for f in face:
            vertex_index = int(f.split('/')[0]) - 1
            normals[vertex_index] += normal
    normals = [list(normal / np.linalg.norm(normal)) for normal in normals]
    return normals


def export_ply(filename, vertices, normals, faces):
    with open(filename, 'w') as file:
        file.write('ply\n')
        file.write('format ascii 1.0\n')
        file.write(f'element vertex {len(vertices)}\n')
        file.write('property float x\n')
        file.write('property float y\n')
        file.write('property float z\n')
        file.write('property float nx\n')
        file.write('property float ny\n')
        file.write('property float nz\n')
        file.write(f'element face {len(faces)}\n')
        file.write('property list uchar int vertex_indices\n')
        file.write('end_header\n')

        for vertex, normal in zip(vertices, normals):
            file.write(f'{" ".join(map(str, vertex))} {" ".join(map(str, normal))}\n')

        for face in faces:
            indices = [int(f.split('/')[0]) - 1 for f in face]
            file.write(f'3 {" ".join(map(str, indices))}\n')


def main():
    input_file = './meshes/cow.obj'
    output_file = 'output.ply'

    vertices, textures, normals, faces, has_texture, has_normals, max_corners = parse_obj(input_file)

    print(f"Datei enthält Texturkoordinaten: {'Ja' if has_texture else 'Nein'}")
    print(f"Datei enthält Normalen: {'Ja' if has_normals else 'Nein'}")
    print(f"Maximale Anzahl Ecken pro Polygon: {max_corners}")
    print(f"Anzahl Vertices: {len(vertices)}")
    print(f"Anzahl Faces: {len(faces)}")
    print(f"Anzahl Edges: {sum(len(face) for face in faces)}")

    if not has_normals:
        normals = compute_normals(vertices, faces)

    export_ply(output_file, vertices, normals, faces)


if __name__ == '__main__':
    main()

