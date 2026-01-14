import ReactQueryProvider from "@/lib/queryProvider";
import "@mantine/core/styles.css";
import "@/patches";
import { router } from "@/lib/router";
import { themeOverride } from "@/lib/theme";
import "@mantine/notifications/styles.css";
import { MantineProvider } from "@mantine/core";
import { Notifications } from "@mantine/notifications";
import "@/index.css";
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

// eslint-disable-next-line @typescript-eslint/no-non-null-assertion
createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <ReactQueryProvider>
      <MantineProvider theme={themeOverride} forceColorScheme={"dark"}>
        <RouterProvider router={router} />
        <Notifications />
      </MantineProvider>
      <ReactQueryDevtools />
    </ReactQueryProvider>
  </StrictMode>,
);
