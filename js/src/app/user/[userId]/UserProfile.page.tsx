import Header from "@/components/ui/header/Header";
import { useParams } from "react-router";

import UserProfile from "./_components/UserProfile/UserProfile";

export default function UserProfilePage() {
  const { userId } = useParams();
  return (
    <>
      <Header />
      <UserProfile userId={userId} />
    </>
  );
}
