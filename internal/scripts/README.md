# Codebloom Scripts

This directory contains CLI scripts written in the [React Ink](https://github.com/vadimdemedes/ink) framework in order to help the Codebloom process.

### Structure

_Last updated: 02/15/2026_

This directory currently contains two scripts:

- `git-crypt/add-user`: A CLI script that can be used to add a user's public key to the repository `.git-crypt` in order to access certain secrets.
- `git-crypt/generate-key`: A CLI script that can be used to generate a GPG key and guide the user on how to upload it to [GitHub](https://github.com)

### Requirements

- [pnpm](https://pnpm.io/)
    > It is **STRONGLY** recommended that you follow the setup directions for `pnpm` inside of [docs/setup/README.md](../../docs/setup/README.md)

### Run

1. Run `pnpm i` to install all dependencies
2. Check [`package.json`](./package.json) to see which script you would like to run.
    - For example, to automatically compile & run the `add-user` script, simple run:

    ```bash
    pnpm run git-crypt-add-user
    ```
