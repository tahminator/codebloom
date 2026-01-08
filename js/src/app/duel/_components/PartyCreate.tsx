import { useCreatePartyMutation } from "@/lib/api/queries/duels";
import { Box, Button } from "@mantine/core";
import { notifications } from "@mantine/notifications";
import { useNavigate } from "react-router";

export default function PartyCreate() {
  const { mutate } = useCreatePartyMutation();
  const navigate = useNavigate();

  const onCreate = () => {
    mutate(undefined, {
      onSuccess: (response) => {
        const code = response.payload?.code;
        if (code) {
          navigate(`/duel/${code}`);
        } else {
          notifications.show({ message: "Failed to create party" });
        }
      },
    });
  };

  return (
    <Box w="100%" px="lg">
      <Button
        w="100%"
        h="10vh"
        radius="10px"
        onClick={onCreate}
        style={{
          fontSize: "clamp(1.9rem, 3vw, 2.2rem)",
          backgroundColor: "#1c3513",
          border: "2px solid green",
          color: "white",
        }}
      >
        Create
      </Button>
    </Box>
  );
}
