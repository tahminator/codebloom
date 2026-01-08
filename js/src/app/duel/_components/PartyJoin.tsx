import { useJoinPartyMutation } from "@/lib/api/queries/duels";
import { Box, Button, Input } from "@mantine/core";
import { notifications } from "@mantine/notifications";
import { useState } from "react";
import { useNavigate } from "react-router-dom";

export default function PartyJoin() {
  const { mutate } = useJoinPartyMutation();
  const navigate = useNavigate();
  const [partyCode, setPartyCode] = useState("");

  const onJoin = (code: string) => {
    if (!code) {
      notifications.show({ message: "Please enter a party code" });
      return;
    }
    mutate(
      { partyCode: code },
      {
        onSuccess: (data) => {
          if (data.success) {
            navigate(`/duel/${code}`);
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
            borderRadius: "10px",
            fontSize: "clamp(1.9rem, 3vw, 2.2rem)",
            height: "10vh",
            backgroundColor: "#333833",
            border: "2px solid rgba(255,255,255,0.75)",
            textAlign: "center",
          },
        }}
      />
      <Box w="100%" px="lg">
        <Button
          w="100%"
          h="10vh"
          radius="10px"
          onClick={() => onJoin(partyCode)}
          style={{
            fontSize: "clamp(1.9rem, 3vw, 2.2rem)",
            backgroundColor: "#1c3513",
            border: "2px solid green",
            color: "white",
          }}
        >
          Join
        </Button>
      </Box>
    </>
  );
}
