import { Button, Loader } from "@mantine/core";
import { Link } from "react-router-dom";
import { useAuthCallbackInfo, useAuthQuery } from "@/app/login/hooks";
import ToastWithRedirect from "./toast/ToastWithRedirect";
import { useEffect } from "react";
import { notifications } from "@mantine/notifications";
import Toast from "./toast/Toast";

export default function LogoutButton() {
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

  if (!authenticated) {
    return (
      <ToastWithRedirect to="/login" message="You are not authenticated!" />
    );
  }
  return (
    <div className="flex flex-col items-center justify-center w-screen h-screen">
      <Link to="/api/auth/logout" reloadDocument>
        <Button>Logout</Button>
      </Link>
    </div>
  );
}
