import { Footer } from "@/components/ui/footer/Footer";
import Header from "@/components/ui/header/Header";
import DocumentDescription from "@/components/ui/title/DocumentDescription";
import DocumentTitle from "@/components/ui/title/DocumentTitle";
import { Text, Title, Container, Stack, Anchor } from "@mantine/core";

const lastUpdated = "September 1, 2025";

export default function PolicyPage() {
  return (
    <>
      <>
        <DocumentTitle title={`CodeBloom - Privacy Policy`} />
        <DocumentDescription
          description={`CodeBloom - View our Privacy Policy`}
        />
      </>
      <Header />
      <Container
        size="sm"
        mt="xl"
        pb="xl"
        component="article"
        aria-labelledby="privacy-heading"
      >
        <Stack gap="sm">
          <header>
            <Title id="privacy-heading" order={2} mb="xs">
              Privacy Policy
            </Title>
          </header>
          <Text size="sm" c="dimmed" mb="sm">
            Last updated: {lastUpdated}
          </Text>
          <Text mb="sm">
            This website is non-commercial. We do not show advertisements.
          </Text>
          <section>
            <Title order={3} mt="md" mb="xs">
              The Data That We Collect
            </Title>
            <Text mb="sm">We collect some personal data, including:</Text>
            <ul style={{ marginTop: 0, marginBottom: "0.5em" }}>
              <li>Discord Username</li>
              <li>LeetCode Username</li>
              <li>School Email</li>
            </ul>
            <Text>
              This data is required to provide our service. Your data is stored
              securely and never shared with any third-party sources or
              advertisers, unless required by law. Our code is 100% open-source,
              so anyone can review how the data is being handled.
            </Text>
          </section>
          <section>
            <Title order={3} mt="md" mb="xs">
              Cookies
            </Title>
            <Text>
              We only use cookies for authentication purposes; they do not get
              used for any tracking or advertisements.
            </Text>
          </section>
          <section>
            <Title order={3} mt="md" mb="xs">
              Your Rights
            </Title>
            <Text>
              You may request deletion of your data at any time by contacting us
              at{" "}
              <Anchor
                href="mailto:codebloom@patinanetwork.org"
                underline="always"
              >
                codebloom@patinanetwork.org
              </Anchor>
            </Text>
          </section>
          <section>
            <Title order={3} mt="md" mb="xs">
              Policy Changes
            </Title>
            <Text>
              This policy may change over time, in which case we will update
              this page along with the updated date above.
            </Text>
          </section>
          <section>
            <Title order={3} mt="md" mb="xs">
              Contact Us
            </Title>
            <Text>
              If you have any questions about this policy, please contact us at{" "}
              <Anchor
                href="mailto:codebloom@patinanetwork.org"
                underline="always"
              >
                codebloom@patinanetwork.org
              </Anchor>
            </Text>
          </section>
        </Stack>
      </Container>
      <Footer />
    </>
  );
}
