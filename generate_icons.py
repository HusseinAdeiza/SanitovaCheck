#!/usr/bin/env python3
"""Generate launcher icons from app_icon_3d_final.png for all densities."""
import os
from PIL import Image

# Paths
PROJECT_DIR = r"C:\Users\cw_66\AndroidStudioProjects\SanitovaCheck"
SOURCE_ICON = os.path.join(PROJECT_DIR, "brand-assets", "app_icon_3d_final.png")
MIPMAP_DIR = os.path.join(PROJECT_DIR, "app", "src", "main", "res")

# Sizes for each density (square launcher icon)
DENSITIES = {
    "mipmap-mdpi": 48,
    "mipmap-hdpi": 72,
    "mipmap-xhdpi": 96,
    "mipmap-xxhdpi": 144,
    "mipmap-xxxhdpi": 192,
}

# Water blue background color
WATER_BLUE = (0, 119, 182)  # #0077B6

def create_launcher_icon(size, source_img, round=False):
    """Create a launcher icon with water blue background and centered icon."""
    # Create background
    if round:
        bg = Image.new("RGBA", (size, size), (0, 0, 0, 0))
        from PIL import ImageDraw
        draw = ImageDraw.Draw(bg)
        draw.ellipse([0, 0, size - 1, size - 1], fill=WATER_BLUE + (255,))
    else:
        bg = Image.new("RGBA", (size, size), WATER_BLUE + (255,))
    
    # Resize source icon to fit with padding (about 70% of the size)
    icon_size = int(size * 0.7)
    icon = source_img.copy()
    icon.thumbnail((icon_size, icon_size), Image.LANCZOS)
    
    # Center the icon
    x = (size - icon.width) // 2
    y = (size - icon.height) // 2
    
    # Paste with alpha compositing
    bg.paste(icon, (x, y), icon)
    
    return bg

def main():
    print("Loading source icon...")
    source = Image.open(SOURCE_ICON).convert("RGBA")
    
    for density, size in DENSITIES.items():
        out_dir = os.path.join(MIPMAP_DIR, density)
        os.makedirs(out_dir, exist_ok=True)
        
        # Square icon
        icon = create_launcher_icon(size, source, round=False)
        icon_path = os.path.join(out_dir, "ic_launcher.png")
        icon.save(icon_path, "PNG")
        
        # Round icon
        round_icon = create_launcher_icon(size, source, round=True)
        round_path = os.path.join(out_dir, "ic_launcher_round.png")
        round_icon.save(round_path, "PNG")
        
        # Remove old webp files
        for old in ["ic_launcher.webp", "ic_launcher_round.webp"]:
            old_path = os.path.join(out_dir, old)
            if os.path.exists(old_path):
                os.remove(old_path)
                print(f"  Removed old: {old}")
        
        print(f"Generated {density}: {size}x{size}")
    
    # Update background color XML
    bg_xml_path = os.path.join(PROJECT_DIR, "app", "src", "main", "res", "values", "ic_launcher_background.xml")
    with open(bg_xml_path, 'w') as f:
        f.write('''<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="ic_launcher_background">#0077B6</color>
</resources>
''')
    print(f"Updated background color to #0077B6")
    
    # Also update the other background XMLs that may exist
    for bg_file in [
        os.path.join(PROJECT_DIR, "app", "src", "main", "res", "values", "icsanitovacheckshield_background.xml"),
        os.path.join(PROJECT_DIR, "app", "src", "main", "res", "values", "icsanitovacheckshieldbackground.xml"),
    ]:
        if os.path.exists(bg_file):
            with open(bg_file, 'w') as f:
                f.write('''<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="icsanitovacheckshield_background">#0077B6</color>
</resources>
''')
            print(f"Updated {os.path.basename(bg_file)}")
    
    print("\nDone! Launcher icons generated successfully.")

if __name__ == "__main__":
    main()
