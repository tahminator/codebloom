import UserSubmissions from "@/app/user/[userId]/submissions/_components/UserSubmissions/UserSubmissions";
import ToastWithRedirect from "@/components/ui/toast/ToastWithRedirect";
import { Button, Flex, Center } from "@mantine/core";
import { useParams, useNavigate } from "react-router-dom";

export default function UserSubmissionsPage() {
  const { userId } = useParams();
  const navigate = useNavigate();

  if (!userId) {
    return <ToastWithRedirect to={-1} message={"This User ID is not valid."} />;
  }

  return (
    <>
      <Flex
        direction={"column"}
        align={"center"}
        miw={"98vw"}
        mih={"90vh"}
        p={"lg"}
      >
        <Center mt={"xs"}>
          <Button
            variant={"outline"}
            onClick={() => {
              navigate(-1);
            }}
          >
            ← Go back
          </Button>
        </Center>
        <UserSubmissions userId={userId} />
      </Flex>
    </>
  );
}
