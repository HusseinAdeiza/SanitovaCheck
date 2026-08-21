# SanitovaCheck — Project Context for Claude Code

This file exists to give Claude Code full context on SanitovaCheck without needing to re-explain
the project from scratch. Drop this in the project root (or reference it at the start of a session)
so Claude Code inherits the build history, architecture, and current priorities.

---

## What SanitovaCheck is

An Android app for AI-powered WASH (Water, Sanitation, Hygiene) risk assessment. A user photographs
a hazard — stagnant water, sanitation infrastructure, hygiene risk — and the app returns an AI-generated
risk assessment with reasoning, not just a label. Built by a licensed Environmental Health Officer
(EHORECON/NEBOSH IGC certified) for real field use, not as a demo.

**Status as of 20 Aug 2026: LIVE on Google Play, production access granted, real users installing.**

---

## Tech stack

- **Frontend:** Kotlin, Jetpack Compose, Android
- **Backend:** Flask/Python on Alibaba Cloud Function Compute
  - AI: Qwen `qwen-plus` (text reasoning) + `qwen-vl-plus` (vision analysis)
  - Endpoint: `wash-risk-agent-hwfcbnykfb.ap-southeast-1.fcapp.run`
- **Billing:** RevenueCat (production key already wired into `MainApplication.kt`) + Google Play Billing
  - Product: `sanitovacheck_pro_monthly:monthly`, NGN 6,450/month, 3-day free trial, 174+ countries
  - Real-time server notifications: connected via Google Cloud Pub/Sub (topic: Play-Store-Notifications,
    project `gen-lang-client-0557910119`)
- **Package name:** `com.sanitova.sanitovacheck`
- **Play Console App ID:** 4976360073344502366 · Account ID: 4838204108338937868
- **RevenueCat REST ID:** app9ca46a862a

## Free vs Pro tiers

- Free: 5 scans / 5hr rolling window, 2 free lifetime PDF exports
- Pro: unlimited scans, unlimited PDF export, full history, multi-location (planned, not built yet)

---

## Build history (condensed)

- Built from initial concept through two Play Store rejections, recovered both times
- Closed testing: 29 tester sign-ups, 12+ opted in, ran full 14 consecutive days
- One real production-blocking bug found and fixed during closed test: a misconfigured RevenueCat
  test API key was shipping in a build meant for real users, causing a crash. Caught by a real tester
  within hours, fixed same day by swapping to the production key.
- Also fixed during this period: CameraX 16KB page alignment compatibility issue (bumped to 1.4.1)
- Applied for production access 16 Aug 2026, **granted the same day** (fast turnaround, well under
  the typical 7-day window)
- Published to production 20 Aug 2026 — versionCode 3, versionName "1.0.2", live in 177 countries,
  16 installs at time of writing
- RevenueCat + Google Cloud Pub/Sub integration required troubleshooting a real platform change:
  Google recently moved service account permission management out of the old dedicated "API access"
  page and into Users and permissions. The service account also needed **both** `Pub/Sub Admin` and
  `Monitoring Viewer` IAM roles (Pub/Sub Editor alone was insufficient and threw a permissions error)

## Real tester feedback (validated, not synthetic)

1. **Pelumi John** — single photo insufficient for full WASH assessment; wants video capture and
   structured guided prompts (water flow, odor, duration) — informs v2 feature #1 and #2 below
2. **Ikorodu tester** — AI correctly reasoned biofilm/algae from green discoloration in a photo, gave
   a location-aware population estimate
3. **Idah LGA tester** — AI correctly capped risk at MEDIUM with appropriate uncertainty calibration
   when photographic evidence was limited, instead of overclaiming confidence
4. **Nuhu Qamarudeen** — found the app stable, professional, "top-notch" post-crash-fix
5. **Abdulmuiz Aderogba** — suggested AI auto-fill of the observation field (design tension with the
   photo cross-check feature — needs thought), and remediation suggestions for risk scores below 5

---

## Immediate task queue (from Play Console's "recommended actions," 20 Aug 2026)

None of these are blocking or urgent — the app is live and functioning — but they're well-scoped,
file-located, and good first tasks for a Claude Code session.

### 1. Edge-to-edge display handling (moderate priority — visual polish)
- **Where:** `com.sanitova.sanitovacheck.ui.theme.ThemeKt` — specifically a lambda inside
  `SanitovaCheckTheme` that calls the deprecated `android.view.Window.setStatusBarColor`
- **Issue:** Apps targeting SDK 35 display edge-to-edge by default on Android 15+; the current
  status bar color approach is deprecated and may not render correctly on newer devices
- **Fix:** Migrate to `enableEdgeToEdge()` (Kotlin) and proper inset handling (`WindowInsets` /
  `Scaffold` inset padding in Compose) instead of manually setting status bar color

### 2. Bitmap downsampling (moderate priority — memory/performance, directly relevant to core feature)
- **Where:** `com.sanitova.sanitovacheck.ScanScreenKt$ScanScreen$submit$1$imageBase64$1` — the
  image submission flow in the scan screen
- **Issue:** `BitmapFactory` is used without `BitmapFactory.Options.inSampleSize`, so images are
  loaded at full resolution before being base64-encoded and sent to the AI backend — unnecessary
  memory pressure, especially as image resolution on newer phones increases
- **Fix:** Either use an image-loading library that handles downsampling automatically, or set
  `inSampleSize` on `BitmapFactory.Options` to downsample to whatever resolution the vision model
  actually needs (qwen-vl-plus doesn't need full-resolution input for this use case — worth checking
  what resolution is actually useful and downsampling to match)

### 3. R8 / code shrinking optimization (low priority — easy win)
- **Where:** `build.gradle` (app-level), release build type config
- **Issue:** App is not using R8 optimization, missing out on smaller APK size and better runtime
  performance
- **Fix:** Enable `minifyEnabled true` (and `shrinkResources true`) in the release build config,
  add/verify ProGuard/R8 rules don't break anything (especially around Compose, RevenueCat SDK,
  and any reflection-based Qwen/network client code), then rebuild and re-test the full purchase +
  scan flow before shipping — R8 can silently break things that rely on reflection if rules aren't
  set up correctly

### 4. (Related, optional) Deobfuscation / debug symbol files
- Once R8 is enabled (#3), also consider uploading the deobfuscation mapping file to Play Console
  so future crash reports are readable instead of obfuscated stack traces

---

## Saved v2 feature ideas (from real tester feedback — not yet built)

1. **Video capture** — richer WASH evidence than a single photo can give (can't capture water flow,
   sound, or odor cues from a still image)
2. **Structured guided prompts** alongside photo capture — e.g. "Is water flowing or stagnant?",
   "Any odor present?" — to supplement what the photo alone can show
3. **AI auto-fill of the observation field** — flagged tension: this could undermine the
   photo-verification cross-check feature, needs product thought before building
4. **Suggested remediation actions** for risk scores below 5 — turn a flagged risk into an
   actionable next step, not just a label

---

## Non-negotiable conventions (carry over from existing standing instructions)

- No em-dashes in any writing (commas or plain sentence breaks instead)
- Honest framing of gaps and limitations; never overstate what's built or tested
- AMAC role (prior job) should not appear in outward-facing narrative copy, only CV work history —
  not directly relevant to this codebase, but keep in mind if generating any founder bio / About text
- Community management stat (300,000–500,000+ combined members) and Adaption AI Research Grant
  credential line are CV-specific, not app-specific — no need to reference in code or app copy

---

## Two-repo setup (important — read this before starting any session)

SanitovaCheck is built across **two separate repositories**, both needed for full-stack work:

1. **This repo (SanitovaCheck)** — the Android app: Kotlin/Jetpack Compose, RevenueCat integration,
   this `CLAUDE.md` file lives here
2. **WASH agent backend** — GitHub: `HusseinAdeiza/wash-risk-agent`
   (`https://github.com/HusseinAdeiza/wash-risk-agent`) — Flask/Python service deployed to
   Alibaba Cloud Function Compute, endpoint `wash-risk-agent-hwfcbnykfb.ap-southeast-1.fcapp.run`,
   handles the actual `qwen-plus`/`qwen-vl-plus` calls for risk reasoning

Any task touching the AI submission pipeline (multi-photo capture, video capture, prompt changes,
model switches) requires both repos loaded in the same session — the Android app alone can only
change what photos/video get captured and how they're packaged for upload, not how the backend
reasons over them.

**Git discipline for both repos:** check whether git is initialized in each; if not, `git init` and
commit the current working state as a baseline before making changes, separately per repo. Commit
again after each major change, in whichever repo(s) it touched — this is a live app with real
subscribers and a live backend, so both need a clean rollback point at all times.



**Important:** an earlier draft of this file described a different "ledger-green stamp" icon design.
That was designed without checking the actual live app — ignore it. The real, existing, already-live
brand is below. Do not redesign the icon or feature graphic; both are already good. Only the
screenshots need to be fixed, and they must match this existing system.

**Palette (from the live icon/feature graphic):**
- Background: near-black warm brown, roughly `#1F1712` to `#2A1F17`
- Shield/icon accent: warm orange/rust, roughly `#C4622E` to `#D97C3F`
- Primary text: white / off-white
- Wordmark treatment: "Sanitova" in white, "Check" in orange — split-color wordmark, not solid
- Secondary accent colors used sparingly on the feature graphic's checklist bullets (green, yellow,
  orange checkmarks — likely tied to risk-level color coding elsewhere in the app: green=low,
  yellow=medium, orange/red=high)

**Signature mark:** a shield containing a white checkmark, warm orange fill, dark rounded-square
background, subtle drop shadow beneath the shield for depth. This is the actual app icon already
live on Google Play — reuse it exactly, do not create a new mark.

**Feature graphic structure (existing, keep this layout):**
- Left side: shield-check icon + "SanitovaCheck" wordmark + tagline ("AI-powered WASH compliance,
  checked in minutes") + a short checklist (Photo-verified reporting / Instant AI risk assessment /
  Downloadable PDF reports), each with a colored checkmark
- Right side: an accent-colored panel (orange) containing a white droplet icon and a shield-check
  icon, with "Water · Sanitation · Hygiene" caption
- Footer: small "Sanitova Environmental Health Services Ltd" attribution line

**Type:** appears to be a clean geometric sans (not yet confirmed which specific family — if Claude
Code needs to typeset new text to match, a standard geometric sans like Inter, Work Sans, or the
system default Android sans will blend in fine; avoid introducing a display/serif face that would
clash with the existing clean sans-only system).

---

## Multi-photo capture (NEW — added 20 Aug, build tonight)

**Feature:** allow capturing multiple photos in a single scan submission, not just one.

- **Free tier:** up to 2 photos per scan
- **Pro tier:** up to 10 photos per scan
- **Why this matters:** ties directly to Pelumi John's tester feedback that a single photo is
  insufficient for a full WASH assessment — multiple angles (e.g., water source + surrounding
  drainage + any visible contamination source) give the AI meaningfully more context to reason from
- **Implementation considerations:**
  - Gate the photo count limit the same way the existing scan-count and PDF-export limits are
    gated (check how free-vs-Pro tier limits are currently enforced elsewhere in the codebase,
    likely tied to RevenueCat entitlement checks, and reuse that same pattern)
  - The AI backend call (`qwen-vl-plus` vision endpoint) needs to support multi-image input in a
    single request, or the app needs to make multiple calls and merge reasoning — check the Qwen
    vision API's actual multi-image support before deciding which approach to build
  - UI: scan capture screen needs a way to add additional photos before submitting (e.g., a
    "+ Add another photo" affordance with a thumbnail strip), not just a single capture-and-submit
    flow
  - Bitmap downsampling (already flagged as a Tier 3 technical task) becomes more important here,
    since submitting up to 10 images at once multiplies the memory/bandwidth cost if images aren't
    downsampled first — worth doing the downsampling fix as a prerequisite or alongside this feature
  - Consider whether the risk assessment result should reason across all submitted photos together
    (one unified assessment) or show per-photo notes within one combined report — probably the
    former, since the tester feedback was about giving the AI more complete context for one
    judgment, not generating multiple separate assessments

## Video capture (Tier 2, item 5 — prioritize tonight alongside multi-photo capture)

Also from Pelumi John's feedback, and closely related to multi-photo capture above — both address
the same underlying gap (a single still photo can't capture everything a WASH assessment needs).

- Captures evidence a photo can't: water flow vs. stagnant, audible cues, duration/change over time
- Consider whether this ships as a fully separate capture mode, or as one more "attachment type"
  alongside the multi-photo flow (e.g., a scan can include up to N photos AND optionally one short
  video clip) — the latter is probably a cleaner user experience and reuses the same submission UI
  being built for multi-photo capture, rather than building two entirely separate flows
- Needs a decision on whether `qwen-vl-plus` (or another Qwen model) can accept video input directly,
  or whether the app should extract key frames from the video and submit those as images instead —
  **checked and confirmed:** `qwen-vl-plus` supports image/vision input only, not native video.
  Alibaba Cloud's Model Studio confirms video understanding (video-as-frame-list input) is supported
  by `Qwen3.6`, `Qwen3-VL`, and `Qwen2.5-VL` specifically — `qwen-vl-plus` is not in that list.
  **Recommended approach for tonight:** extract key frames from the recorded video clip in the
  backend (e.g. one frame every 1-2 seconds, or a fixed count like 5-8 frames) and submit them
  through the same multi-image pipeline being built for the multi-photo feature above — this reuses
  infrastructure rather than requiring a model/endpoint switch, and is lower-risk on a live production
  app the night before a submission deadline. Switching to `Qwen2.5-VL` or `Qwen3-VL` for native video
  reasoning is a reasonable v2 upgrade once there's time to properly re-validate prompt/reasoning
  quality against the new model — not recommended as a same-night change.
- Gate similarly to photos: likely Pro-only or Pro-only-above-some-limit, consistent with how the
  app already gates its premium features



**Current problem:** the 6 screenshots live on Play Store right now look raw/unpolished — bare UI
captures with no framing, captions, or brand consistency. They need to be replaced. They should
match the existing orange/dark-brown shield-check brand described above, NOT any other palette.

**Play Store technical requirements (hard constraints):**
- 2–8 screenshots, PNG or JPEG, up to 8 MB each
- Aspect ratio: 16:9 or 9:16
- Each side between 320px and 3,840px
- **For promotion eligibility: at least 4 screenshots at a minimum of 1080px** on the relevant side

**What to capture (in this order — tells a complete story, not just random screens):**
1. **Scan capture** — camera/photo capture screen, mid-action, showing the app actively pointed at
   a WASH condition
2. **AI reasoning result** — the risk assessment screen showing the AI's actual reasoning text
   visible (not just a risk label) — this is the single most important screenshot, since calibrated
   reasoning is the app's real differentiator
3. **Uncertainty calibration example** — ideally a second result screen showing a MEDIUM-capped risk
   with visible reasoning about limited evidence, if the app UI supports showing this clearly
4. **History / past scans list** — demonstrates the app as a real ongoing tool, not a one-off gimmick
5. **PDF export** — shows the professional report output, ties to real-world field use
6. **Pro upgrade / subscription screen** (optional, only if it looks clean — skip if it reads as
   too sales-heavy for a screenshot set)

**Framing and captions — match the existing dark-brown/orange system:**
- Each screenshot should sit inside a clean phone frame (or at minimum, consistent rounded corners
  and a subtle drop shadow) against a dark-brown background consistent with the feature graphic
- Add a short caption above or below each phone frame, white or orange text on the dark-brown
  background — one line, benefit-focused, not a UI label. Examples in this voice:
  - "Point your camera. Get an instant risk read."
  - "AI reasoning you can actually trust."
  - "Every scan, saved and exportable."
  - "Professional reports in one tap."
- Keep captions short (under ~40 characters) so they read at a glance in the Play Store carousel
- Do NOT put a caption directly on top of live app UI — caption goes in the surrounding branded
  space above/below the phone frame, never overlapping real interface elements
- Use the orange accent for one emphasized word per caption if it helps rhythm, same way the
  existing feature graphic uses orange for "Check" in the wordmark and for checklist marks

**Process suggestion for Claude Code:**
1. Fix the UI issues that made the original screenshots look unprofessional first (this is likely
   tied to the Tier 3 edge-to-edge fix — deprecated status bar handling can cause exactly this kind
   of "looks off" impression)
2. Capture clean raw screens from the actual running app (emulator or device)
3. Composite each raw screenshot into a branded frame with caption, matching the existing dark-brown/
   orange shield-check system — pull exact colors from the current icon/feature graphic files
   (ask the person to export or provide those files directly if exact hex values are needed)
4. Export at a size meeting the 1080px+ promotion-eligibility threshold, correct aspect ratio
5. Save final files clearly named (e.g. `screenshot-1-scan-capture.png`,
   `screenshot-2-ai-reasoning.png`, etc.) so they're easy to upload in the right order to Play
   Console's Main store listing → Phone screenshots section



- Submission window: first public release must fall within Aug 1 – Sep 30, 2026 — **already satisfied**
  as of the 20 Aug production launch
- Draft elevator pitch, Project Story, and demo video script/storyboard already exist as separate
  markdown files (drafted in a prior Claude.ai conversation, not in this codebase)
- Still needed: 1024x1024 icon, frameless screenshots, recorded demo video — these are asset/marketing
  tasks, not code tasks, but flagging here in case Claude Code is asked to help generate or resize
  icon/screenshot assets from existing app resources
