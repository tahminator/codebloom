import ToastWithRedirect from "@/components/ui/toast/ToastWithRedirect";
import { useAuthQuery } from "@/lib/api/queries/auth";
import { useLeavePartyMutation } from "@/lib/api/queries/duels";
import { Box, Button, Flex, Loader } from "@mantine/core";
import { useParams } from "react-router";

export default function DuelPage() {
  const { lobbyCode } = useParams<{ lobbyCode: string }>();
  const { data, status } = useAuthQuery();
  const { mutate } = useLeavePartyMutation();

  if (status === "pending") {
    return (
      <Flex
        direction={"column"}
        align={"center"}
        justify={"center"}
        w={"98vw"}
        h={"90vh"}
      >
        <Loader />
      </Flex>
    );
  }

  if (status === "error") {
    return (
      <ToastWithRedirect to={-1} message={"Sorry, something went wrong."} />
    );
  }

  const authenticated = !!data.user && !!data.session;

  if (!authenticated) {
    return (
      <ToastWithRedirect to={"/login"} message={"Please log in to continue."} />
    );
  }

  const onboarded = !!data.user.leetcodeUsername;

  if (!onboarded) {
    return (
      <ToastWithRedirect
        to="/onboarding"
        message="Please finish the onboarding to gain access."
      />
    );
  }

  return (
    <Box>
      {lobbyCode} <Button onClick={() => mutate()}>Leave Party</Button>
    </Box>
  );
}
