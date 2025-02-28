import { Button } from "@mantine/core";
import { Link } from "react-router-dom";

export default function OnboardingButton() {
  return (
    <Link to="/onboarding">
      <Button radius="md" size="md" className="shadow-xl shadow-green-800">
        Get Onboarded
      </Button>
    </Link>
  );
}
