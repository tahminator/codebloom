import Banner from "@/components/ui/banner/Banner";
import { useLatestAnnouncement } from "@/lib/api/queries/announcement";

export default function BannerParent() {
  const { data, status } = useLatestAnnouncement();

  if (status !== "success" || !data || !data.success || !data.payload) {
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
