from PIL import Image
import os

img = Image.open('brand-assets/app_icon_3d_final.png')
# Use RGBA if the image has transparency, otherwise RGB
if img.mode not in ('RGBA', 'RGB'):
    img = img.convert('RGBA')

sizes = {
    'mipmap-mdpi': 48,
    'mipmap-hdpi': 72,
    'mipmap-xhdpi': 96,
    'mipmap-xxhdpi': 144,
    'mipmap-xxxhdpi': 192
}

for folder, size in sizes.items():
    resized = img.resize((size, size), Image.LANCZOS)
    # Convert to RGB if saving as PNG without alpha to avoid issues
    if resized.mode == 'RGBA':
        # Keep RGBA for PNG
        pass
    resized.save(f'app/src/main/res/{folder}/ic_launcher.png', 'PNG')
    resized.save(f'app/src/main/res/{folder}/ic_launcher_round.png', 'PNG')
    print(f'Written {folder} ({size}x{size})')

print('All launcher icons updated with 3D design.')
