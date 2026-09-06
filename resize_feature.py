from PIL import Image
import sys

img = Image.open('C:/Users/cw_66/AndroidStudioProjects/SanitovaCheck/brand-assets/feature_graphic_raw.png')
img_resized = img.resize((1024, 500), Image.LANCZOS)
img_resized.save('C:/Users/cw_66/AndroidStudioProjects/SanitovaCheck/brand-assets/feature_graphic.png', 'PNG')
print('Resized to 1024x500')
