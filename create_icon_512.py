from PIL import Image

img = Image.open('brand-assets/app_icon_3d_final.png')
icon = img.resize((512, 512), Image.LANCZOS)
icon.save('brand-assets/app_icon_512.png', 'PNG')
print('512x512 app icon saved to brand-assets/app_icon_512.png')
