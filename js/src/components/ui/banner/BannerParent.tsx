import Banner from "@/components/ui/banner/Banner";
import { useLatestAnnouncement } from "@/lib/api/queries/announcement";

export default function BannerParent() {
  const { data, status } = useLatestAnnouncement();

  if (status === "pending" || status === "error" || !data.success) {
    return <></>;
  }

  // null payload means announcement doesn't exist.
  if (!data.success || !data.payload) {
    return <></>;
  }

  const { message, showTimer, expiresAt } = data.payload;

  return (
    <Banner
      message={message}
      counter={showTimer ? new Date(expiresAt) : undefined}
    />
  );
}
