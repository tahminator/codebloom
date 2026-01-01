import AboutUs from "@/app/_component/AboutUs";
import DocumentDescription from "@/components/ui/title/DocumentDescription";
import DocumentTitle from "@/components/ui/title/DocumentTitle";

export default function RootPage() {
  return (
    <>
      <DocumentTitle title={`CodeBloom`} />
      <DocumentDescription description={`CodeBloom - Welcome to CodeBloom!`} />
      <AboutUs />
    </>
  );
}
