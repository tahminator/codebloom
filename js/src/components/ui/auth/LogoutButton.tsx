import { Button } from "@mantine/core";
import { Link } from "react-router-dom";

export default function LogoutButton({ className }: { className?: string }) {
  return (
    <Link to="/api/auth/logout" reloadDocument className="w-full">
      <Button className={className} fullWidth color="red">
        Logout
      </Button>
    </Link>
  );
}
