import Toast from "@/components/ui/toast/Toast";
import ToastWithRedirect from "@/components/ui/toast/ToastWithRedirect";
import { useAuthQuery } from "@/lib/api/queries/auth";
import { Flex, Loader, Title } from "@mantine/core";

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
    <Flex w={"100vw"} h={"100vh"} direction={"column"}>
      <Title order={1} ta="center">
        Admin Page
      </Title>
      <Flex w={"100%"} direction={"row"}>
        <Flex w={"50%"} direction={"column"} ta={"center"}>
          <Title order={4}>Leaderboards (WIP)</Title>
        </Flex>
        <Flex w={"50%"} direction={"column"} ta={"center"}>
          <Title order={4}>Users (WIP)</Title>
        </Flex>
      </Flex>
    </Flex>
  );
}
