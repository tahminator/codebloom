import { Button } from "@mantine/core";
import { Link } from "react-router-dom";

export default function LoginButton({ className }: { className?: string }) {
  return (
    <Link to="/api/auth/flow/discord" reloadDocument>
      <Button fullWidth radius="md" className={className}>
        Login to Discord
      </Button>
    </Link>
  );
}
