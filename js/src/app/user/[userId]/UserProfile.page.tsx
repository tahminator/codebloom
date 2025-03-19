import UserProfileHeader from "@/app/user/[userId]/_components/UserProfile/UserProfileHeader";
import UserSubmissions from "@/app/user/[userId]/_components/UserSubmissions/UserSubmissions";
import Header from "@/components/ui/header/Header";
import { Button, Flex } from "@mantine/core";
import { useParams } from "react-router";
import { Link } from "react-router-dom";

export default function UserProfilePage() {
  const { userId } = useParams();

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
        <Button component={Link} to={"/dashboard"} variant={"outline"}>
          Go back to dashboard
        </Button>
        <UserSubmissions userId={userId} />
      </Flex>
    </>
  );
}
