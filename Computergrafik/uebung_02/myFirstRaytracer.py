import numpy as np
from PIL import Image

# image width , height
w, h = 600, 400

# generate a gray scale (one channel) noise image
gs_img1 = np.random.rand(w, h)
gs_img2 = np.random.rand(w, h)
gs_img3 = np.random.rand(w, h)

# scale image values to [0,250]
gs_img1 *= 255
gs_img2 *= 255
gs_img3 *= 255

# generate 8 Bit PIL image. these are transposed compared to numpy arrays
r = Image.fromarray(gs_img1.astype(np.uint8).T)
g = Image.fromarray(gs_img2.astype(np.uint8).T)
b = Image.fromarray(gs_img3.astype(np.uint8).T)

# make GRB image (here all three channels are the same)
rgb = [r, g, b]

# merge channels and save image
Image.merge("RGB", rgb).save("noise_image.png")

# the raytracer
'''objectlist = list()
maxdist = 0
hitdist = 0

def primary_ray(x, y):
    return 0

for x in range(image_width):
    for y in range(image_height):
        # generate primary ray through pixel (x,y)
        ray = primary_ray(x, y)
        maxdist = float('inf')
        color = BACKGROUND_COLOR
        for object in objectlist:
            hitdist = object.intersectionParameter(ray)
            if hitdist and hitdist < maxdist:
                maxdist = hitdist
                color = object.color_at(ray)
        image.putpixel((x,y), color)'''
