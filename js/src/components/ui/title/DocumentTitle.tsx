import { getDocumentTitle } from "@/lib/helper/document";
import { useDocument } from "@/lib/hooks/useDocument";
import { useEffect } from "react";

/**
 * A custom React function that sets the title of the page.
 * The alternative would be to pollute the codebase with useEffects and repetitive checks, which are
 * unfavorable and hard to maintain.
 */
export default function DocumentTitle({ title }: { title: string }) {
  const { setTitle } = useDocument();

  useEffect(() => {
    // to toggle back when unmounting.
    const prevTitle = getDocumentTitle();

    setTitle(title);

    return () => {
      setTitle(prevTitle);
    };
  }, [setTitle, title]);

  return <></>;
}
