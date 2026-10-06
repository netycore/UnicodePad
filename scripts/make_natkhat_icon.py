from pathlib import Path
from PIL import Image, ImageDraw

ROOT = Path("app/src/main/res")
NAVY = "#101827"
MINT = "#64F0C0"
INK = "#10251F"
CORAL = "#FF806E"
YELLOW = "#FFD166"
BLUE = "#6EA8FE"

# High-resolution artwork; transparent version is used by adaptive icons.
S = 432
fg = Image.new("RGBA", (S, S), (0, 0, 0, 0))
d = ImageDraw.Draw(fg)

# Character-board tile
d.rounded_rectangle((78, 78, 354, 354), radius=70, fill=MINT)

# 3x3 grid
for x in (170, 262):
    d.rounded_rectangle((x - 5, 112, x + 5, 320), radius=5, fill=INK)
for y in (170, 262):
    d.rounded_rectangle((112, y - 5, 320, y + 5), radius=5, fill=INK)

# Playful Unicode-inspired marks inside the board
d.ellipse((126, 126, 156, 156), fill=CORAL)
d.polygon([(216, 124), (222, 138), (238, 140), (226, 151),
           (230, 167), (216, 158), (202, 167), (206, 151),
           (194, 140), (210, 138)], fill=YELLOW)
d.ellipse((276, 276, 306, 306), fill=BLUE)

# Slightly different accent marks
d.rounded_rectangle((196, 280, 236, 302), radius=11, fill=CORAL)
d.ellipse((128, 278, 156, 306), fill=YELLOW)

# Raster launcher icons for older Android versions
legacy_sizes = {
    "mipmap-mdpi": 48,
    "mipmap-hdpi": 72,
    "mipmap-xhdpi": 96,
    "mipmap-xxhdpi": 144,
    "mipmap-xxxhdpi": 192,
}

for folder, size in legacy_sizes.items():
    out = ROOT / folder
    out.mkdir(parents=True, exist_ok=True)

    canvas = Image.new("RGBA", (S, S), NAVY)
    canvas.alpha_composite(fg)
    icon = canvas.resize((size, size), Image.Resampling.LANCZOS)
    icon.save(out / "ic_launcher.png")
    icon.save(out / "ic_launcher_round.png")

    # Adaptive-icon foreground uses transparent background.
    fg.resize((size * 2, size * 2), Image.Resampling.LANCZOS).save(
        out / "ic_launcher_foreground.png"
    )

print("NatkhatBoard launcher icon images created.")
