import Toast from "@/components/ui/toast/Toast";
import { useCurrentLeaderboardMetadataQuery } from "@/lib/api/queries/leaderboard";
import { Loader } from "@mantine/core";

import NewLeaderboardFormBody from "./NewLeaderBoardFormBody";

function NewLeaderboardForm() {
  const { data, status } = useCurrentLeaderboardMetadataQuery();

  if (status === "pending") {
    return (
      <div className="flex flex-col items-center justify-center w-screen h-screen">
        <Loader />
      </div>
    );
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

  const currentLeaderboardName = data.data.name;
  return (
    <NewLeaderboardFormBody currentLeaderboardName={currentLeaderboardName} />
  );
}

export default NewLeaderboardForm;
