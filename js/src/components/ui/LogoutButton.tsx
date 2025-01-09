import { Button } from "@mantine/core";
import { Link } from "react-router-dom";

export default function LogoutButton() {
  return (
    <Link to="/api/auth/logout" reloadDocument>
      <Button>Logout</Button>
    </Link>
  );
}
