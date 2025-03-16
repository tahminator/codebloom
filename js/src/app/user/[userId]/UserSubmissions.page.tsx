import { useAuthQuery } from "@/app/login/hooks";
import UserSubmissionContent from "@/app/user/[userId]/_components/UserSubmissionContent";
import Toast from "@/components/ui/toast/Toast";
import ToastWithRedirect from "@/components/ui/toast/ToastWithRedirect";
import { Flex, Loader } from "@mantine/core";
import { useParams } from "react-router";

export default function UserSubmissionPage() {
  const { userId } = useParams();
  const { data, status } = useAuthQuery();

  if (status === "pending") {
    return (
      <Flex
        direction={"column"}
        align={"center"}
        justify={"center"}
        w={"100vw"}
        h={"100vh"}
      >
        <Loader />
      </Flex>
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

  return <UserSubmissionContent userId={userId} />;
}
