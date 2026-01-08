import { useJoinPartyMutation } from "@/lib/api/queries/duels";
import { Box, Button, Input } from "@mantine/core";
import { notifications } from "@mantine/notifications";
import { useState } from "react";
import { useNavigate } from "react-router-dom";

export default function PartyJoin() {
  const { mutate } = useJoinPartyMutation();
  const navigate = useNavigate();
  const [partyCode, setPartyCode] = useState("");

  const onJoin = (partyCode: string) => {
    mutate(
      { partyCode },
      {
        onSuccess: (data) => {
          if (data.success) {
            navigate(`/duel/${partyCode}`);
          } else {
            notifications.show({
              message: "Party Unavailable",
            });
          }
        },
      },
    );
  };

  return (
    <>
      <Input
        w={"100%"}
        px="lg"
        value={partyCode}
        onChange={(event) => setPartyCode(event.currentTarget.value)}
        styles={{
          input: {
            backgroundColor: "#333833",
            border: "0.2vw solid rgba(255,255,255,0.75)",
            textAlign: "center",
          },
        }}
      />
      <Box w="100%" px="lg">
        <Button w="100%" onClick={() => onJoin(partyCode)}>
          Join
        </Button>
      </Box>
    </>
  );
}
