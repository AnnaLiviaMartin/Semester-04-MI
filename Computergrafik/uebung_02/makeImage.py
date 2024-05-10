from PIL import Image


def create_gray_image(width, height):
    # Erzeuge ein neues Bildobjekt mit grauer Farbe
    gray_color = (128, 128, 128)  # Mittleres Grau
    image = Image.new("RGB", (width, height), gray_color)

    # Speichere das Bild
    image.save("gray_image.png")

def pixelsForImage(width, height):
    image = Image.new("RGB", (width, height))
    gray_color = (128, 128, 128)  # Mittleres Grau

    for x in range(width):
        for y in range(height):
            image.putpixel((x, y), gray_color)

    image.save("gray_image.png")


# Aufruf der Funktion, um ein graues Bild zu erstellen
#create_gray_image(400, 400)
pixelsForImage(400, 400)