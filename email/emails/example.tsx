import {
  Heading,
  Html,
  Link,
  Preview,
  Section,
  Text,
} from "@react-email/components";

const styles = {
  body: {
    backgroundColor: "#f6f9fc",
    fontFamily:
      "-apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif",
  },
  container: {
    backgroundColor: "#ffffff",
    margin: "40px auto",
    padding: "20px",
    borderRadius: "6px",
    maxWidth: "600px",
  },
  headerBadge: {
    backgroundColor: "#0070f3",
    borderRadius: "4px",
    color: "white",
    display: "inline-block",
    fontSize: "12px",
    fontWeight: "bold",
    padding: "4px 8px",
    textTransform: "uppercase" as const,
  },
  h1: {
    color: "#333333",
    fontSize: "24px",
    fontWeight: "bold",
    margin: "20px 0",
  },
  paragraph: {
    color: "#555555",
    fontSize: "16px",
    lineHeight: "24px",
    margin: "10px 0",
  },
  button: {
    backgroundColor: "#0070f3",
    borderRadius: "4px",
    color: "white",
    display: "inline-block",
    fontSize: "16px",
    fontWeight: "bold",
    padding: "12px 24px",
    textDecoration: "none",
  },
  urlBox: {
    backgroundColor: "#f0f0f0",
    borderRadius: "4px",
    color: "#555555",
    fontSize: "14px",
    marginTop: "20px",
    padding: "10px",
    wordBreak: "break-all" as const,
  },
};

interface ExampleEmailProps {
  recipientName: string;
  verifyUrl: string;
  supportEmail: string;
}

export default function ExampleEmail({
  recipientName,
  verifyUrl,
  supportEmail,
}: ExampleEmailProps) {
  return (
    <Html>
      <Preview>Hello from CodeBloom!</Preview>
      <Section style={styles.body}>
        <Section style={styles.container}>
          <span style={styles.headerBadge}>CodeBloom</span>
          <Heading as="h1" style={styles.h1}>
            Hello,{" "}
            <span id="input-recipientName-innerText"> {recipientName}</span>!
          </Heading>
          <Text style={styles.paragraph}>
            Thank you for signing up for <span>CodeBloom</span>. Please verify
            your email address by clicking the button below.
          </Text>
          <Link
            href={verifyUrl}
            style={styles.button}
            id="input-verifyUrl-href"
          >
            Verify Email
          </Link>
          <div style={styles.urlBox} id="input-verifyUrl-innerText">
            {verifyUrl}
          </div>
          <Text style={styles.paragraph}>This link will expire in 1 hour.</Text>
          <Text style={styles.paragraph}>
            If you have any questions, please contact us at{" "}
            <Link id="input-supportEmail-href" href={supportEmail}>
              <span id="input-supportEmail-innerText">{supportEmail}</span>
            </Link>
            .
          </Text>
        </Section>
      </Section>
    </Html>
  );
}
