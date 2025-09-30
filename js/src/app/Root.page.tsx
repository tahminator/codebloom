import AboutUs from "@/app/_component/AboutUs";
import { Footer } from "@/components/ui/footer/Footer";
import Header from "@/components/ui/header/Header";
import DocumentDescription from "@/components/ui/title/DocumentDescription";
import DocumentTitle from "@/components/ui/title/DocumentTitle";

export default function RootPage() {
  return (
    <>
      <DocumentTitle title={`CodeBloom`} />
      <DocumentDescription
        description={`CodeBloom - Welcome to CodeBloom!`}
      />
      <Header />
      <AboutUs />
      <Footer />
    </>
  );
}
