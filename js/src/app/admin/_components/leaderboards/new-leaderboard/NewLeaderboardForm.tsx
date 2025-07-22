import Toast from "@/components/ui/toast/Toast";
import { useCurrentLeaderboardMetadataQuery } from "@/lib/api/queries/leaderboard";

import NewLeaderboardFormBody from "./NewLeaderBoardFormBody";
import NewLeaderboardFormSkeleton from "./NewLeaderboardFormSkeleton";

function NewLeaderboardForm() {
  const { data, status } = useCurrentLeaderboardMetadataQuery();

  if (status === "pending") {
    return <NewLeaderboardFormSkeleton />;
  }

  if (status === "error") {
    return <Toast message={"Something went wrong."} />;
  }

  if (!data) {
    return <Toast message={"No leaderboard data found."} />;
  }
  if (!data.success) {
    return <Toast message={data.message} />;
  }

  const currentLeaderboardName = data.payload.name;
  return (
    <NewLeaderboardFormBody currentLeaderboardName={currentLeaderboardName} />
  );
}

export default NewLeaderboardForm;
