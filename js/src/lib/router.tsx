import DashboardPage from "@/app/dashboard/Dashboard.page";
import LeaderboardPage from "@/app/leaderboard/Leaderboard.page";
import LoginPage from "@/app/login/Login.page";
import Onboarding from "@/app/onboarding/Onboarding.page";
import RootPage from "@/app/Root.page";
import SubmissionDetailsPage from "@/app/submission/s/[submissionId]/SubmissionDetails.page";
import UserSubmissionsPage from "@/app/submission/u/[userId]/UserSubmissions.page";
import { createBrowserRouter } from "react-router-dom";

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
    path: "/onboarding",
    element: <Onboarding />,
  },
  {
    path: "/submission/s/:submissionId",
    element: <SubmissionDetailsPage />,
  },
  {
    path: "/submission/u/:userId",
    element: <UserSubmissionsPage />,
  },
]);
