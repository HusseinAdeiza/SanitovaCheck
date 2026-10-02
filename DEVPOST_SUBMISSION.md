# SanitovaCheck

WASH education and AI-assisted self-assessment, built by an environmental health professional in Nigeria.

## Inspiration

I am a Licensed Environmental Health Officer, EHORECON-certified, running Sanitova Environmental Health Services Ltd in Nigeria. I wanted to make water, sanitation, and hygiene education easier to access through a phone, alongside a practical way to document environmental health concerns.

That became SanitovaCheck. My goal is to help people understand WASH risks and prevention, while being clear that an app cannot replace a professional inspection or laboratory water testing.

## How I have been building it

SanitovaCheck has taken weeks of development, testing, corrections, and publishing work. My shared records show it in Google Play closed testing with a July 28, 2026 update, continued setup work in August, and further fixes in September. It was not a one-night build.

I used Android Studio and worked with Claude AI and Claude Code to plan features, implement changes, and troubleshoot problems. I brought the environmental health perspective and decided what the app needed to do. I also installed builds on my own Android phone, checked the results, and returned with screenshots when something was wrong.

The work stretched across many conversations. In one Claude session, I reached the image-sharing limit and asked for a handover document so I could continue in another chat. Progress often meant carrying the same unresolved issue into the next session rather than starting a new feature.

## Testing and feedback

SanitovaCheck went through Google Play closed testing. One shared Console snapshot recorded an installed audience of 13. Having people install the app was an early milestone, but I do not treat that number as proof of user satisfaction or product impact.

My own hands-on testing also shaped the app. In the development chats, I reported that only the first three educational videos worked while others opened unavailable YouTube pages. Later, I narrowed the remaining problem to two videos. That led to repeated revisions of the video list and its presentation. A better-looking card was not enough if the lesson itself could not be watched.

I also checked authentication, the launcher icon, and the assessment reports. During later testing, I noticed that a report could appear to accept a description even when the submitted picture did not match it. I requested image-description verification, but its visible behaviour remained unresolved in my testing. I am not presenting it as a proven feature in this submission.

These experiences changed how I judged progress: a successful build did not mean the problem was fixed. I needed to see the correct behaviour on the phone.

## How the product evolved

The project grew beyond scanning. I explicitly asked for a place where users could learn about WASH, ask questions, and watch lessons about preventing WASH-related diseases.

The learning approach combines educational articles with references, links to public educational videos, and general AI-assisted WASH questions and answers. The current chat should not be described as retrieving answers from an ingested WHO document library. That document-grounded system is a separate roadmap item.

I also considered what would make the free version genuinely useful. The proposed history model allowed free users to retain up to 10 scans, with unlimited history for Pro. The reasoning was to let people experience ongoing value before asking them to subscribe. Exact limits should match the demonstrated submission build.

## RevenueCat and the work behind publishing

RevenueCat and Google Play Billing are part of the project's subscription approach. The intended Pro offering supports more extensive use, including expanded history and PDF report export.

The integration required work beyond the Android interface. My chats document repeated attempts to locate Google Play API settings and work through the Google Cloud service-account and RevenueCat credential setup. Instructions did not always match the Console screens I was seeing, and this consumed time across sessions.

Publishing brought further obstacles. I worked through an upload-key reset and its activation wait, rebuilt release bundles, and revised store-listing language after a misleading-claims rejection. References to health organisations needed clear sources and wording that did not imply affiliation or endorsement.

## What I learned and what comes next

I am proud that SanitovaCheck progressed through closed testing to a published Android app, and that I kept returning to problems found during actual use. AI coding tools helped me build, but they did not remove the need to check links, challenge incorrect assumptions, or test on a device.

My next priorities are more structured tester feedback, reliable image-description verification, and better support for field use with limited connectivity. I also want to add languages such as Hausa, French, and Swahili, develop document-grounded Q&A with traceable citations, and assess how relevant Sphere Handbook indicators could inform future workflows. These are plans, not claims of completed functionality or certification.

SanitovaCheck began from my work in Nigeria. I want to develop it into a useful tool for communities and field workers elsewhere, while keeping its guidance understandable and its limitations visible.

---

## Editorial evidence notes — not for the public submission

Status: Draft for the creator's review. This document is a narrative synthesis of supplied chats, not a new technical verification of the app.

- The first September 17 pasted file documents Console navigation difficulties, the chat image limit, and a handover titled August 16, 2026. It contains no tester testimonials.
- The second pasted file documents feature-planning discussions, the proposed 10-scan free history, the user's request for WASH learning, and explicit separation of immediate scope from roadmap items. It does not prove those requested features were all implemented.
- The earlier pasted Console snapshot supplies the July 28 closed-testing update and installed audience of 13. Neither establishes the exact project start date, 13 active testers, or organic growth.
- Specific video, authentication, icon, and image/report observations above come from the creator's own messages in this conversation. Do not attribute these to external testers.
- External tester names, direct quotations, feedback dates, and resulting changes have not been supplied. Add an external-feedback paragraph only after the creator provides those records.
- The creator previously reported that the app was published and an update approved. Current Play status was not checked for this writing task.
- Removed unsupported claims from the earlier draft: reliable photo-verification/fraud prevention, laboratory-like contamination detection, WHO endorsement, proven global deployment, guaranteed crash prevention, and a verified FastAPI/Firestore architecture.
- Corrected the professional credential spelling to EHORECON. Google Play supplies storefront pricing; do not claim RevenueCat itself performs currency conversion.

### Creator review before submission

- [ ] Confirm this first-person account reflects your experience.
- [ ] Supply any external tester feedback to include, or retain the current distinction between closed testing and your own testing.
- [ ] Confirm subscription features and limits against the build judges will use.
- [ ] Check the hackathon rules and identify work completed within its eligible development period separately from earlier work.

Google Play: https://play.google.com/store/apps/details?id=com.sanitova.sanitovacheck
