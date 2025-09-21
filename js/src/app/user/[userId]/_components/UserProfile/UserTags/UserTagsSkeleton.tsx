import { Skeleton } from "@mantine/core";

export default function UserTagsSkeleton() {
  return (
    <div className="flex flex-wrap gap-2">
      <Skeleton height={40} width={40} />
      <Skeleton height={40} width={40} />
      <Skeleton height={40} width={40} />
    </div>
  );
}
