# Feature Workflow

Use this flow when handling a feature from assignment through merge and handoff.

## Workflow

1. Read the task description in Notion and understand the requested change/feature.
2. Set the Notion ticket status to `In Progress`.
3. Pull the latest `main` branch.
4. Create a local branch named exactly the Notion ticket ID.
5. Make every commit message start with `<ticket-id>: `.
6. Open a pull request when the change/feature is completed.
7. Set the Notion ticket status to `Pending PR`.
8. Add a clear PR description.
9. Add a screenshot from the development environment (if available).
10. Deploy the feature to staging (type /deploy in the PR comments).
11. Add a screenshot from the staging environment (if available).
12. Ensure all PR checklist items are completed.
13. Resolve any Copilot suggestions.
14. Ping a reviewer on discord.
15. Resolve reviewer comments.
16. Rebase and merge the branch into `main`.
17. Set the Notion ticket status to `Done`.

## Notes

- Keep the branch name and commit prefix aligned with the same ticket ID.
- If screenshots are not available, note that in the PR description.
- Try to squash commits when possible.
  - Example: If you have a commit like "checkstyle fix", squash it into the most relevant feature commit.