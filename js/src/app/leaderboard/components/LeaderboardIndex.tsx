import Toast from "@/components/ui/toast/Toast";
import { useFullLeaderboardEntriesQuery } from "@/app/leaderboard/hooks";
import { Loader } from "@mantine/core";

export default function LeaderboardIndex() {
  const { data, status } = useFullLeaderboardEntriesQuery();

  if (status === "pending") {
    return (
      <div className="flex flex-col items-center justify-center w-screen h-screen">
        <Loader />
      </div>
    );
  }

  if (status === "error") {
    return <Toast message="Sorry, something went wrong." />;
  }

  return (
    <div className="p-4">
      <h1 className="text-2xl font-bold mb-4">Leaderboard</h1>
      <pre className=" p-4 rounded shadow overflow-x-auto">
        {JSON.stringify(data.leaderboard, null, 2)}
      </pre>
    </div>
  );
}
