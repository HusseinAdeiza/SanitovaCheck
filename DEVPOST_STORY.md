# Devpost Submission — SanitovaCheck
### Story order mirrors the demo video: problem → why me → what it does → how built → tested → limits → next

Paste the block below into Devpost's **"About the project"** field.
Do not include the `---` markers or the code-fence lines.

---

## Inspiration

Access to safe water and reliable sanitation is a basic need, yet millions of people in
Nigeria have no quick, trustworthy way to judge whether the water they use every day is
safe, or whether a sanitation facility is up to standard.

Most WASH information is buried in reports nobody reads. Most risk assessments belong to
officials who are hours away. Meanwhile, people are making decisions about water every
single day with no support at all.

I am a Licensed Environmental Health Officer, EHORECON-certified, running Sanitova
Environmental Health Services Ltd in Nigeria. I built SanitovaCheck because I kept seeing
that same problem in my own work: people needed a first answer now, not a report next
quarter. It started as a tool for my own assessments and became a free Android app that
anyone can use.

## What it does

SanitovaCheck combines WASH education with AI-assisted self-assessment in one app.

- **Scan** — photograph a water source or sanitation facility, add a location and what you
  observe, and receive an AI-generated risk assessment with observations drawn from the
  image, a risk indication, and suggested next steps.
- **Learn** — practical WASH articles covering handwashing, cholera prevention, safe water
  storage and sanitation, each with references, plus curated educational videos that open
  directly in YouTube.
- **AI assistant** — general WASH questions answered in plain language.
- **Scan History** — every assessment saved and revisitable.
- **Pro** — PDF report export and extended history, for professionals who need documented
  records. Subscriptions run through Google Play Billing and RevenueCat.

The assessment flow and the learning content are free. That is deliberate. I wanted the
upgrade decision to follow real use of the app rather than hitting a new user with a
paywall on day one.

### What this is not

A photograph cannot confirm microbial contamination, and it cannot establish that water is
safe to drink. What the app does is flag visible indicators — discolouration, leaks,
proximity to a contamination source — so a user knows whether to act and whether to escalate
to a professional. It is a first check, not a laboratory result, and not a replacement for
an official inspection.

SanitovaCheck is independent and is not affiliated with or endorsed by WHO, UNICEF, or CDC.
References to their educational materials are citations, not partnerships. The AI chat
provides general responses; it is not a document-retrieval system grounded in an ingested
library of those organisations' publications.

## How we built it

SanitovaCheck has taken weeks of development, testing, corrections, and publishing work.
By 27 July 2026 I was inviting people to test it through Google Play. Work continued
through August and into September.

The Android app is built in Android Studio with Kotlin and Jetpack Compose, using CameraX
for camera functionality. The project uses a Python backend hosted on Alibaba Cloud Function
Compute and Qwen models for AI functionality. RevenueCat and Google Play Billing support the
subscription model.

I worked with Claude AI and Claude Code to plan features, implement changes, and
troubleshoot problems. I brought the environmental health perspective, decided what the app
needed to do, and checked whether the behaviour matched the intended use.

The work involved repeated builds and installations on my own Android phone. When something
did not work, I returned with screenshots and specific descriptions. Some issues persisted
across several sessions. One Claude conversation reached its image-sharing limit, so I
requested a handover document and continued in another chat.

AI assistance sped up the build. It did not remove the need to test on a device or to reject
suggestions that did not fit. A successful build was never treated as proof that a feature
worked.

## Testing and how feedback shaped the project

I invited people to join the Google Play test and follow a real workflow: start an
assessment, photograph a water source or sanitation facility, add a location and
description, submit, and read the report. I also asked them to try PDF export and explore
History and Settings.

I asked about crashes, confusing steps, whether the assessment made sense, and what was
missing. I offered a thank-you reward of ₦1,000 or more for genuine feedback, including
criticism. These were incentivised testing responses, not independent public reviews.

### A tester could not complete the flow

On 27 July, one tester reported instability and a point where they could no longer proceed.
Their email included four attachments.

That report is why testing beyond my own phone mattered. A flow that looked complete during
development had left another person unable to finish. It also confirmed that installing an
app does not mean someone can use it.

### A photograph was not enough context

On 31 July, another tester described the interface as user-friendly and the assessment
process as straightforward, but questioned whether a single photograph could capture enough
about water flow, the source, and surrounding conditions.

I agreed, and proposed video capture plus guided questions about conditions such as stagnant
water or unusual odour. That exchange shaped the roadmap for richer evidence, and it
sharpened a boundary I now state plainly in the app: an image can contribute to an
assessment, but it cannot establish everything about a water source.

### Users wanted less typing

On 2 August, a tester suggested that AI should fill the "What do you observe?" field
automatically, and asked for treatment suggestions when scores were low. I explained why an
independent description is useful when comparing what someone reported against what appears
in the image. On 4 August they clarified: keep the field, but make it optional for people
who want a faster run.

That is a real design trade-off. More context can support an assessment, but requiring too
much typing discourages people from finishing it. Making the field optional is on the
roadmap.

The request for treatment suggestions showed people want to know what to do next, not just
receive a score. Any such guidance needs safeguards and should not depend on a score alone.

### Encouragement, honestly received

Other testers reported clear wording, no crashes or freezes during their sessions, and
explanations they found useful. One appreciated the report's discussion of a discoloured,
leaking water source.

I distinguish perceived usefulness from scientific accuracy. A convincing explanation is not
proof of contamination. Testers were also asked not to purchase Pro because of the cost, so
their feedback covered the core experience but did not verify the paid subscription flow.

Reviews have continued to arrive since, including critical ones. I would rather have honest
feedback now than a flattering number later.

## Challenges we ran into

### Problems that only appeared on the device

My own testing found what build output did not catch. At one stage only the first three
educational videos worked while others opened unavailable YouTube pages. After revisions I
identified two more broken videos and returned with those specific examples. A better-looking
video card was not a fix if the lesson itself could not be watched.

I also worked through authentication issues, launcher-icon behaviour, and assessment
reports. When a report appeared to accept a description that did not match the uploaded
image, I requested image-description verification. Its visible behaviour did not resolve in
my testing, so I am not presenting it as a proven feature.

A tester later reported a raw Firebase error during sign-up caused by a failed network
connection. The build now maps authentication failures to plain guidance — "check your
connection and try again" — instead of showing a technical error string.

### Getting updates published

Publishing involved an upload-key reset, exporting the replacement certificate, waiting for
the new key to become valid, and preparing signed release bundles.

I also revised the store listing after a misleading-claims rejection. References to health
organisations needed clear source links and wording that did not imply affiliation or
endorsement. That constraint shaped how the app describes itself, and the same wording
appears here deliberately.

## Accomplishments that we're proud of

SanitovaCheck progressed through closed testing to a published Android app on Google Play,
with roughly thirteen testers in the first closed test and more since.

I am also proud that the process stayed open to criticism. Testers questioned the limits of
photographs and the effort required to complete an assessment. I kept testing personally and
returned to problems even after being told they were fixed.

The app now combines WASH learning with AI-assisted self-assessment in one application, with
a subscription approach intended to support continued development.

## What we learned

The biggest lesson is that a successful build is not the same as a working feature. I had to
install the app, follow the workflow, and inspect the result.

Useful feedback includes questions about the product's assumptions, not just bug reports. A
tester who completes a flow successfully can still identify a limitation that matters more
than a visual defect.

I learned to separate what was proposed, what was implemented, and what had actually been
verified. That distinction matters in an environmental health application, where confident
wording can imply more certainty than the evidence supports.

AI coding tools helped me build, but I remained responsible for reviewing their suggestions
and deciding whether the results were appropriate.

## What's next for SanitovaCheck

- Make image-description verification reliable, and express uncertainty more clearly in
  reports.
- Collect richer assessment context through guided questions and video-based workflows.
- Make the observation field optional, as testers asked.
- Improve support for field use with limited connectivity.
- Add languages including Hausa, French, and Swahili.
- Develop document-grounded WASH questions and answers with traceable citations.
- Continue structured testing, including dedicated verification of subscription purchases and
  entitlement behaviour.

These are development plans, not claims of completed functionality or certification.

SanitovaCheck grew out of my environmental health work in Nigeria. I want to keep improving
it with users, while being clear about what it can and cannot do.

---

# FIELD NOTES (not pasted into Devpost)

**Award targeting paragraph** — paste this separately, after the story:

> **Award targeting:** We are competing for the **RevenueCat Peace Prize**, which recognises
> the project delivering the greatest social good — bringing accessible WASH risk
> assessment to communities that currently lack it, built by a licensed Environmental Health
> Officer. We are also entering **Ship Kotlin Everywhere (JetBrains)**, since the entire app
> is built with Kotlin, Jetpack Compose and CameraX. As a live Google Play app with a
> RevenueCat-powered subscription, we are also eligible for the **HAMM Award** and the
> **Grand Prize**.

**Built with tags:** kotlin, jetpack-compose, android, camerax, python, flask,
alibaba-cloud-function-compute, qwen, revenuecat, google-play, material

**Try it out:** https://play.google.com/store/apps/details?id=com.sanitova.sanitovacheck

**Claims deliberately excluded** — do not add these back:
- "177 countries", "thousands of users" — not verified
- Any affiliation with WHO, UNICEF, CDC
- "AI verified", "certified", "lab-validated"
- "Unlimited scans"
- Any statement that a photograph confirms water safety
