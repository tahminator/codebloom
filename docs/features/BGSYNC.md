# Background Sync

## Submissions

Codebloom will automatically sync your submissions from [leetcode.com](https://leetcode.com/) in the background once you have signed up and completed the short Codebloom onboarding.

_Last updated: 02/15/2026_

This background sync runs approximately every 30 minutes.

## Gotchas

The full submission flow is somewhat asynchronous, which can lead to some confusing behavior sometimes where a user might have a submission registered, but their code is not visible.

This is because when a user's submissions are fetched, we retrieve their 20 most recent submissions from [leetcode.com](https://leetcode.com) (20 is the maximum we can get at a given time).

These 20 submissions are automatically parsed out by our scorer logic (see [`./SCORES.md`](./SCORES.md) for more details).

However, these 20 submissions retrieved from [leetcode.com](https://leetcode.com) do not include `code`, `runtime`, `memory`, and more. To retrieve these fields, you must make another call - one for each submission.

This is a problem because even with only 200 users \* 20 submissions = 400 calls in a short period of time. This was causing us to get rate-limited hard by [leetcode.com](https://leetcode.com), especially since this is considered a sensitive query, and as such, I made the decision to split this flow into two parts:

1. Get submissions, process immediately and provide points
    - We want to provide immediate and fast feedback + this network call is cheap and inexpensive.
2. For each submission, create a `Job` entry that points to `Question`
    - This kicks off a background service that is listening for any notifications via PostgreSQL `NOTIFY/LISTEN`.
    - Once a notification is received, it wakes the `Job` processor up and will continue to process `Job` entries until there is none left.
    - This processor is slowed down much more since we usually have a huge batch of requests and there is not as much of a need to process these requests quickly

Before this implementation, we were frequently getting rate-limited at 50 users - now we are at 235 and we have not faced said problem again since.
