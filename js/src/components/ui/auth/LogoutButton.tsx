import { Button } from "@mantine/core";
import { Link } from "react-router-dom";

export default function LogoutButton({ className }: { className?: string }) {
  return (
    <Link to="/api/auth/logout" reloadDocument>
      <Button className={className}>Logout</Button>
    </Link>
  );
}
