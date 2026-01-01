import AdminPage from "@/app/admin/admin.page";
import ClubSignUp from "@/app/club/[clubSlug]/ClubSignUp.page";
import DashboardPage from "@/app/dashboard/Dashboard.page";
import DuelPage from "@/app/duel/[lobbyCode]/Duel.page";
import PartyCreationPage from "@/app/duel/create/PartyCreation.page";
import LeaderboardEmbed from "@/app/embed/leaderboard/LeaderboardEmbed";
import PotdEmbed from "@/app/embed/potd/PotdEmbed";
import ErrorPage from "@/app/error/Error.page";
import LeaderboardWithIdPage from "@/app/leaderboard/[leaderboardId]/LeaderboardWithId.page";
import AllLeaderboardsPage from "@/app/leaderboard/all/AllLeaderboards.page";
import LeaderboardPage from "@/app/leaderboard/Leaderboard.page";
import LoginPage from "@/app/login/Login.page";
import Onboarding from "@/app/onboarding/Onboarding.page";
import PolicyPage from "@/app/privacy/Policy.page";
import RootPage from "@/app/Root.page";
import SettingsPage from "@/app/settings/Settings.page";
import SubmissionDetailsPage from "@/app/submission/[submissionId]/SubmissionDetails.page";
import UserSubmissionsPage from "@/app/user/[userId]/submissions/UserSubmissions.page";
import UserProfilePage from "@/app/user/[userId]/UserProfile.page";
import PageShell from "@/components/ui/page/PageShell";
import ToastWithRedirect from "@/components/ui/toast/ToastWithRedirect";
import { duelFF, schoolFF } from "@/lib/ff";
import { createBrowserRouter } from "react-router-dom";

export const router = createBrowserRouter([
  {
    path: "/",
    element: <PageShell children={<RootPage />} />,
    errorElement: <ErrorPage />,
  },
  {
    path: "/login",
    element: <PageShell children={<LoginPage />} hideHeader hideFooter />,
    errorElement: <ErrorPage />,
  },
  {
    path: "/dashboard",
    element: <PageShell children={<DashboardPage />} />,
    errorElement: <ErrorPage />,
  },
  {
    path: "/leaderboard",
    element: <PageShell children={<LeaderboardPage />} />,
    errorElement: <ErrorPage />,
  },
  {
    path: "/leaderboard/all",
    element: <PageShell children={<AllLeaderboardsPage />} />,
    errorElement: <ErrorPage />,
  },
  {
    path: "/leaderboard/:leaderboardId",
    element: <PageShell children={<LeaderboardWithIdPage />} />,
    errorElement: <ErrorPage />,
  },
  {
    path: "/onboarding",
    element: <PageShell children={<Onboarding />} />,
    errorElement: <ErrorPage />,
  },
  {
    path: "/submission/:submissionId",
    element: <PageShell children={<SubmissionDetailsPage />} />,
    errorElement: <ErrorPage />,
  },
  {
    path: "/user/:userId",
    element: <PageShell children={<UserProfilePage />} />,
    errorElement: <ErrorPage />,
  },
  {
    path: "/user/:userId/submissions",
    element: <PageShell children={<UserSubmissionsPage />} />,
    errorElement: <ErrorPage />,
  },
  {
    path: "/embed/leaderboard",
    element: (
      <PageShell children={<LeaderboardEmbed />} hideHeader hideFooter />
    ),
    errorElement: <ErrorPage />,
  },
  {
    path: "/embed/potd",
    element: <PageShell children={<PotdEmbed />} hideHeader hideFooter />,
    errorElement: <ErrorPage />,
  },
  {
    path: "/admin",
    element: <PageShell children={<AdminPage />} />,
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
      : <PageShell children={<SettingsPage />} />,
    errorElement: <ErrorPage />,
  },
  {
    path: "/privacy",
    element: <PageShell children={<PolicyPage />} />,
    errorElement: <ErrorPage />,
  },
  {
    path: "/club/:clubSlug?",
    element: <PageShell children={<ClubSignUp />} />,
    errorElement: <ErrorPage />,
  },
  {
    path: "/privacy",
    element: <PageShell children={<PolicyPage />} />,
    errorElement: <ErrorPage />,
  },
  {
    path: "/duel/create",
    element:
      duelFF ?
        <PageShell children={<PartyCreationPage />} />
      : <ToastWithRedirect
          to={"/"}
          message={
            "Sorry, this is not available right now. Please try again later."
          }
        />,
    errorElement: <ErrorPage />,
  },
  {
    path: "/duel/:lobbyCode",
    element:
      duelFF ?
        <PageShell children={<DuelPage />} />
      : <ToastWithRedirect
          to={"/"}
          message={
            "Sorry, this is not available right now. Please try again later."
          }
        />,
    errorElement: <ErrorPage />,
  },
]);
