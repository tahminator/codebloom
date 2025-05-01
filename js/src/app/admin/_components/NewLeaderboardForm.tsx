import { Modal, Button } from "@mantine/core";
import { useState } from "react";

function NewLeaderboardForm() {
  const [isModalOpen, setModalOpen] = useState(false);
  const toggleModal = () => {
    setModalOpen((prev) => !prev);
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
        <div></div>
      </Modal>
    </div>
  );
}

export default NewLeaderboardForm;
