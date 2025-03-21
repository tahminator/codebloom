# Frontend

## Development Server

```bash
pnpm run dev
```

# Autoformatters

Check out the autoformatter guide [here](https://github.com/tahminator/codebloom/tree/main/docs/autoformatters.md)

## Routing

All routes must be within the app directory.

The entry point for the route should always end in `.page.tsx` (ex. `Root.page.tsx`)

The folder names should represent the route. (ex. If you want to create a `/blog`, you should create a folder called `blog` inside of the `app` directory, then make a `CallMeAnything.page.tsx` file. Import that function into `/lib/router.tsx` to actually attach the routes.)

Dynamic routes, where a specific part of the route can be changed, can be created by covering the file name with a bracket.
(ex. If you want to create `/blog/:blogId`, you should create the folder like so `/app/blog/[blogId]`, then make a `*.Page.tsx` file. Make sure to import that function to `lib/router.tsx` to actually attach the routes. In order to use the route, you can use this function from the page-level entry point:

```tsx
const { blogId } = useParams();
```

If you need to utilize more complex routing behaviors, visit the docs [here](https://reactrouter.com/6.29.0/route/route).

To make it clear that a folder and ALL it's children is not part of the route, append a `_` at the start of the file route. (ex. `/app/dashboard/_components`)

Examples:

- `/app/Root.page.tsx` → `/`
- `/app/dashboard/Dashboard.page.tsx` → `/dashboard`
- `/app/submission/s/[submissionId]/SubmissionDetails.page.tsx` → `/submission/s/[submissionId]`

```tsx
export const router = createBrowserRouter([
    {
        path: "/",
        element: <RootPage />,
    },
    {
        path: "/dashboard",
        element: <DashboardPage />,
    },
    {
        path: "/submission/s/:submissionId",
        element: <SubmissionPage />,
    },
]);
```

## Naming Conventions

### TypeScript Files

- Regular ts files can be named in camelCase (ex. `customTypes.ts`)
- React files (.tsx) must be named in PascalCase (ex. `DashboardPage.tsx`)
- If a file doesn't need to be .tsx, then it should be a .ts file

### Function Naming

- React functions should be in PascalCase

    ```tsx
    export default function Dashboard() {}
    ```

- React hooks and any other function/constant should be in camelCase

    ```tsx
    const useAuthQuery = () => {};
    ```

## Folder Naming Conventions

You should try to limit folders to one word, but if you must require multiple words, you may use kebab-case (ex. `/lib/custom-types`)

## Separation of Concerns

You may use inline styles as long as it isn't deemed to be too long or complicated (at which you should be using `.module.css` files to separate off into.). Please read the section [below](#styling) on styling for more details.

You should put any custom hooks inside of a `hook.ts` file, and any custom types inside of a `types.ts` file. If you don't see any reason why the type may be re-used, you may put the file inside of the `/app` folder in the same route that it's used in. However, if you believe that the type may be re-used or would be easier to track down if in a central location (such as a database model type), put them in `/lib/types` or `/lib/hooks`.

## React Query

React Query should NEVER be created inline to a component. Instead, you should abstract the query into a custom hook so that if you need to call this query in another component, you can do so trivially.

```tsx
export const useFetchPotdQuery = () => {
    return useQuery({
        queryKey: ["potd", new Date().getDay()],
        queryFn: fetchPotd,
    });
};

async function fetchPotd() {
    const res = await fetch("/api/leetcode/potd");

    const json = (await res.json()) as ApiResponse<POTD>;

    return json;
}
```

All React Query functions should be placed inside of the `js/src/lib/queries` folder. You should try to match it a specific service so similar hooks can be found together, but this isn't strictly enforced.

## Comments

Do not leave comments within the JSX, unless you ABSOLUTELY have to. If you are in the situation where you feel like you have to, you should rethink your composition to reduce complexity.

Complicated hooks should have JSDoc comments at the top of the function like so:

```tsx
/**
 * A custom React hook that will attach the state to the URL params.
 * @param name The name of the key in the URL
 * Returns a stateful value and a function to update it.
 */
```

as well as comments inside of the function wherever necessary. A good example is [here](https://github.com/tahminator/codebloom/tree/main/js/src/lib/hooks/useUrlState.ts).

## Styling

You will get very far using the built-in components inside of Mantine, such as `Flex`, `Container`, `Box`, `Stack`. They are customizable so reach for the docs or reach out to Tahmid if you are confused about what the better choice may be between styling.

If you must, you may use inline styles via the style prop like so:

```tsx
<Text style={{ display: "inline" }} />
```

If the styling is very complicated, you may reach for CSS files, but only if you use `*.module.css` so that the styles don't bleed into the global scope. Module CSS files restrict the styling by renaming styles automatically at build time so that they do not bleed into the global namespace.

Tailwind is inside this project due to the ease of prototyping during development, but you shouldn't have to use it in production. Thereby, it is discouraged but not banned.

See examples:

- [Custom Hooks Example](https://github.com/tahminator/codebloom/tree/main/js/src/app/dashboard/hooks.ts)
- [Database Types Example](https://github.com/tahminator/codebloom/tree/main/js/src/lib/types/db)
