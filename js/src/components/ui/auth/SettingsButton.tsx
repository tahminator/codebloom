import { Button } from "@mantine/core";
import { Link } from "react-router-dom";

export default function SettingsButton({ className }: { className?: string }) {
  return (
    <Link to="/settings" className="w-full">
      <Button className={className} fullWidth color="gray">
        Settings
      </Button>
    </Link>
  );
}
