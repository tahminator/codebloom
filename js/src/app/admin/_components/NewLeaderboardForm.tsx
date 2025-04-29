import { Modal, Title, Button } from "@mantine/core";
import { useState } from "react";

function NewLeaderboardForm() {
  const [isModalOpen, setModalOpen] = useState(false);
  const toggleModal = () => {
    setModalOpen((prev) => !prev);
  };

  return (
    <div>
      <Button variant="outline" onClick={toggleModal}>
        Open Leaderboard(WIP)
      </Button>
      <Modal opened={isModalOpen} onClose={toggleModal}>
        <Title order={6} ta="center">
          {" "}
          Leaderboard (WIP){" "}
        </Title>
        <div></div>
      </Modal>
    </div>
  );
}

export default NewLeaderboardForm;
