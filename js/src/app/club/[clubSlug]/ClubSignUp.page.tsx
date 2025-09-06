import { Footer } from "@/components/ui/footer/Footer";
import Header from "@/components/ui/header/Header";
import Toast from "@/components/ui/toast/Toast";
import ToastWithRedirect from "@/components/ui/toast/ToastWithRedirect";
import { useAuthQuery } from "@/lib/api/queries/auth";
import { Box, Loader } from "@mantine/core";

import ClubSignUp from "./_components/ClubSignUpForm";

export default function ClubSignupPage() {
  const { data, status } = useAuthQuery();

  if (status === "pending") {
    return <Loader />;
  }
  if (status === "error") {
    return (
      <Toast message="Sorry, something went wrong. Please try again later." />
    );
  }

  const authenticated = !!data.user && !!data.session;
  if (!authenticated) {
    return (
      <ToastWithRedirect to="/login" message="You are not authenticated!" />
    );
  }

  return (
    <Box
      style={{
        minHeight: "100vh",
        display: "flex",
        flexDirection: "column",
      }}
    >
      <Header />
      <Box>
        <ClubSignUp {...data.user} />
      </Box>
      <Footer />
    </Box>
  );
}
