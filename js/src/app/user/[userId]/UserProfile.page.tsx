import { useAuthQuery } from "@/app/login/hooks";
import UserProfileHeader from "@/app/user/[userId]/_components/UserProfile/UserProfileHeader";
import UserSubmissions from "@/app/user/[userId]/_components/UserSubmissions/UserSubmissions";
import { Footer } from "@/components/ui/footer/Footer";
import Header from "@/components/ui/header/Header";
import Toast from "@/components/ui/toast/Toast";
import ToastWithRedirect from "@/components/ui/toast/ToastWithRedirect";
import { Button, Flex, Loader } from "@mantine/core";
import { useParams } from "react-router";
import { Link } from "react-router-dom";

export default function UserProfilePage() {
  const { userId } = useParams();
  const { data, status } = useAuthQuery();

  if (status === "pending") {
    return (
      <>
        <Header />
        <Flex
          direction={"column"}
          align={"center"}
          justify={"center"}
          miw={"98vw"}
          mih={"90vh"}
        >
          <Loader />
        </Flex>
        <Footer />
      </>
    );
  }

  if (status === "error") {
    return (
      <>
        <Header />
        <Flex
          direction={"column"}
          align={"center"}
          justify={"center"}
          miw={"98vw"}
          mih={"90vh"}
        >
          <Toast message="Sorry, something went wrong." />
        </Flex>
        <Footer />
      </>
    );
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
