import { Modal, Title } from '@mantine/core';
import { useState } from 'react';



function NewLeaderboardForm() {
    const [isModalOpen, setModalOpen] = useState(false);
    const toggleModal = () => {
        setModalOpen(prev => !prev);
    };

    return (
        <div>
            <button onClick={toggleModal}>Toggle Modal</button>
            <Modal opened={isModalOpen} onClose={toggleModal} >
            < Title order={6} ta="center"> Leaderboard (WIP) </Title>
                <div></div>
            </Modal>
        </div>
    );
}

export default NewLeaderboardForm;