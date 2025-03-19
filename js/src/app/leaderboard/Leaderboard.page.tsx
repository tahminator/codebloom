import Leaderboard from "@/app/leaderboard/_components/Leaderboard";
import { Footer } from "@/components/ui/footer/Footer";
import Header from "@/components/ui/header/Header";

export default function LeaderboardPage() {
  return (
    <div className="flex flex-col min-h-screen">
      <Header />
      <div className="flex-grow">
        <Leaderboard />
      </div>
      <Footer />
    </div>
  );
}
