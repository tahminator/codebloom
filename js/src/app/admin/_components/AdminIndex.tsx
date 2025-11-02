import NewAnnouncementModal from "@/app/admin/_components/announcements/NewAnnouncementModal";
import UserAdminList from "@/app/admin/_components/users/UserAdminList";
import { Flex, Title } from "@mantine/core";

import DeleteAnnouncementButton from "./announcements/DeleteAnnouncementButton";
import IncompleteQuestionList from "./incomplete/IncompleteQuestionList";
import AllLeaderboardAdmin from "./leaderboards/pagination/AllLeaderboardAdmin";

export default function AdminIndex() {
  return (
    <Flex w={"98vw"} mih={"100vh"} direction={"column"}>
      <Title order={1} ta="center">
        Admin Page
      </Title>
      <Flex w={"100%"} direction={"row"} justify={"center"}>
        <NewAnnouncementModal />
      </Flex>
      <Flex w={"100%"} direction={"row"} justify={"center"}>
        <DeleteAnnouncementButton />
      </Flex>
      <Flex w={"100%"} direction={{ base: "column", sm: "row" }}>
        <Flex
          w={{ base: "100%", sm: "50%" }}
          direction={"column"}
          ta={"center"}
          m={"sm"}
        >
          <Title order={4}>Leaderboards</Title>
          <AllLeaderboardAdmin />
        </Flex>
        <Flex
          w={{ base: "100%", sm: "50%" }}
          direction={"column"}
          ta={"center"}
          m={"sm"}
        >
          <Title order={4}>IncompleteQuestions</Title>
          <IncompleteQuestionList />
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
