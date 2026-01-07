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
        return "0.62em";
      case "md":
        return "0.72em";
      case "lg":
        return "0.85em";
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
        marginRight: 16,
        padding: "1.5px 12px",
        borderRadius: "4px",
        fontSize: s,
        lineHeight: 1.15,
      }}
      wrapLongLines={true}
    >
      {name}
    </SyntaxHighlighter>
  );
}
