import UserProfileHeader from "@/app/user/[userId]/_components/UserProfile/UserProfileHeader";
import UserSubmissions from "@/app/user/[userId]/_components/UserSubmissions/UserSubmissions";
import Header from "@/components/ui/header/Header";
import { Button, Flex, Center } from "@mantine/core";
import { useParams, useNavigate } from "react-router-dom";

export default function UserProfilePage() {
  const { userId } = useParams();
  const navigate = useNavigate();

  return (
    <>
      <Header />
      <Flex
        direction={"column"}
        align={"center"}
        miw={"98vw"}
        mih={"90vh"}
        p={"lg"}
      >
        <UserProfileHeader userId={userId} />
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
