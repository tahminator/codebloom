import Banner from "@/components/ui/banner/Banner";
import ReactQueryProvider from "@/lib/queryProvider";
import { router } from "@/lib/router";
import "@mantine/core/styles.css";
import { themeOverride } from "@/lib/theme";
import { MantineProvider } from "@mantine/core";
import "@mantine/notifications/styles.css";
import { Notifications } from "@mantine/notifications";
import { ReactQueryDevtools } from "@tanstack/react-query-devtools";
import { StrictMode } from "react";

import "./index.css";

import { createRoot } from "react-dom/client";
import { RouterProvider } from "react-router";

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <ReactQueryProvider>
      <MantineProvider theme={themeOverride} forceColorScheme={"dark"}>
        <Banner
          message="The FIRST official leaderboard for the month of April will be starting very soon!"
          counter={new Date("2025-04-09T18:00:00-04:00")}
        />
        <RouterProvider router={router} />
        <Notifications />
      </MantineProvider>
      <ReactQueryDevtools />
    </ReactQueryProvider>
  </StrictMode>,
);
