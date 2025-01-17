import { useAuthQuery } from "@/app/login/hooks";
import Header from "@/components/ui/header/Header";
import Toast from "@/components/ui/toast/Toast";
import ToastWithRedirect from "@/components/ui/toast/ToastWithRedirect";
import { Loader } from "@mantine/core";
import LeaderboardIndex from "@/app/leaderboard/components/LeaderboardIndex";
import { Footer } from "@/components/ui/footer/Footer";

export default function LeaderboardPage() {
  const { data, status } = useAuthQuery();

  if (status === "pending") {
    return (
      <div className="flex flex-col items-center justify-center w-screen h-screen">
        <Loader />
      </div>
    );
  }

  if (status === "error") {
    return <Toast message="Sorry, something went wrong." />;
  }

  const authenticated = !!data.user && !!data.session;

  if (!authenticated) {
    return (
      <ToastWithRedirect to="/login" message="You are not authenticated!" />
    );
  }

  return (
    <>
      <Header />
      <LeaderboardIndex />
      <Footer />
    </>
  );
}
