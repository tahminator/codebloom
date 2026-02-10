import { Skeleton } from "@mantine/core";

export default function ProfilePictureSkeleton() {
  return <Skeleton data-testid="user-profile-skeleton-picture" height={150} width={150} mx={"md"} />;
}
