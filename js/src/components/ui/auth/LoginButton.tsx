import { Button } from "@mantine/core";
import { Link } from "react-router-dom";

export default function LogoutButton({ className }: { className?: string }) {
  return (
    <Link to="/api/auth/flow/discord" reloadDocument>
      <Button className={className}>Login to Discord</Button>
    </Link>
  );
}
