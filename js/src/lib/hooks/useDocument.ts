import {
  getDocumentDescription,
  getDocumentTitle,
} from "@/lib/helper/document";
import { useState, useCallback } from "react";

/**
 * A React hook for setting the page's title and description.
 *
 * Returns:
 *   - title: string
 *   - setTitle: (newValue: string) => void
 *   - description: string
 *   - setDescription: (newValue: string) => void
 */
export function useDocument() {
  const [title, _setTitle] = useState(() => getDocumentTitle());
  const [description, _setDescription] = useState(() =>
    getDocumentDescription(),
  );

  /**
   * If `newValue` is undefined, it will revert to the original title.
   */
  const setTitle = useCallback((newValue: string | undefined = "CodeBloom") => {
    document.title = newValue;
    _setTitle(newValue);
  }, []);

  /**
   * If `newValue` is undefined, it will revert to the original description.
   *
   */
  const setDescription = useCallback(
    (
      newValue:
        | string
        | undefined = "CodeBloom - The LeetCode leaderboard to help you get your dream job.",
    ) => {
      let desc = document.querySelector<HTMLMetaElement>(
        'meta[name="description"]',
      );
      if (!desc) {
        desc = document.createElement("meta");
        desc.name = "description";
        document.head.appendChild(desc);
      }
      desc.content = newValue;
      _setDescription(newValue);
    },
    [],
  );

  return {
    title,
    setTitle,
    description,
    setDescription,
  };
}
