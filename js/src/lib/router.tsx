import DashboardPage from "@/app/dashboard/Dashboard.page";
import LeaderboardPage from "@/app/leaderboard/Leaderboard.page";
import LoginPage from "@/app/login/Login.page";
import RootPage from "@/app/Root.page";
import { createBrowserRouter } from "react-router-dom";
import SwaggerUI from "swagger-ui-react";

export const router = createBrowserRouter([
  {
    path: "/",
    element: <RootPage />,
  },
  {
    path: "/login",
    element: <LoginPage />,
  },
  {
    path: "/dashboard",
    element: <DashboardPage />,
  },
  {
    path: "/leaderboard",
    element: <LeaderboardPage />,
  },
  {
    path: "/swagger",
    element: import.meta.env.DEV ? (
      <SwaggerUI url={"/v3/api-docs"} />
    ) : (
      <div>This endpoint is disabled in production.</div>
    ),
  },
]);
