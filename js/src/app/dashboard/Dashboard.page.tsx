import LeaderboardForDashboard from "@/app/dashboard/components/LeaderboardForDashboard/LeaderboardForDashboard";
import OnboardingLeetcodeUser from "@/app/dashboard/components/OnboardingLeetcodeUser";
import ProblemOfTheDay from "@/app/dashboard/components/ProblemOfTheDay/ProblemOfTheDay";
import RecentSubmissions from "@/app/dashboard/components/RecentSubmissions/RecentSubmissions";
import RefreshSubmissions from "@/app/dashboard/components/RefreshSubmissions";
import { useAuthQuery } from "@/app/login/hooks";
import { Footer } from "@/components/ui/footer/Footer";
import Header from "@/components/ui/header/Header";
import Toast from "@/components/ui/toast/Toast";
import ToastWithRedirect from "@/components/ui/toast/ToastWithRedirect";
import { Center, Flex, Loader, Title } from "@mantine/core";
import { useMediaQuery } from "@mantine/hooks";

export default function DashboardPage() {
  const smallPhone = useMediaQuery("(min-width: 48em)");
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
      <Flex
        p={"32px"}
        direction={"column"}
        gap={"lg"}
        style={{
          width: "98vw",
          minHeight: "90vh",
        }}
      >
        <Center>
          <Title order={2}>Dashboard</Title>
        </Center>
        <Center>
          {!data.user.leetcodeUsername ? (
            <OnboardingLeetcodeUser />
          ) : (
            <RefreshSubmissions />
          )}
        </Center>
        <Flex direction={smallPhone ? "row" : "column"} gap={"md"}>
          <Flex direction={"column"} flex={1}>
            <ProblemOfTheDay />
          </Flex>
          <Flex direction={"column"} flex={1}>
            <LeaderboardForDashboard userId={data.user.id} />
          </Flex>
          <Flex direction={"column"} flex={1}>
            <RecentSubmissions />
          </Flex>
        </Flex>
      </Flex>
      <Footer />
    </>
  );
}
