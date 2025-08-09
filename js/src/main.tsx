import BannerParent from "@/components/ui/banner/BannerParent";
import ReactQueryProvider from "@/lib/queryProvider";
import "@mantine/core/styles.css";
import ErrorReporter from "@/lib/reporting/ErrorReporter";
import { router } from "@/lib/router";
import "@mantine/notifications/styles.css";
import { themeOverride } from "@/lib/theme";
import { MantineProvider } from "@mantine/core";
import { Notifications } from "@mantine/notifications";

import "./index.css";

import "@mantine/dates/styles.css";
import * as Sentry from "@sentry/react";
import { ReactQueryDevtools } from "@tanstack/react-query-devtools";
import dayjs from "dayjs";
import customParseFormat from "dayjs/plugin/customParseFormat";
import { Fragment, StrictMode } from "react";
import { createRoot } from "react-dom/client";
import { RouterProvider } from "react-router";

dayjs.extend(customParseFormat);

if (import.meta.env.PROD) {
  Sentry.init({
    dsn: import.meta.env.VITE_DSN,
    // Setting this option to true will send default PII data to Sentry.
    // For example, automatic IP address collection on events
    sendDefaultPii: true,
  });
}

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <Fragment>
      <ReactQueryProvider>
        <MantineProvider theme={themeOverride} forceColorScheme={"dark"}>
          <BannerParent />
          <RouterProvider router={router} />
          <Notifications />
        </MantineProvider>
        <ReactQueryDevtools />
      </ReactQueryProvider>
    </Fragment>
  </StrictMode>,
);
