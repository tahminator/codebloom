import NewAnnouncementModal from "@/app/admin/_components/announcements/NewAnnouncementModal";
import UserAdminList from "@/app/admin/_components/UserAdminList";
import Toast from "@/components/ui/toast/Toast";
import ToastWithRedirect from "@/components/ui/toast/ToastWithRedirect";
import { useAuthQuery } from "@/lib/api/queries/auth";
import { Flex, Loader, Title } from "@mantine/core";

import AllLeaderboardsPage from "./AllLeaderboardAdmin";

export default function AdminIndex() {
  const { data, status } = useAuthQuery();
  if (status == "pending") {
    return (
      <div className="flex flex-col items-center justify-center w-screen h-screen">
        <Loader />
      </div>
    );
  }
  if (status == "error") {
    return <Toast message={"Something went wrong."} />;
  }
  const { isAdmin } = data;
  if (!isAdmin) {
    return (
      <ToastWithRedirect
        message={"You are not authorized to view this page."}
        to="/"
      />
    );
  }
  return (
    <Flex w={"98vw"} h={"100vh"} direction={"column"}>
      <Title order={1} ta="center">
        Admin Page
      </Title>
      <Flex w={"100%"} direction={"row"} justify={"center"}>
        <NewAnnouncementModal />
      </Flex>
      <Flex w={"100%"} direction={{ base: "column", sm: "row" }}>
        <Flex
          w={{ base: "100%", sm: "50%" }}
          direction={"column"}
          ta={"center"}
          m={"sm"}
        >
          <Title order={4}>Leaderboards</Title>
          <AllLeaderboardsPage />
        </Flex>
        <Flex
          w={{ base: "100%", sm: "50%" }}
          direction={"column"}
          ta={"center"}
          m={"sm"}
        >
          <Title order={4}>Users</Title>
          <UserAdminList />
        </Flex>
      </Flex>
    </Flex>
  );
}
