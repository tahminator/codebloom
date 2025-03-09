import DashboardPage from "@/app/dashboard/Dashboard.page";
import ErrorPage from "@/app/error/Error.page";
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
    errorElement: <ErrorPage />,
  },
  {
    path: "/login",
    element: <LoginPage />,
    errorElement: <ErrorPage />,
  },
  {
    path: "/dashboard",
    element: <DashboardPage />,
    errorElement: <ErrorPage />,
  },
  {
    path: "/leaderboard",
    element: <LeaderboardPage />,
    errorElement: <ErrorPage />,
  },
  {
    path: "/onboarding",
    element: <Onboarding />,
    errorElement: <ErrorPage />,
  },
  {
    path: "/submission/s/:submissionId",
    element: <SubmissionDetailsPage />,
    errorElement: <ErrorPage />,
  },
  {
    path: "/submission/u/:userId",
    element: <UserSubmissionsPage />,
    errorElement: <ErrorPage />,
  },
]);
