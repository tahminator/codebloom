import Toast from "@/components/ui/toast/Toast";
import ToastWithRedirect from "@/components/ui/toast/ToastWithRedirect";
import { Loader } from "@mantine/core";
import LogoutButton from "@/components/ui/auth/LogoutButton";
import { useAuthQuery } from "@/app/login/hooks";

export default function DashboardPage() {
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

  return (
    <div className="flex flex-col items-center justify-center w-screen h-screen">
      <LogoutButton />
    </div>
  );
}
