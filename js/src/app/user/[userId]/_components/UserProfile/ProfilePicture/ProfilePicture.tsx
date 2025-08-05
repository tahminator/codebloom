import Toast from "@/components/ui/toast/Toast";
import { useUserProfileQuery } from "@/lib/api/queries/user";
import { Avatar, Center } from "@mantine/core";
import { useParams } from "react-router";

import ProfilePictureSkeleton from "./ProfilePictureSkeleton";

export default function ProfilePicture() {
    const { userId } = useParams();
    const { data, status } = useUserProfileQuery({ userId });
    
    if (status === "pending") {
    return <ProfilePictureSkeleton />;
    }

    if (status === "error") {
    return (
        <Toast message="Sorry, something went wrong. Please try again later." />
    );
    }

    if (!data.success) {
    return <Toast message={data.message} />;
    }

    const user = data.payload;
    
    const initial =
    user.nickname ? user.nickname.charAt(0).toUpperCase() : null;
    

    return user.profileUrl ? (  
    <Center>
        <Avatar
            size={150}
            src={user.profileUrl}
            radius="sm"
            alt="avatar"
        />
    </Center>
    ) : (
    <Center>
        <Avatar
            size={150}
            radius="md"
            alt="avatar"
        >
            {initial}
        </Avatar>
    </Center>
        );
}