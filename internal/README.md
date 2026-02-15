# `internal/`

This directory houses applications & services that are more related to the developers of Codebloom in comparison to the client-facing servers & UIs to codebloom.

## Structure

_Last updated: 02/14/2026_

This `internal/` directory currently consists of

- `standup-bot`: a Discord bot written in [Rust](https://rust-lang.org/) to send daily standup messages to our private Discord server.
  - [View detailed documentation](./standup-bot/README.md)
- `scripts`: a collection of scripts written in the [React Ink CLI](https://github.com/vadimdemedes/ink) framework to help us with onboarding users. As of right now, this is used to help onboard new members of the Codebloom development team to [`git-crypt`](https://github.com/AGWA/git-crypt).
  - [View detailed documentation](./scripts/README.md)
