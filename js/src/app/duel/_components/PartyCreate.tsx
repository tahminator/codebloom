import { useCreatePartyMutation } from "@/lib/api/queries/duels";
import { Box, Button } from "@mantine/core";
import { notifications } from "@mantine/notifications";
import { useQueryClient } from "@tanstack/react-query";
import { useNavigate } from "react-router";

export default function PartyCreate() {
  const { mutate } = useCreatePartyMutation();

  const queryClient = useQueryClient();
  const navigate = useNavigate();

  const onCreate = () => {
    mutate(undefined, {
      onSuccess: async (data) => {
        if (!data.success) {
          notifications.show({
            message: data.message,
            color: "red",
          });
          return;
        }

        await queryClient.invalidateQueries({ queryKey: ["party"] });
        navigate(`/duel/current`);
      },
    });
  };

  return (
    <Box w="100%">
      <Button fullWidth radius="md" onClick={onCreate}>
        Create
      </Button>
    </Box>
  );
}
