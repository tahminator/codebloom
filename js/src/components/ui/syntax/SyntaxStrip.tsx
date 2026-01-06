import SyntaxHighlighter from "react-syntax-highlighter";
import { vs2015 } from "react-syntax-highlighter/dist/esm/styles/hljs";

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
        return "0.75em";
      case "md":
        return "0.8em";
      case "lg":
        return "1em";
    }
  })();

  return (
    <SyntaxHighlighter
      language={language}
      style={vs2015}
      customStyle={{
        display: "inline-block",
        overflow: "auto",
        minWidth: 0,
        maxWidth: "100%",
        margin: 0,
        padding: "2px 6px",
        borderRadius: "4px",
        fontSize: s,
      }}
      wrapLongLines={true}
    >
      {name}
    </SyntaxHighlighter>
  );
}
