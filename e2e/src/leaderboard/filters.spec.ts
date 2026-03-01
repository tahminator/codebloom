import { test, expect } from "@playwright/test";

test("filters are available when clicked on", async ({ page }) => {
  await page.goto("http://localhost:5173/");
  await page.getByTestId("transitional-button-Leaderboard").click();
  await page.getByRole("button", { name: "Filters" }).click();
  await expect(page.getByRole("menu", { name: "Filters" })).toBeVisible();
});
