import { useAuthCallbackInfo, useAuthQuery } from "@/app/login/hooks";
import Toast from "@/components/ui/toast/Toast";
import ToastWithRedirect from "@/components/ui/toast/ToastWithRedirect";
import { Button, Loader } from "@mantine/core";
import { notifications } from "@mantine/notifications";
import { useEffect } from "react";
import { Link } from "react-router-dom";

export default function LoginPage() {
  // This handles any sort of callback info we get from the backend.
  const { success, message } = useAuthCallbackInfo();
  const { data, status } = useAuthQuery();

  useEffect(() => {
    if (typeof success === "boolean" && message) {
      notifications.show({
        message,
        color: success ? "green" : "red",
      });
    }
  }, [message, success]);

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

  if (authenticated) {
    return (
      <ToastWithRedirect
        to="/dashboard"
        message="You are already authenticated"
      />
    );
  }

  return (
    <div className="flex flex-col items-center justify-center w-screen h-screen">
      <Link to="/api/auth/flow/discord" reloadDocument>
        <Button>Login to Discord</Button>
      </Link>
    </div>
  );
}
