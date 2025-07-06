import { Footer } from "@/components/ui/footer/Footer";
import Header from "@/components/ui/header/Header";
import Toast from "@/components/ui/toast/Toast";
import ToastWithRedirect from "@/components/ui/toast/ToastWithRedirect";
import { useAuthQuery } from "@/lib/api/queries/auth";
import { Box, Center, Loader, Title } from "@mantine/core";

import SchoolVerifySettingsCard from "./_components/SchoolVerifySettingsCard";

export default function SettingsPage() {
  const { data, status } = useAuthQuery();

  if (status === "pending") {
    return (
      <div className="flex flex-col items-center justify-center w-screen h-screen">
        <Loader />
      </div>
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

  const schoolExists = data.user.schoolEmail !== null;

  return (
    <>
      <Header />
      <Box p={"lg"}>
        <Box>
          <Center>
            <Title order={3} p="md">
              Settings
            </Title>
          </Center>
          <SchoolVerifySettingsCard schoolExists={schoolExists} />
        </Box>
      </Box>
      <Footer />
    </>
  );
}
