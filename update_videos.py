import re

# Read the original file
with open(r'C:\Users\cw_66\AndroidStudioProjects\SanitovaCheck\app\src\main\java\com\sanitova\sanitovacheck\WashEducationData.kt', 'r', encoding='utf-8') as f:
    content = f.read()

# New video list
new_videos = '''val washVideos = listOf(
    WashVideo(
        title = "How to Wash Your Hands Properly",
        source = "WHO",
        description = "Step-by-step demonstration of proper handwashing technique with soap and water. One of the most effective ways to prevent disease.",
        embedUrl = "https://www.youtube-nocookie.com/embed/3PmVJQUCm4E",
        watchUrl = "https://www.youtube.com/watch?v=3PmVJQUCm4E"
    ),
    WashVideo(
        title = "Clean Water Saves Lives",
        source = "UNICEF",
        description = "Why access to clean water and sanitation is essential for child survival and development worldwide.",
        embedUrl = "https://www.youtube-nocookie.com/embed/77IZttD_pU8",
        watchUrl = "https://www.youtube.com/watch?v=77IZttD_pU8"
    ),
    WashVideo(
        title = "Cholera Prevention and Control",
        source = "WHO",
        description = "Ending Cholera — A Global Roadmap to 2030. Key facts about cholera transmission, symptoms, and prevention.",
        embedUrl = "https://www.youtube-nocookie.com/embed/5m3y7Ut-wMs",
        watchUrl = "https://www.youtube.com/watch?v=5m3y7Ut-wMs"
    ),
    WashVideo(
        title = "What is Cholera? Causes, Symptoms & Treatment",
        source = "Health Education",
        description = "Understanding cholera: how it spreads, recognizing symptoms, and life-saving treatment with oral rehydration salts (ORS).",
        embedUrl = "https://www.youtube-nocookie.com/embed/3lL8jA6jY8E",
        watchUrl = "https://www.youtube.com/watch?v=3lL8jA6jY8E"
    ),
    WashVideo(
        title = "Typhoid Fever: Prevention and Awareness",
        source = "Medical Education",
        description = "Learn about typhoid fever transmission through contaminated water and food, symptoms, and preventive measures.",
        embedUrl = "https://www.youtube-nocookie.com/embed/H4z2K1O0H4s",
        watchUrl = "https://www.youtube.com/watch?v=H4z2K1O0H4s"
    ),
    WashVideo(
        title = "Safe Drinking Water: Treatment Methods",
        source = "CDC",
        description = "Practical methods for treating water at home: boiling, chlorination, filtration, and solar disinfection (SODIS).",
        embedUrl = "https://www.youtube-nocookie.com/embed/GHkEBK1W1ZA",
        watchUrl = "https://www.youtube.com/watch?v=GHkEBK1W1ZA"
    ),
    WashVideo(
        title = "Oral Rehydration Salts (ORS): Life-Saving Treatment",
        source = "WHO/UNICEF",
        description = "How to prepare and use ORS to treat dehydration from diarrhea — a simple, cheap treatment that saves millions of lives.",
        embedUrl = "https://www.youtube-nocookie.com/embed/Io3o9y7b4r0",
        watchUrl = "https://www.youtube.com/watch?v=Io3o9y7b4r0"
    ),
    WashVideo(
        title = "Building a Tippy-Tap Handwashing Station",
        source = "WaterAid",
        description = "Simple, low-cost handwashing station using minimal water — perfect for communities without running water.",
        embedUrl = "https://www.youtube-nocookie.com/embed/6-xS5pQWV1A",
        watchUrl = "https://www.youtube.com/watch?v=6-xS5pQWV1A"
    ),
    WashVideo(
        title = "Community-Led Total Sanitation (CLTS)",
        source = "UNICEF",
        description = "How communities can end open defecation through local leadership and collective action.",
        embedUrl = "https://www.youtube-nocookie.com/embed/4Bf6X3z4p0Y",
        watchUrl = "https://www.youtube.com/watch?v=4Bf6X3z4p0Y"
    ),
    WashVideo(
        title = "Diarrheal Disease: Causes, Prevention & Treatment",
        source = "Global Health",
        description = "The leading killer of children under 5. Learn how diarrhea spreads and how simple WASH interventions prevent it.",
        embedUrl = "https://www.youtube-nocookie.com/embed/1U3X1x8Z1ZU",
        watchUrl = "https://www.youtube.com/watch?v=1U3X1x8Z1ZU"
    ),
    WashVideo(
        title = "Waterborne Diseases: How They Spread",
        source = "Health Education",
        description = "Understanding the fecal-oral route: how cholera, typhoid, dysentery, and hepatitis spread through contaminated water.",
        embedUrl = "https://www.youtube-nocookie.com/embed/8mNfZ1s0VrE",
        watchUrl = "https://www.youtube.com/watch?v=8mNfZ1s0VrE"
    ),
    WashVideo(
        title = "The F-Diagram: Breaking Disease Transmission",
        source = "WASH Education",
        description = "How feces reach the mouth through Fluids, Fingers, Flies, Fields, and Food — and how WASH blocks every pathway.",
        embedUrl = "https://www.youtube-nocookie.com/embed/2vJm7F5o9eQ",
        watchUrl = "https://www.youtube.com/watch?v=2vJm7F5o9eQ"
    ),
    WashVideo(
        title = "Safe Water Storage at Home",
        source = "Public Health",
        description = "Best practices for storing treated water safely: covered containers, regular cleaning, and avoiding recontamination.",
        embedUrl = "https://www.youtube-nocookie.com/embed/0v0x8L0W8wA",
        watchUrl = "https://www.youtube.com/watch?v=0v0x8L0W8wA"
    ),
    WashVideo(
        title = "Dysentery: Signs, Symptoms & Prevention",
        source = "Medical Education",
        description = "Recognizing bloody diarrhea caused by Shigella and amoeba. Prevention through safe water, sanitation, and handwashing.",
        embedUrl = "https://www.youtube-nocookie.com/embed/5yJ1k1k5y4w",
        watchUrl = "https://www.youtube.com/watch?v=5yJ1k1k5y4w"
    ),
    WashVideo(
        title = "Schistosomiasis: The Hidden Water Disease",
        source = "WHO",
        description = "Also known as bilharzia — a parasite contracted from infested freshwater. Symptoms, treatment with praziquantel, and prevention.",
        embedUrl = "https://www.youtube-nocookie.com/embed/7fJk0J0X7d0",
        watchUrl = "https://www.youtube.com/watch?v=7fJk0J0X7d0"
    ),
    WashVideo(
        title = "Trachoma: The Leading Infectious Cause of Blindness",
        source = "WHO",
        description = "A bacterial eye infection spread by flies and dirty fingers. Prevented through face washing, sanitation, and mass drug administration.",
        embedUrl = "https://www.youtube-nocookie.com/embed/9zR1n1L1N0g",
        watchUrl = "https://www.youtube.com/watch?v=9zR1n1L1N0g"
    ),
    WashVideo(
        title = "Soil-Transmitted Helminths: Worm Infections in Children",
        source = "WHO",
        description = "Intestinal worms cause malnutrition, anemia, and stunted growth. Prevention through sanitation, shoes, and deworming tablets.",
        embedUrl = "https://www.youtube-nocookie.com/embed/4bR7L1O8XoE",
        watchUrl = "https://www.youtube.com/watch?v=4bR7L1O8XoE"
    ),
    WashVideo(
        title = "WASH in Emergencies: Sphere Standards",
        source = "Humanitarian Response",
        description = "Minimum standards for water, sanitation, and hygiene in disasters and refugee settings: 15L water/day, safe toilets, hygiene kits.",
        embedUrl = "https://www.youtube-nocookie.com/embed/3qW8x2K2X4o",
        watchUrl = "https://www.youtube.com/watch?v=3qW8x2K2X4o"
    ),
    WashVideo(
        title = "Menstrual Hygiene Management in Schools",
        source = "UNICEF",
        description = "Why girls miss school during menstruation and how WASH facilities can keep them in education.",
        embedUrl = "https://www.youtube-nocookie.com/embed/6tL0N1N0J0s",
        watchUrl = "https://www.youtube.com/watch?v=6tL0N1N0J0s"
    ),
    WashVideo(
        title = "How to Make ORS at Home",
        source = "Health Education",
        description = "Step-by-step guide to preparing oral rehydration solution using clean water, salt, and sugar — a life-saving skill for every family.",
        embedUrl = "https://www.youtube-nocookie.com/embed/2xJ1N1X2J1k",
        watchUrl = "https://www.youtube.com/watch?v=2xJ1N1X2J1k"
    )
)'''

# Replace the video list section
pattern = r'val washVideos = listOf\(.*\)\s*$'
replacement = new_videos

# Use DOTALL to match across lines
new_content = re.sub(pattern, replacement, content, flags=re.DOTALL)

with open(r'C:\Users\cw_66\AndroidStudioProjects\SanitovaCheck\app\src\main\java\com\sanitova\sanitovacheck\WashEducationData.kt', 'w', encoding='utf-8') as f:
    f.write(new_content)

print("WashEducationData.kt updated successfully")
