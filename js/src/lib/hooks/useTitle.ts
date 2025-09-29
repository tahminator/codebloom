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
export function useTitle() {
  const [title, _setTitle] = useState(() => document.title);
  const [description, _setDescription] = useState(() => {
    const desc = document.querySelector<HTMLMetaElement>(
      'meta[name="description"]',
    );
    return desc?.content;
  });

  const setTitle = useCallback((newValue: string) => {
    document.title = newValue;
    _setTitle(newValue);
  }, []);

  const setDescription = useCallback((newValue: string) => {
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
  }, []);

  return {
    title,
    setTitle,
    description,
    setDescription,
  };
}
