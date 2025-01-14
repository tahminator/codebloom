import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";
import tsconfigPaths from "vite-tsconfig-paths";

// https://vite.dev/config/
export default defineConfig({
  plugins: [react(), tsconfigPaths()],
  server: {
    proxy: {
      "/api": {
        target: "http://localhost:8080",
        // This is not traditional, but it allows the authenticated redirect to go back to the SPA.
        changeOrigin: false,
      },
      "/swagger": {
        // OpenAPI route
        target: "http://localhost:8080",
        changeOrigin: false,
      },
      "/v3": {
        target: "http://localhost:8080",
        changeOrigin: true,
      },
    },
  },
});
