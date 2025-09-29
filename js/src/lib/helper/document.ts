export const getDocumentTitle = () => {
  return document.title;
};

export const getDocumentDescription = () => {
  const desc = document.querySelector<HTMLMetaElement>(
    'meta[name="description"]',
  );
  return desc?.content;
};
