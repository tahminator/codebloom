import AdminPage from "@/app/admin/admin.page";
import ClubSignUp from "@/app/clubs/ClubSignUp.page";
import DashboardPage from "@/app/dashboard/Dashboard.page";
import ErrorPage from "@/app/error/Error.page";
import LeaderboardWithIdPage from "@/app/leaderboard/[leaderboardId]/LeaderboardWithId.page";
import AllLeaderboardsPage from "@/app/leaderboard/all/AllLeaderboards.page";
import LeaderboardPage from "@/app/leaderboard/Leaderboard.page";
import LoginPage from "@/app/login/Login.page";
import Onboarding from "@/app/onboarding/Onboarding.page";
import RootPage from "@/app/Root.page";
import SettingsPage from "@/app/settings/Settings.page";
import SubmissionDetailsPage from "@/app/submission/[submissionId]/SubmissionDetails.page";
import UserProfilePage from "@/app/user/[userId]/UserProfile.page";
import UserSubmissionsPage from "@/app/user/[userId]/UserSubmissions.page";
import ToastWithRedirect from "@/components/ui/toast/ToastWithRedirect";
import { schoolFF } from "@/lib/ff";
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
    path: "/leaderboard/all",
    element: <AllLeaderboardsPage />,
    errorElement: <ErrorPage />,
  },
  {
    path: "/leaderboard/:leaderboardId",
    element: <LeaderboardWithIdPage />,
    errorElement: <ErrorPage />,
  },
  {
    path: "/onboarding",
    element: <Onboarding />,
    errorElement: <ErrorPage />,
  },
  {
    path: "/submission/:submissionId",
    element: <SubmissionDetailsPage />,
    errorElement: <ErrorPage />,
  },
  {
    path: "/user/:userId",
    element: <UserProfilePage />,
    errorElement: <ErrorPage />,
  },
  {
    path: "/user/:userId/submissions",
    element: <UserSubmissionsPage />,
    errorElement: <ErrorPage />,
  },
  {
    path: "/admin",
    element: <AdminPage />,
    errorElement: <ErrorPage />,
  },
  {
    path: "/settings",
    element:
      !schoolFF ?
        <ToastWithRedirect
          to={"/"}
          message={
            "Sorry, this is not available right now. Please try again later."
          }
        />
      : <SettingsPage />,
    errorElement: <ErrorPage />,
  },
  {
    path: "/club/:clubSlug",
    element: <ClubSignUp />,
    errorElement: <ErrorPage />,
  },
]);
