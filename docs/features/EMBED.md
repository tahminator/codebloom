# Embeddable widgets

<img src="/screenshots/patina-codebloom-page.png" alt="Patina Network - Codebloom page" >
<div align="center">
    <span>
        <i>
            Embedded widget showcase used on
            <a href="https://patinanetwork.org/programs/codebloom">
                patinanetwork.org/programs/codebloom
            </a>
        </i>
    </span>
</div>
<br />

Codebloom currently has two embeddable routes, which are explained below. These routes have been properly secured against CSRF and XSS and as such, can be embedded into any website.

We manually add these routes via [SecurityConfig.java](./../../src/main/java/org/patinanetwork/codebloom/api/auth/security/SecurityConfig.java#L56-L65) in order to ensure that we don't accidentally open any more routes than we expected.

## `/embed/leaderboard`

This endpoint returns a custom leaderboard that is designed to be embeddable.

This endpoint supports a couple special properties via URL params:

- `pageSize` allows you to change the size of the pagination (default: `20`, max: `20`)
- `filterName=true` will only return the leaderboard for that specific filter instead. It will also render the icon of that specific integration next to our Codebloom (see left side of screenshot above).
  - You cannot use more than one `filterName` at a time. If you do, it will remove all `filterName` from the dashboard

[Goto `codebloom.patinanetwork.org/embed/leaderboard?patina=true&pageSize=5`](https://codebloom.patinanetwork.org/embed/leaderboard?patina=true&pageSize=5) to view the embed using some test parameters

## `/embed/potd`

This endpoint is a basic view that always returns the current Problem Of The Day (visit [`./POTD.md`](./POTD.md) to learn more).

Unlike the traditional Problem Of The Day that is visible on the dashboard page, this doesn't use any authenticated information to hide the Problem Of The Day if you have already solved it.

It is safe to assume that this endpoint will always show the current Problem Of The Day within the precision of a couple minutes.

[Goto `codebloom.patinanetwork.org/embed/potd`](https://codebloom.patinanetwork.org/embed/potd)
