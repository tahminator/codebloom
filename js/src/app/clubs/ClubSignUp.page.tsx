import { Footer } from "@/components/ui/footer/Footer";
import Header from "@/components/ui/header/Header";
import { Box } from "@mantine/core";

import ClubSignUp from "./_components/ClubSignUp";

export default function ClubSignupPage() {
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
        <ClubSignUp />
      </Box>

      <Footer />
    </Box>
  );
}
