/// <reference types="vitest/config" />
import react from "@vitejs/plugin-react";
import { defineConfig } from "vite";
import tsconfigPaths from "vite-tsconfig-paths";

// https://vite.dev/config/
export default defineConfig({
  plugins: [react(), tsconfigPaths()],
  envDir: "../",
  server: {
    proxy: {
      "/api": {
        target: "http://localhost:8080",
        // This is not traditional, but it allows the authenticated redirect to go back to the SPA.
        changeOrigin: false,
      },
    },
  },
  resolve: {
    alias: {
      "@tabler/icons-react": "@tabler/icons-react/dist/esm/icons/index.mjs",
    },
  },
  test: {
    globals: true,
    environment: "jsdom",
    coverage: {
      enabled: true,
      provider: "v8",
    },
    setupFiles: ["src/lib/test/defaults.tsx"],
  },
});
