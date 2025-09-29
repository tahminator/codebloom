import { getDocumentDescription } from "@/lib/helper/document";
import { useDocument } from "@/lib/hooks/useDocument";
import { useEffect } from "react";

/**
 * A custom React function that sets the description of the page.
 * The alternative would be to pollute the codebase with useEffects and repetitive checks, which are
 * unfavorable and hard to maintain.
 */
export default function DocumentDescription({
  description,
}: {
  description: string;
}) {
  const { setDescription } = useDocument();

  useEffect(() => {
    // so we can undo on unmount.
    const prevDescription = getDocumentDescription();

    setDescription(description);

    return () => {
      setDescription(prevDescription);
    };
  }, [setDescription, description]);

  return <></>;
}
