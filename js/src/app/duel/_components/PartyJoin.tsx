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
        placeholder="Enter Party Code"
        value={partyCode}
        onChange={(event) => setPartyCode(event.currentTarget.value)}
        radius="10px"
        styles={{
          input: {
            textAlign: "center",
          },
        }}
      />
      <Box>
        <Button fullWidth radius="10px" onClick={() => onJoin(partyCode)}>
          Join
        </Button>
      </Box>
    </>
  );
}
