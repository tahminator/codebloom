import { AdminSchema } from "@/app/admin/_components/types"
import { Modal, Button, TextInput } from "@mantine/core";
import { zodResolver, useForm } from "@mantine/form";
import { useState } from "react";
import {z} from "zod";


function NewLeaderboardForm() {
  const [isModalOpen, setModalOpen] = useState(false);

  const form = useForm({
    mode: "uncontrolled",
    validate: zodResolver(AdminSchema),
    initialValues: {
      name: "",
    }
  });

  const toggleModal = () => {
    setModalOpen((prev) => !prev);
  };

  const OnSubmit = (data: z.infer<typeof AdminSchema>) => {
    console.log(data);
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
        <form onSubmit={form.onSubmit(OnSubmit)}>
          <TextInput {...form.getInputProps( "name")} label="Name" />
          <Button
            type="submit"
            size="xs"
            style={{ marginTop: "10px" }}
          >
            Submit
          </Button>
        </form>
      </Modal>
    </div>
  );
}
export default NewLeaderboardForm;
