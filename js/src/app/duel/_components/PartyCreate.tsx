import { useCreatePartyMutation } from "@/lib/api/queries/duels";
import { Box, Button } from "@mantine/core";
import { notifications } from "@mantine/notifications";

export default function PartyCreate() {
  const { mutate } = useCreatePartyMutation();

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
