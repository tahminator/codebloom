import { Skeleton } from "@mantine/core";

export default function UserAdminListSkeleton() {
  return (
    <div style={{ padding: "2rem" }}>
      <Skeleton height={40} width="100%" mb="xl" />

      <div
        style={{
          display: "flex",
          justifyContent: "space-between",
          marginBottom: "1.5rem",
        }}
      >
        <Skeleton height={36} width={320} />
      </div>

      {[...Array(5)].map((_, i) => (
        <Skeleton key={i} height={60} mb="sm" radius="md">
          <div style={{ height: "60px" }} />
        </Skeleton>
      ))}

      <div
        style={{
          display: "flex",
          justifyContent: "center",
          gap: "0.5rem",
          marginTop: "2rem",
        }}
      >
        {[...Array(6)].map((_, i) => (
          <Skeleton key={i} height={36} width={36} />
        ))}
      </div>
    </div>
  );
}
