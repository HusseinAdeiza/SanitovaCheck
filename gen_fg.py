from PIL import Image
import os

src = r"C:/Users/cw_66/AndroidStudioProjects/SanitovaCheck/brand-assets/app_icon_3d_final.png"
out = r"C:/Users/cw_66/AndroidStudioProjects/SanitovaCheck/app/src/main/res/drawable/ic_launcher_foreground_img.png"

img = Image.open(src).convert("RGBA")
size = 288
img.thumbnail((size, size), Image.LANCZOS)
canvas = Image.new("RGBA", (size, size), (0, 0, 0, 0))
x = (size - img.width) // 2
y = (size - img.height) // 2
canvas.paste(img, (x, y), img)
canvas.save(out, "PNG")
print(f"Saved foreground to {os.path.basename(out)}")
