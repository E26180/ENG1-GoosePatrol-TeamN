# Req1 · Part a · Introduction (draft)

*Draft by Ahmet · based on the team's elicitation process and decisions · to be reviewed by Aybike, Alex and Kev*
*Limit: ≤ 1 page (4 marks). References do not count towards the page limit.*

---

**Elicitation.** After analysing the product brief, the team held a brainstorming session that produced around 35–40 candidate questions for the client. We discussed these together and kept the 20–25 most relevant ones, recorded in a shared document split into two parts. We then held a semi-structured interview with the client [1]: four of the eight team members attended, two asking the questions (one part each) and two taking notes. With the client's permission, the meeting was also recorded so that nothing would be missed. Afterwards, we combined the written notes (as the main source) with the recording into a summary of the client's answers, from which the requirements were derived.

**Negotiation.** Where the client's answers differed from or extended the product brief, the client's view was taken as decisive. The most important constraint is that the game must contain no violence at all, because it will be used as marketing material on the university's website and must be friendly in tone. Within this constraint, and as long as the brief's requirements are met, the client gave the team creative freedom over the story, setting, items and events. One conflict remains open: the brief describes hidden events as "invisible until triggered", whereas the client explained that hidden "does not mean invisible" (the player may see something, but not know what it will do). We have contacted the client to confirm which interpretation to follow, and the affected requirements are marked as pending until then.

**Presentation.** Following common practice [1], [2], requirements are split into user requirements (what the client and players need, in plain language) and system requirements, which are further divided into functional requirements (what the game does) and non-functional requirements (qualities and constraints, such as platforms and accessibility). Each requirement has a unique, meaningful identifier (e.g. `UR_TIMER`, `FR_PAUSE`, `NFR_PLATFORM`), so that it can be traced to the architecture, code and tests in later deliverables. Every system requirement lists the user requirement(s) it supports. Priorities use the keywords *shall* (mandatory), *should* (important) and *may* (optional) [4]. To keep requirements objective and testable, each non-functional requirement has a fit criterion that states how it can be measured [3]. Requirements are presented in tables because they are easy to scan and to cross-reference.

---

## References

[1] I. Sommerville, *Software Engineering*, 10th ed. Pearson, 2016, ch. 4.
[2] ISO/IEC/IEEE 29148:2018, *Systems and software engineering — Life cycle processes — Requirements engineering*.
[3] S. Robertson and J. Robertson, *Mastering the Requirements Process*, 3rd ed. Addison-Wesley, 2012.
[4] S. Bradner, "Key words for use in RFCs to Indicate Requirement Levels", RFC 2119, IETF, 1997.

---

### Before submitting (checklist)
- [ ] Send the hidden-events email to the client; update the "one conflict remains open" sentence with the answer
- [ ] Make sure the IDs and priorities here match the tables in part b
- [ ] Check the references are cited in the format the team uses for all documents
- [ ] Check it fits on one page in the final template (sans-serif 11pt, 1 cm margins)
