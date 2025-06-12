import { Skeleton } from "@mantine/core";

export default function NewLeaderboardFormSkeleton() {
  return (
    <div style={{ padding: "1rem" }}>
      <Skeleton height={40} width="100%" mb="xl">
        <div style={{ height: "40px" }} />
      </Skeleton>
    </div>
  );
}
