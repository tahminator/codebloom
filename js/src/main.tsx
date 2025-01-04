import { themeOverride } from "@/lib/theme";
import { router } from "@/lib/router";
import { MantineProvider } from "@mantine/core";
import "@mantine/core/styles.css";
import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import { RouterProvider } from "react-router";
import "./index.css";
import ReactQueryProvider from "@/lib/query-provider";

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <ReactQueryProvider>
      <MantineProvider theme={themeOverride}>
        <RouterProvider router={router} />
      </MantineProvider>
    </ReactQueryProvider>
  </StrictMode>
);
