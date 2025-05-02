import { AdminSchema } from "@/app/admin/_components/types";
import { ApiResponse } from "@/lib/api/common/apiResponse";
import { useCurrentLeaderboardMetadataQuery } from "@/lib/api/queries/leaderboard";
import { Modal, Button, TextInput } from "@mantine/core";
import { zodResolver, useForm } from "@mantine/form";
import { notifications } from "@mantine/notifications";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { z } from "zod";

function NewLeaderboardForm() {
  const [isModalOpen, setModalOpen] = useState(false);
  const { data, status } = useCurrentLeaderboardMetadataQuery();
  const queryClient = useQueryClient();
  const navigate = useNavigate();

  const currentLeaderboardName =
    status === "success" && data?.success && data?.data?.name ?
      data.data.name
    : "";

  const toggleModal = () => {
    setModalOpen((prev) => !prev);
  };

  const form = useForm({
    mode: "uncontrolled",
    validate: zodResolver(AdminSchema(currentLeaderboardName)),
    initialValues: {
      name: currentLeaderboardName,
      confirmation: "",
    },
  });
  const createLeaderboard = async ({ name }: { name: string }) => {
    const res = await fetch("/api/admin/leaderboard/create", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ name }),
    });

    const json = (await res.json()) as ApiResponse<object>;
    return json;
  };
  const { mutate } = useMutation({
    mutationFn: createLeaderboard,
    onSuccess: async (data) => {
      notifications.show({
        message: data.message,
        color: data.success ? undefined : "red",
      });

      if (data.success) {
        await queryClient.invalidateQueries({ queryKey: ["leaderboard"] });
        navigate("/admin");
      }
      return;
    },
  });

  const onSubmit = (values: z.infer<ReturnType<typeof AdminSchema>>) => {
    mutate({ name: values.name });
  };

  return (
    <div>
      <Button variant="outline" onClick={toggleModal}>
        Create
      </Button>
      <Modal
        opened={isModalOpen}
        onClose={toggleModal}
        title={"Create New Leaderboard"}
      >
        <form onSubmit={form.onSubmit(onSubmit)}>
          <TextInput
            {...form.getInputProps("confirmation")}
            label="Type leaderboard name"
            onKeyDown={(checking) => {
              if (checking.key === "Enter") checking.preventDefault();
            }}
          />
          <Button type="submit" size="xs" style={{ marginTop: "10px" }}>
            Submit
          </Button>
        </form>
      </Modal>
    </div>
  );
}

export default NewLeaderboardForm;
