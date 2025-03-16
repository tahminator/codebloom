import { Skeleton } from "@mantine/core";

// TODO - Make this nicer
export default function UserSubmissionsSkeleton() {
  // <Box style={{ overflowX: "auto" }} maw={"100%"} miw={"66%"}>
  return <Skeleton width={1000} height={2000} m={"lg"} />;
}
