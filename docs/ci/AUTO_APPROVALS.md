# Auto Approvals

The auto-approval script ([.github/scripts/auto-approval/index.ts](.github/scripts/auto-approval/index.ts)) automatically approves pull requests based on two conditions.

## How It Works

When triggered, the script:

1. Accepts `--githubToken`, `--repo`, and `--prId` as arguments.
2. Loads `NOTION_TASK_DB_ID` and `NOTION_SECRET` from the env.
3. Looks up the Notion task linked to the PR and reads its **Priority** property.
4. Fetches the PR from GitHub to get the author's username.
5. Checks whether the author is a repository collaborator.

## Approval Conditions

A PR is auto-approved if **either** condition is met:

| Condition | Requirement |
|---|---|
| **Support ticket override** | PR's linked Notion task has Priority = `Support` **and** the PR author is a repository collaborator |
| **Owner override** | PR author is an `AUTHORIZED_USER` |

If neither condition is met, the script logs `"Skipping auto approval..."` and exits without approving.

## Required Secrets / Environment

| Variable | Source | Description |
|---|---|---|
| `GH_TOKEN` | CI secret | GitHub token used to fetch PR info and submit the review |
| `NOTION_TASK_DB_ID` | `.env.ci` | Notion database ID for tasks |
| `NOTION_SECRET` | `.env.ci` | Notion integration secret |
