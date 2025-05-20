import ReactQueryProvider from "@/lib/queryProvider";
import { router } from "@/lib/router";
import "@mantine/core/styles.css";
import { themeOverride } from "@/lib/theme";
import { MantineProvider } from "@mantine/core";
import "@mantine/notifications/styles.css";
import { Notifications } from "@mantine/notifications";
import * as Sentry from "@sentry/react";
import { ReactQueryDevtools } from "@tanstack/react-query-devtools";

import "./index.css";

import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import { RouterProvider } from "react-router";

Sentry.init({
  dsn: import.meta.env.VITE_DSN,
  // Setting this option to true will send default PII data to Sentry.
  // For example, automatic IP address collection on events
  sendDefaultPii: true,
});

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <ReactQueryProvider>
      <MantineProvider theme={themeOverride} forceColorScheme={"dark"}>
        {/* <Banner */}
        {/*   message="The FIRST official leaderboard for the month of April will be ending very soon!" */}
        {/*   counter={new Date("2025-05-02T18:00:00-04:00")} */}
        {/* /> */}
        <RouterProvider router={router} />
        <Notifications />
      </MantineProvider>
      <ReactQueryDevtools />
    </ReactQueryProvider>
  </StrictMode>,
);
