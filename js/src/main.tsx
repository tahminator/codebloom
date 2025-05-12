import ReactQueryProvider from "@/lib/queryProvider";
import "@mantine/core/styles.css";
import { router } from "@/lib/router";
import { themeOverride } from "@/lib/theme";
import "@mantine/notifications/styles.css";
import { MantineProvider } from "@mantine/core";
import { Notifications } from "@mantine/notifications";
import { ReactQueryDevtools } from "@tanstack/react-query-devtools";

import "./index.css";

import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import { RouterProvider } from "react-router";

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
