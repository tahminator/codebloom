import { useCreatePartyMutation } from "@/lib/api/queries/duels";
import { Box, Button } from "@mantine/core";
import { useNavigate } from "react-router";

export default function PartyCreate() {
  const { mutate } = useCreatePartyMutation();
  const navigate = useNavigate();

  const onCreate = () => {
    mutate(undefined, {
      onSuccess: (response) => {
        console.log(response);

        navigate(`/duel/${response.payload?.code}`);
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
