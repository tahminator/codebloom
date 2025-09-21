# Leetcode Client

## TL:DR

We have a Leetcode client that interfaces with leetcode.com's GraphQL API to fetch question data, user submissions, and other information. It handles authentication by automatically stealing session cookies via Playwright.

The main client can be found at [LeetcodeClientImpl.java](https://github.com/tahminator/codebloom/blob/main/src/main/java/com/patina/codebloom/common/leetcode/LeetcodeClientImpl.java), though we only use [ThrottledLeetcodeClient](https://github.com/tahminator/codebloom/blob/main/src/main/java/com/patina/codebloom/common/leetcode/throttled/ThrottledLeetcodeClientImpl.java) to avoid rate-limiting.

## Client Features

The `LeetcodeClient` interface provides these methods:

- `findQuestionBySlug` - Gets full question details including title, difficulty, acceptance rate, and topic tags
- `findSubmissionsByUsername` - Fetches recent accepted submissions for a user
- `findSubmissionDetailBySubmissionId` - Gets detailed submission info (runtime, memory, code)
- `getPotd` - Gets the Problem of the Day
- `getUserProfile` - Fetches user profile data (ranking, avatar, bio)
- `getAllTopicTags` - Returns all available question topic tags

## Authentication

We use a scheduled task (`LeetcodeAuthStealer`) that:

- Runs every 30 minutes to refresh session cookies
- Uses Playwright to automate GitHub OAuth login flow
- Stores the `LEETCODE_SESSION` cookie in our database
- Falls back to email alerts if authentication fails

This is necessary because leetcode.com requires authenticated requests for most API calls.

## Rate Limiting

We have a `ThrottledLeetcodeClient` that wraps the main client with rate limiting:

- Uses bucket4j for token bucket rate limiting
- Configured for 1 request per 100ms to avoid getting blocked
- Automatically queues requests when rate limit is hit

## GraphQL Queries

All API calls use GraphQL queries stored in the `queries` package:

- `SelectProblemQuery` - Get question details by slug
- `SelectAcceptedSubmisisonsQuery` - Get user's accepted submissions
- `GetSubmissionDetails` - Get submission runtime/memory details
- `GetPotd` - Get daily challenge question
- `GetUserProfile` - Get user profile info
- `GetTopics` - Get all topic tags

## Error Handling

The client throws `RuntimeException` for:

- HTTP status codes other than 200
- JSON parsing errors
- Network timeouts
- Authentication failures

All errors bubble up to the calling services which handle them appropriately.

## Usage in Codebase

The client is used throughout the app for:

- Fetching question data when users submit solutions
- Getting user profiles for leaderboards
- Syncing topic tags with our database
- Calculating scores based on submission details
- Daily challenge notifications

