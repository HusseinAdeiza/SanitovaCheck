package com.sanitova.sanitovacheck

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.CleanHands
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Recycling

data class WashArticle(
    val id: String,
    val title: String,
    val summary: String,
    val category: String,
    val icon: ImageVector,
    val content: String,
    val keyTakeaways: List<String>,
    val references: List<String>
)

data class WashVideo(
    val title: String,
    val source: String,
    val description: String,
    val embedUrl: String
)

val washArticles = listOf(
    WashArticle(
        id = "what_is_wash",
        title = "What is WASH?",
        summary = "Water, Sanitation, and Hygiene (WASH) is a cornerstone of public health. Safe drinking water, adequate sanitation, and proper hygiene prevent disease and save lives.",
        category = "Basics",
        icon = Icons.Filled.WaterDrop,
        content = """Water, Sanitation, and Hygiene (WASH) refers to three interconnected public health pillars that are essential for human health, dignity, and development.

Water means access to safe, sufficient, and affordable water for drinking, cooking, cleaning, and personal hygiene. The World Health Organization recommends a minimum of 20 liters per person per day for basic needs, but many communities in low-resource settings fall far below this threshold.

Sanitation refers to the safe management of human waste through toilets, latrines, sewage systems, and wastewater treatment. Without proper sanitation, human feces contaminate water sources, soil, and food, spreading deadly diseases.

Hygiene covers behaviors and practices that prevent disease transmission — most critically handwashing with soap at key moments (after using the toilet, before eating, before preparing food, after cleaning a child).

Globally, 2 billion people lack safely managed drinking water, 3.6 billion lack safely managed sanitation, and 2 billion lack basic handwashing facilities at home. These gaps disproportionately affect children — diarrheal diseases caused by poor WASH are a leading cause of under-5 mortality worldwide.

WASH interventions are among the most cost-effective public health investments available. Every dollar invested in WASH generates approximately four dollars in economic returns through reduced healthcare costs, improved productivity, and better educational outcomes.""",
        keyTakeaways = listOf(
            "WASH = Water + Sanitation + Hygiene — three interconnected pillars",
            "2 billion people lack safe drinking water globally",
            "Handwashing with soap can reduce diarrheal disease by up to 50%",
            "WASH is one of the most cost-effective public health investments"
        ),
        references = listOf(
            "WHO/UNICEF Joint Monitoring Programme (JMP) 2023 Report",
            "WHO Guidelines on Drinking-Water Quality, 4th Edition",
            "UNICEF WASH Programme Guidance, 2022"
        )
    ),
    WashArticle(
        id = "water_safety",
        title = "Water Safety & Treatment",
        summary = "Learn how to assess water quality, common contamination sources, and practical treatment methods including boiling, chlorination, and filtration for field use.",
        category = "Water",
        icon = Icons.Filled.WaterDrop,
        content = """Safe drinking water is water that is free from pathogens, chemicals, and physical contaminants that can cause illness. In field settings, water safety involves three key steps: source protection, treatment, and safe storage.

Common Contamination Sources:
- Microbial: Bacteria (E. coli, Vibrio cholerae), viruses (hepatitis A, rotavirus), protozoa (Giardia, Cryptosporidium)
- Chemical: Nitrates from agriculture, heavy metals from mining, arsenic from geological sources
- Physical: Turbidity, sediment, debris

Field Water Treatment Methods:

1. Boiling — The most reliable method. Bring water to a rolling boil for at least 1 minute (3 minutes at altitudes above 2,000m). Kills all pathogens including viruses and protozoa. Does not remove chemicals.

2. Chlorination — Add chlorine tablets or liquid bleach (sodium hypochlorite) following manufacturer instructions. Effective against bacteria and viruses, but less effective against protozoa like Cryptosporidium. Leave water for 30 minutes before drinking. A residual chlorine level of 0.2–0.5 mg/L indicates proper disinfection.

3. Filtration — Ceramic, sand, or membrane filters remove bacteria, protozoa, and some viruses depending on pore size. Look for filters certified to remove pathogens. Filters do not remove dissolved chemicals.

4. Solar Disinfection (SODIS) — Fill clear plastic bottles with water and leave in direct sunlight for 6 hours (or 2 days if cloudy). UV radiation kills pathogens. Best for small volumes in sunny climates.

Safe Storage:
- Use covered, narrow-mouth containers
- Keep containers elevated off the ground
- Clean containers regularly with soap and safe water
- Do not dip hands or utensils into stored water

Water Quality Testing:
- Turbidity: Water should be clear; cloudiness indicates contamination
- Smell: No foul odors
- Taste: No unusual tastes
- Test kits: Simple field kits can test for E. coli, pH, chlorine residual, and turbidity""",
        keyTakeaways = listOf(
            "Boiling for 1 minute is the most reliable field treatment",
            "Chlorination is effective but requires 30-minute contact time",
            "Safe storage is as important as treatment — use covered containers",
            "Test water for turbidity, smell, and microbial contamination regularly"
        ),
        references = listOf(
            "WHO Guidelines for Drinking-Water Quality, 4th Edition",
            "Sphere Handbook: Water Supply, Sanitation and Hygiene Promotions, 2018",
            "CDC Safe Water System Guidelines"
        )
    ),
    WashArticle(
        id = "sanitation",
        title = "Sanitation Systems",
        summary = "Understand latrine types, sewage management, and safe waste disposal. Key for preventing cholera, dysentery, and soil-transmitted helminths.",
        category = "Sanitation",
        icon = Icons.Filled.Recycling,
        content = """Proper sanitation prevents human waste from contaminating the environment, water sources, and food. Without sanitation, diseases like cholera, dysentery, typhoid, and soil-transmitted helminths (intestinal worms) spread rapidly.

The F-Diagram of Disease Transmission:
Feces can reach the mouth through: Fluids (water), Fingers, Flies, Fields (soil), and Food. Proper sanitation breaks all these pathways.

Types of Sanitation Systems:

1. Pit Latrines — Simple, low-cost. A hole in the ground with a slab and superstructure. Must be located at least 30 meters from water sources and downhill from them. Needs regular emptying or abandonment and replacement when full.

2. Ventilated Improved Pit (VIP) Latrines — A pit latrine with a vent pipe that draws air out, reducing odors and fly problems. The pipe should be painted black and exposed to sunlight to create thermal updraft.

3. Pour-Flush Latrines — Uses 1–3 liters of water per flush to move waste into a pit or septic tank. More hygienic than dry pit latrines but requires a reliable water source.

4. Septic Tanks — Watertight tanks that receive wastewater from flush toilets. Solids settle and are partially digested by bacteria. Effluent flows to a soak pit or drainage field. Must be emptied every 2–5 years.

5. Composting Toilets — Separates liquid and solid waste. Solids are mixed with carbon material (sawdust, ash) and composted. After 6–12 months, the compost can be used safely as fertilizer.

Key Design Principles:
- Minimum 30 meters from any water source
- Downhill from water sources
- Fly-proof (tight-fitting lid, mesh screen on vents)
- Easy to clean
- Accessible to all users including children, elderly, and people with disabilities

Excreta Management in Emergencies:
In disasters and refugee camps, the Sphere Handbook recommends:
- Maximum 50 people per toilet in emergency phase
- Maximum 20 people per toilet in stabilized settings
- Separate toilets for men and women
- Lighting and locks for safety, especially for women and girls""",
        keyTakeaways = listOf(
            "Sanitation breaks the fecal-oral disease transmission cycle",
            "Latrines must be at least 30m from water sources",
            "In emergencies: max 50 people per toilet, separate for men/women",
            "Composting toilets can produce safe fertilizer after 6–12 months"
        ),
        references = listOf(
            "Sphere Handbook: Minimum Standards in Humanitarian Response, 2018",
            "WHO Sanitation and Health Guidelines, 2018",
            "UNICEF Sanitation Programme Guidance, 2020"
        )
    ),
    WashArticle(
        id = "handwashing",
        title = "Hand Hygiene",
        summary = "Proper handwashing with soap at critical times can reduce diarrheal disease by up to 50%. Learn the 5 key moments and how to promote behavior change.",
        category = "Hygiene",
        icon = Icons.Filled.CleanHands,
        content = """Handwashing with soap is one of the most effective and inexpensive ways to prevent disease. It can reduce diarrheal disease by up to 50% and respiratory infections by up to 25%. Yet globally, 2 billion people lack basic handwashing facilities at home.

The 5 Key Moments for Handwashing:
1. After using the toilet or cleaning a child
2. Before eating or handling food
3. Before feeding a child
4. After handling animals or animal waste
5. After coughing, sneezing, or blowing your nose

Proper Handwashing Technique (WHO 6 Steps):
1. Wet hands with safe water
2. Apply enough soap to cover all hand surfaces
3. Rub hands palm to palm
4. Rub back of each hand with palm of other hand
5. Rub palm to palm with fingers interlaced
6. Rub backs of fingers with opposing palms
7. Rub each thumb rotationally in opposite palm
8. Rub fingertips rotationally in opposite palm
9. Rinse thoroughly with safe water
10. Dry hands completely with a clean towel or air dry

When Soap and Water Are Not Available:
Alcohol-based hand rub (at least 60% alcohol) is an effective alternative. Apply a palmful and rub all surfaces until dry (approximately 20–30 seconds). Note: Alcohol rubs are NOT effective against spores, parasites like Giardia, or when hands are visibly dirty.

Promoting Behavior Change in Communities:
- Community-Led Total Sanitation (CLTS): Communities map open defecation areas and calculate health costs, triggering collective action.
- School WASH Programs: Install handwashing stations, integrate hygiene into curriculum, form student hygiene clubs.
- Household Visits: Health workers demonstrate proper technique, address barriers (cost of soap, water access), and follow up.
- Social Norms: Use community champions, religious leaders, and local media to make handwashing a social expectation.

Common Barriers and Solutions:
- No soap → Provide soap or teach local soap-making
- No water → Install tippy-taps (simple handwashing stations using minimal water)
- No time → Place handwashing stations at toilet exits and kitchen entrances
- Not seen as important → Use local disease data and stories to show impact""",
        keyTakeaways = listOf(
            "Handwashing with soap reduces diarrheal disease by up to 50%",
            "Wash at 5 key moments: after toilet, before eating, before feeding, after animals, after coughing",
            "Alcohol rub (60%+) works when soap/water unavailable, but not for visibly dirty hands",
            "Place handwashing stations at critical locations to create habits"
        ),
        references = listOf(
            "WHO Guidelines on Hand Hygiene in Health Care, 2009",
            "UNICEF Hand Hygiene Technical Guidance, 2020",
            "The Lancet: Effect of Handwashing on Child Health (Curtis & Cairncross, 2003)"
        )
    ),
    WashArticle(
        id = "diseases",
        title = "Common WASH-Related Diseases",
        summary = "Cholera, typhoid, dysentery, schistosomiasis, and trachoma are all linked to poor WASH. Learn symptoms, transmission routes, and prevention strategies.",
        category = "Health",
        icon = Icons.Filled.Medication,
        content = """Poor WASH conditions are directly responsible for a significant burden of disease worldwide. Here are the most common and serious WASH-related diseases, their symptoms, and how to prevent them.

Cholera
- Cause: Vibrio cholerae bacteria in contaminated water or food
- Symptoms: Sudden onset of profuse watery diarrhea ("rice water stools"), vomiting, rapid dehydration, can kill within hours
- Prevention: Boil or chlorinate drinking water, proper sanitation, handwashing, oral cholera vaccination in outbreak settings
- Treatment: Immediate oral rehydration salts (ORS) or IV fluids, antibiotics for severe cases

Typhoid Fever
- Cause: Salmonella typhi bacteria in contaminated water or food
- Symptoms: Sustained high fever, headache, abdominal pain, rose spots on chest, constipation or diarrhea
- Prevention: Safe water, proper sanitation, food hygiene, vaccination
- Treatment: Antibiotics (azithromycin or ceftriaxone), hydration

Dysentery (Bloody Diarrhea)
- Cause: Shigella bacteria or Entamoeba histolytica parasite
- Symptoms: Bloody diarrhea, abdominal cramps, fever, tenesmus (painful straining)
- Prevention: Safe water, sanitation, handwashing, safe food preparation
- Treatment: ORS, antibiotics for bacterial dysentery, metronidazole for amoebic dysentery

Soil-Transmitted Helminths (Intestinal Worms)
- Cause: Ascaris, hookworm, whipworm eggs in soil contaminated with human feces
- Symptoms: Malnutrition, anemia (especially from hookworm), stunted growth, cognitive impairment in children
- Prevention: Sanitation (toilets that contain feces), wearing shoes, handwashing, mass drug administration (MDA) programs
- Treatment: Albendazole or mebendazole

Schistosomiasis (Bilharzia)
- Cause: Schistosoma parasites released by freshwater snails
- Symptoms: Blood in urine (S. haematobium) or blood in stool (S. mansoni), abdominal pain, liver damage, increased risk of bladder cancer
- Prevention: Avoid contact with infested freshwater, proper sanitation to prevent egg contamination of water, snail control, MDA with praziquantel
- Treatment: Praziquantel

Trachoma
- Cause: Chlamydia trachomatis bacteria spread by flies and contaminated fingers
- Symptoms: Repeated eye infections leading to scarring, eyelashes turning inward (trichiasis), blindness
- Prevention: Face washing, sanitation to reduce fly breeding, mass antibiotic distribution (azithromycin), surgery for trichiasis
- Treatment: Azithromycin, eyelid surgery for advanced cases

Key Prevention Strategy:
All these diseases share the same transmission pathway: the fecal-oral route. Breaking this cycle through safe water, sanitation, and hygiene prevents them all simultaneously.""",
        keyTakeaways = listOf(
            "Cholera can kill within hours — prioritize safe water and rapid rehydration",
            "Soil-transmitted helminths cause malnutrition and stunting in children",
            "Schistosomiasis is contracted from freshwater — avoid swimming in endemic areas",
            "All these diseases share the fecal-oral route — WASH prevents them all"
        ),
        references = listOf(
            "WHO Fact Sheets: Cholera, Typhoid, Schistosomiasis, Trachoma",
            "CDC Parasitic Disease Information",
            "The Lancet: Global Burden of Disease Study 2019 — WASH-related diseases"
        )
    ),
    WashArticle(
        id = "emergency",
        title = "Emergency WASH Response",
        summary = "In disasters and humanitarian crises, WASH is critical. Learn about the Sphere Handbook minimum standards, rapid assessments, and priority interventions.",
        category = "Emergency",
        icon = Icons.Filled.Warning,
        content = """In emergencies — natural disasters, conflicts, disease outbreaks — WASH is one of the most urgent and lifesaving interventions. Within days of a disaster, populations displaced from their homes lose access to safe water and sanitation, creating ideal conditions for disease outbreaks.

Sphere Handbook Minimum Standards:
The Sphere Handbook is the globally recognized set of minimum standards for humanitarian response. For WASH, the key standards include:

1. Water Supply:
   - 15 liters per person per day (emergency minimum)
   - Maximum 250 people per water point
   - Water source no more than 500 meters from shelter
   - Water quality meets WHO guidelines (turbidity <5 NTU, zero E. coli)

2. Excreta Disposal:
   - Maximum 50 people per toilet in immediate phase
   - Maximum 20 people per toilet in stabilized phase
   - Separate toilets for women and men
   - Safe, culturally appropriate locations with lighting and locks

3. Hygiene Promotion:
   - Hygiene kits distributed (soap, water containers, menstrual hygiene materials)
   - Community mobilization for safe excreta disposal
   - Handwashing promotion at critical locations

Rapid WASH Assessment (First 72 Hours):
- Map water sources, assess contamination risk
- Count existing sanitation facilities and usage
- Identify open defecation areas
- Assess hygiene practices and knowledge gaps
- Identify vulnerable groups (women, children, elderly, disabled)
- Check drainage and vector breeding sites

Priority Interventions by Phase:

*Immediate (Days 0–7):*
- Water trucking or emergency water treatment
- Dig emergency latrines (trench latrines if necessary)
- Distribute hygiene kits
- Set up handwashing stations at latrines and food distribution points
- Start hygiene messaging through community leaders

*Stabilization (Weeks 2–8):*
- Construct more durable latrines
- Establish water point committees
- Train community health workers
- Begin solid waste management
- Set up menstrual hygiene management facilities

*Recovery (Months 3+):*
- Transition to sustainable water systems
- Promote community-led total sanitation (CLTS)
- Hand over management to local authorities or communities
- Integrate WASH with health and nutrition programs

Outbreak Response:
During cholera or other disease outbreaks:
- Increase water supply to 20+ liters per person per day
- Set up oral rehydration points
- Intensify chlorination
- Increase latrine coverage and ensure daily cleaning
- Activate vaccination campaigns if vaccines are available""",
        keyTakeaways = listOf(
            "Sphere standard: 15L water/person/day, max 50 people per toilet in emergencies",
            "First 72 hours: rapid assessment of water sources, sanitation, and hygiene practices",
            "During outbreaks: increase water to 20L/person/day and set up oral rehydration points",
            "Always prioritize vulnerable groups: women, children, elderly, people with disabilities"
        ),
        references = listOf(
            "Sphere Handbook: Humanitarian Charter and Minimum Standards, 2018",
            "UNHCR WASH Operational Guidelines, 2021",
            "WHO Emergency Response Framework, 2017"
        )
    ),
    WashArticle(
        id = "clts",
        title = "Community-Led Total Sanitation",
        summary = "CLTS is an approach that empowers communities to eliminate open defecation through local action, not external subsidies. Learn triggering techniques and follow-up.",
        category = "Behavior Change",
        icon = Icons.Filled.Groups,
        content = """Community-Led Total Sanitation (CLTS) is an innovative approach that empowers communities to completely eliminate open defecation (OD) through their own initiative, without relying on external subsidies for toilet construction. Developed in Bangladesh in 2000 by Dr. Kamal Kar, CLTS has been successfully implemented in over 60 countries.

Core Principles:
1. No Subsidies for Toilets: External agencies do not build toilets for households. Communities design and build their own using local materials.
2. Community-Led: The community identifies the problem, feels shame/embarrassment (the "trigger"), and collectively decides to stop open defecation.
3. Total Sanitation: The goal is not just building toilets but achieving an Open Defecation Free (ODF) status for the entire community.

The Triggering Process:

1. Pre-Triggering: Build rapport with community leaders, map the area, identify natural leaders.

2. Defecation Area Mapping: Facilitators guide community members (including children) to map where they defecate. The map reveals that everyone knows where feces are — including near water sources, paths, and homes.

3. Transect Walk: Walk through the community to visit open defecation areas. Facilitators ask: "Do you see flies? Where do they go after visiting feces?" Flies carry feces to food, water, and faces.

4. Fecal Calculations: Calculate how much feces the community produces daily. Example: 500 people × 200g = 100kg of feces per day. Ask: "Where does it all go?"

5. The Spark/Trigger: Someone usually says, "We are eating each other's shit." This moment of collective realization and shame is the trigger.

6. Action Planning: The community immediately plans where to build latrines, who will help whom, and sets a deadline for ODF.

ODF Verification Criteria:
- No visible human feces in the community
- Every household has a latrine
- Latrines are used by all family members
- Handwashing facilities available near latrines
- Community has systems for maintaining facilities

Challenges and Solutions:
- Slippage (return to OD): Regular follow-up visits, community monitoring, celebrate ODF anniversaries
- Hard-to-reach households: Identify barriers (disability, poverty, isolation) and provide targeted support
- Scale: Move from village to district-wide ODF through government partnerships and local institutions

Success Stories:
India's Swachh Bharat Mission used CLTS principles to declare over 600,000 villages ODF. Bangladesh reduced OD from 34% to less than 1% over two decades.""",
        keyTakeaways = listOf(
            "CLTS uses shame/embarrassment to trigger community action — no toilet subsidies",
            "The 'trigger' moment is when the community realizes they eat each other's feces",
            "ODF means zero visible feces, every household has a latrine, handwashing near toilets",
            "Follow-up is critical — slippage back to OD is common without monitoring"
        ),
        references = listOf(
            "Kar, K. & Chambers, R. (2008). Handbook on Community-Led Total Sanitation",
            "UNICEF CLTS Handbook, 2014",
            "WHO/UNICEF Joint Monitoring Programme: SDG 6.2 Tracking"
        )
    ),
    WashArticle(
        id = " menstrual_hygiene",
        title = "Menstrual Hygiene Management",
        summary = "Safe and dignified menstruation management is essential for health, education, and gender equity. Learn about facilities, materials, and breaking stigma.",
        category = "Hygiene",
        icon = Icons.Filled.HealthAndSafety,
        content = """Menstrual Hygiene Management (MHM) refers to the access to clean materials, private facilities, and adequate information to manage menstruation safely and with dignity. Poor MHM leads to health risks, school absenteeism, and social exclusion.

Health Risks of Poor MHM:
- Reproductive tract infections from unhygienic materials
- Toxic shock syndrome from prolonged use of absorbent materials
- Urinary tract infections
- Skin irritation and rashes

Essential MHM Requirements:

1. Safe Materials:
   - Disposable pads, reusable cloth pads, menstrual cups, or tampons
   - Materials must be absorbent, comfortable, and changed every 4–6 hours
   - Reusable materials must be washed with soap and dried in sunlight

2. Private Facilities:
   - Lockable, gender-segregated toilets
   - Water and soap inside or very near the toilet
   - Private washing and drying areas for reusable materials
   - Disposal bins for used materials

3. Knowledge and Information:
   - Accurate information about menstruation, what is normal, and when to seek help
   - Boys and men should also be educated to reduce stigma
   - Information delivered through schools, health workers, and peer groups

MHM in Schools:
- Lack of MHM facilities is a major reason girls miss school during menstruation
- WHO/UNICEF recommends: separate toilets for girls, water and soap, disposal facilities, and menstrual supplies
- In some countries, girls miss 20% of school days due to poor MHM

Breaking the Stigma:
- Menstruation is a normal biological process — not dirty or shameful
- Engage religious and community leaders to speak positively about MHM
- Include boys and men in MHM education to foster support
- Use local media, drama, and art to normalize the conversation

Emergency MHM:
In humanitarian settings, distribute dignity kits containing: sanitary pads, soap, underwear, and a carrying pouch. Ensure women and girls are consulted about their preferences.""",
        keyTakeaways = listOf(
            "Change menstrual materials every 4–6 hours to prevent infection",
            "Reusable materials must be washed with soap and dried in sunlight",
            "Girls miss ~20% of school days in some countries due to poor MHM facilities",
            "Include boys and men in MHM education to reduce stigma and build support"
        ),
        references = listOf(
            "UNICEF Menstrual Hygiene Management Guidance, 2019",
            "WHO/UNFPA Technical Guidance on MHM, 2020",
            "WaterAid: Menstrual Hygiene Matters, 2012"
        )
    )
)

val washVideos = listOf(
    WashVideo(
        title = "WHO: Water, Sanitation and Hygiene Overview",
        source = "World Health Organization",
        description = "Official WHO overview of global WASH challenges and progress toward SDG 6.",
        embedUrl = "https://www.youtube.com/embed/BCHhwxvQqxg"
    ),
    WashVideo(
        title = "UNICEF: The Power of Handwashing",
        source = "UNICEF",
        description = "How simple handwashing with soap saves children's lives worldwide.",
        embedUrl = "https://www.youtube.com/embed/77IZttD_pU8"
    ),
    WashVideo(
        title = "Cholera Prevention and Control",
        source = "World Health Organization",
        description = "Key measures to prevent and control cholera outbreaks in communities.",
        embedUrl = "https://www.youtube.com/embed/0v2j8Wd1vKg"
    ),
    WashVideo(
        title = "Safe Water Storage at Home",
        source = "Centers for Disease Control",
        description = "Practical guidance on storing treated water safely to prevent recontamination.",
        embedUrl = "https://www.youtube.com/embed/G5hzvu7-TX8"
    ),
    WashVideo(
        title = "Building a Tippy-Tap Handwashing Station",
        source = "WaterAid",
        description = "Step-by-step guide to building a simple, water-saving handwashing station.",
        embedUrl = "https://www.youtube.com/embed/6-xS5pQWV1A"
    ),
    WashVideo(
        title = "Community-Led Total Sanitation in Action",
        source = "UNICEF",
        description = "How communities are ending open defecation through collective action.",
        embedUrl = "https://www.youtube.com/embed/4Bf6X3z4p0Y"
    ),
    WashVideo(
        title = "Menstrual Hygiene Management in Schools",
        source = "UNICEF",
        description = "Why MHM facilities matter for keeping girls in school.",
        embedUrl = "https://www.youtube.com/embed/3y1y6L2g3cE"
    ),
    WashVideo(
        title = "Emergency WASH Response: First 72 Hours",
        source = "Sphere Project",
        description = "Priorities and minimum standards for WASH in humanitarian emergencies.",
        embedUrl = "https://www.youtube.com/embed/5kX5g1i8b2w"
    )
)
