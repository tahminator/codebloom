import BannerParent from "@/components/ui/banner/BannerParent";
import ReactQueryProvider from "@/lib/queryProvider";
import "@mantine/core/styles.css";
import "@/lib/helper/entries";
import { router } from "@/lib/router";
import { themeOverride } from "@/lib/theme";
import "@mantine/notifications/styles.css";
import { MantineProvider } from "@mantine/core";
import { Notifications } from "@mantine/notifications";
import * as Sentry from "@sentry/react";

import "./index.css";

import "@mantine/dates/styles.css";
import { ReactQueryDevtools } from "@tanstack/react-query-devtools";
import dayjs from "dayjs";
import customParseFormat from "dayjs/plugin/customParseFormat";
import utc from "dayjs/plugin/utc";
import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import { RouterProvider } from "react-router";

dayjs.extend(utc);
dayjs.extend(customParseFormat);

if (import.meta.env.PROD) {
  Sentry.init({
    dsn: import.meta.env.VITE_DSN,
    // Setting this option to true will send default PII data to Sentry.
    // For example, automatic IP address collection on events
    sendDefaultPii: true,
  });
}

// eslint-disable-next-line @typescript-eslint/no-non-null-assertion
createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <ReactQueryProvider>
      <MantineProvider theme={themeOverride} forceColorScheme={"dark"}>
        <BannerParent />
        <RouterProvider router={router} />
        <Notifications />
      </MantineProvider>
      <ReactQueryDevtools />
    </ReactQueryProvider>
  </StrictMode>,
);
