import { useAuthQuery } from "@/app/login/hooks";
import UsernameForm from "@/app/onboarding/_components/UsernameForm";
import { Footer } from "@/components/ui/footer/Footer";
import Header from "@/components/ui/header/Header";
import Toast from "@/components/ui/toast/Toast";
import ToastWithRedirect from "@/components/ui/toast/ToastWithRedirect";
import { Loader } from "@mantine/core";

export default function Onboarding() {
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

  if (data?.user?.leetcodeUsername) {
    return (
      <ToastWithRedirect
        to="/dashboard"
        message="You have already onboarded your Leetcode Username!"
      />
    );
  }

  return (
    <>
      <Header />
      <UsernameForm />
      <Footer />
    </>
  );
}
