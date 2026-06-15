# Northwind ERP — Fullstack Project

## Project Structure

```
northwind-erp-fullstack/
├── spring-erp/          # Spring Boot REST backend (Java 17, Oracle DB)
└── angular-erp/         # Angular frontend (TypeScript, Bootstrap)
```

Both sub-projects are developed together. The backend exposes a JSON API on `localhost:8080`; the frontend consumes it from `localhost:4200`.

---

## Status Files

Each sub-project has a living reference document. **Read the relevant file at the start of every session** — it contains full context so you do not need to re-scan the codebase.

| Session opened in | Read this file first |
|---|---|
| `spring-erp/` | `spring-erp/BACKEND_STATUS.md` |
| `angular-erp/` | `angular-erp/FRONTEND_STATUS.md` |

---

## Keeping Status Files Updated

Whenever you make a meaningful change (new feature, bug fix, refactor, dependency change, architecture decision), add a new entry to the **Changelog** section of the relevant status file.

**Changelog entry format:**
```
### [YYYY-MM-DD] — Short title
- What was added / changed / fixed
- Include file/class paths for precision
```

Entries go at the **top** of the Changelog section (newest first).

Also keep the rest of the status file in sync: if an endpoint changes, update its table; if a gap is fixed, remove it from Known Gaps & TODOs.

---

## Shared Convention

Both status files use the **same section structure and formatting rules** so they stay consistent:

- Title: `# Northwind ERP — [Backend|Frontend] Status`
- Numbered Table of Contents with anchor links
- "Tech Stack" table
- "Project Structure" directory tree
- "Architecture & Patterns" narrative
- "Features & Module Status" with per-module `**Status: complete.**` lines
- "Known Gaps & TODOs" as `- [ ]` checkboxes
- Changelog with the format above

Domain-specific sections (e.g. Database Schema, Entities, DAOs in the backend; Styling, API Integration in the frontend) are fine to have in only one file.

---

## General Preferences

- No comments in code unless the *why* is non-obvious.
- No unnecessary abstractions — solve the actual problem, don't design for hypothetical futures.
- Prefer editing existing files over creating new ones.
- Keep responses concise.
