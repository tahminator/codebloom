import { useTitle } from "@/lib/hooks/useTitle";
import { useEffect } from "react";

/**
 * A custom React function that sets the description of the page.
 * The alternative would be to pollute the codebase with useEffects and repetitive checks, which are
 * unfavorable and hard to maintain.
 */
export default function Title({ description }: { description: string }) {
  const { setDescription } = useTitle();

  useEffect(() => {
    setDescription(description);
  }, [setDescription, description]);

  return <></>;
}
