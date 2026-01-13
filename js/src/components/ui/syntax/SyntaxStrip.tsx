import ShikiHighlighter from "react-shiki";

export type SyntaxStripSize = "sm" | "md" | "lg";

export default function SyntaxStrip({
  name,
  language,
  size = "sm",
}: {
  name: string;
  language: string;
  size?: SyntaxStripSize;
}) {
  const s = (() => {
    switch (size) {
      case "sm":
        return "0.62em";
      case "md":
        return "0.72em";
      case "lg":
        return "0.85em";
    }
  })();

  return (
    <ShikiHighlighter
      theme="github-dark-dimmed"
      language={language}
      showLanguage={false}
      structure="inline"
      as="span"
      style={{
        display: "inline-block",
        overflow: "auto",
        minWidth: 0,
        maxWidth: "100%",
        margin: 0,
        marginRight: 16,
        padding: "1.5px 12px",
        borderRadius: "4px",
        fontSize: s,
        lineHeight: 1.15,
      }}
    >
      {name}
    </ShikiHighlighter>
  );
}
