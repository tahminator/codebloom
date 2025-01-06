import { useAuthQuery } from "@/app/login/hooks";
import { Button, Loader } from "@mantine/core";
import { notifications } from "@mantine/notifications";
import { useNavigate } from "react-router";
import { Link } from "react-router-dom";

export default function LoginPage() {
  const navigate = useNavigate();
  const { data, status } = useAuthQuery();

  if (status === "pending") {
    return (
      <div className="flex flex-col items-center justify-center w-screen h-screen">
        <Loader />
      </div>
    );
  }

  if (status === "error") {
    notifications.show({
      message: "Sorry, something went wrong.",
    });
    return <></>;
  }

  const authenticated = !!data.user && !!data.session;

  if (authenticated) {
    navigate("/dashboard");
    return <></>;
  }

  return (
    <div className="flex flex-col items-center justify-center w-screen h-screen">
      <Link to="/api/auth/flow/discord" reloadDocument>
        <Button>Login to Discord</Button>
      </Link>
    </div>
  );
}
