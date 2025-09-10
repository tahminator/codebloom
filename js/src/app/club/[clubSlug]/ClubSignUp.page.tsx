import { Footer } from "@/components/ui/footer/Footer";
import Header from "@/components/ui/header/Header";
import Toast from "@/components/ui/toast/Toast";
import ToastWithRedirect from "@/components/ui/toast/ToastWithRedirect";
import { useAuthQuery } from "@/lib/api/queries/auth";
import { Box, Loader } from "@mantine/core";
import { useParams } from "react-router-dom";

import ClubSignUp from "./_components/ClubSignUpForm";

export default function ClubSignupPage() {
  const { data, status } = useAuthQuery();
  const { clubSlug } = useParams<{ clubSlug: string }>();

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

  // Potentially return a club list page in the future
  if (clubSlug == undefined) {
    return <ToastWithRedirect to="/" message="Club Slug missing!" />;
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
        <ClubSignUp
          id={data.user.id}
          tags={data.user.tags}
          clubSlug={clubSlug}
        />
      </Box>
      <Footer />
    </Box>
  );
}
