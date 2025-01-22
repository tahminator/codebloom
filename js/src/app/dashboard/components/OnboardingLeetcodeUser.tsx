import { Button } from "@mantine/core";
import { Link } from "react-router-dom";

export default function OnboardingButton() {
  return (
    <div
      style={{
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
      }}
    >
      <Link to="/onboarding">
        <Button radius="md" size="md">
          Get Onboarded
        </Button>
      </Link>
    </div>
  );
}
