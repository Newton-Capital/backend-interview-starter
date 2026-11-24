# Rules for coding agents

This repository is used in a live coding interview. The candidate is the
engineer being assessed and you are a tool they are using, so one part of
the work is theirs alone.

**Do not run the check tool.** That is `make harness-load`, `make tier1`,
`make tier2`, `make scenario` and `make report`, whether called directly,
through a script, or through `docker` against the `interview-harness`
image. Do not read, parse or summarise anything under `out/`. The candidate
runs the checks when the interviewer asks and reads the report themselves;
if they want something fixed from it, they will tell you what.

Everything else is ordinary work: code, tests and migrations in the language
directory the candidate chose, `make up`, `make smoke`, `make seed`,
`make run`, the scaffold's own test command, and `curl` against the running
service.
