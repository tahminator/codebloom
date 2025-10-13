import {
  Html,
  Head,
  Preview,
  Body,
  Container,
  Section,
  Img,
  Heading,
  Text,
  Button,
  Hr,
  Link,
  Tailwind,
  Row,
  Column,
} from "@react-email/components";

type SchoolEmailProps = {
  linkUrl: string;
};

export default function SchoolEmail({
  linkUrl = "https://codebloom.patinanetwork.org",
}: SchoolEmailProps) {
  return (
    <Html
      style={{
        fontFamily:
          "-apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif",
      }}
    >
      <Head />
      <Preview>Confirm your email to activate your CodeBloom account</Preview>
      <Tailwind>
        <Body className="m-0 p-0 bg-slate-50 text-slate-900">
          <Container className="mx-auto p-0 max-w-[560px]">
            <Section className="w-full h-[6px] bg-[#00b36a]" />
            <Section className="px-8 pt-7 pb-2" />
            <Row align="center">
              <Column align="left" className="w-[64px]">
                <Img
                  src="https://codebloom.patinanetwork.org/logo.png"
                  alt="CodeBloom"
                  width={64}
                  height={64}
                  style={{ display: "block" }}
                />
              </Column>
              <Column align="left">
                <Heading
                  as="h1"
                  className="m-0 text-[36px] font-bold leading-tight tracking-[-0.2px] text-[#00b36a]"
                >
                  CodeBloom
                </Heading>
              </Column>
            </Row>
            <Section className="px-4 pb-1">
              <Heading
                as="h2"
                className="mt-5 mb-1 text-[16px] font-semibold text-slate-900"
              >
                Welcome to CodeBloom!
              </Heading>
              <Text className="text-[13px] mt-1 leading-[1.6] text-slate-600">
                Thanks for signing up. To get started, please confirm your
                school email by clicking the button below.
              </Text>
            </Section>
            <Section className="text-center pb-2">
              <Button
                href={linkUrl}
                className="inline-block rounded-xl px-6 py-[12px] no-underline bg-[#00b36a] text-white shadow"
              >
                Verify Email
              </Button>
              <Text className="mt-4 text-xs italic leading-[1.6] text-slate-500">
                This link will only be valid for one hour.
              </Text>
            </Section>
            <Hr className="my-0 border-t border-[rgba(15,23,42,0,0,08)]" />
            <Section className="px-8 pt-5 pb-3 text-center">
              <Text className="mt-1 text-xs leading-[1.6] text-slate-600">
                If the button doesn’t work, you can log in directly here:
                <br />
                <Link
                  href={linkUrl}
                  className="underline text-[#00b36a]"
                  id="input-verifyUrl-href"
                >
                  <span id={"input-verifyUrl-innerText"}>{linkUrl}</span>
                </Link>
              </Text>
            </Section>
            <Hr className="my-0 border-t border-[rgba(15,23,42,0,0,08)]" />
            <Section className="px-8 py-6 text-center bg-[#18a869]">
              <Text className="m-0 text-xs text-white">
                Need help? Contact{" "}
                <Link
                  href="mailto:support@patinanetwork.org"
                  className="underline font-medium text-white"
                >
                  support@patinanetwork.org
                </Link>
              </Text>
            </Section>
            <Section className="px-8 py-4 text-center">
              <Text className="m-0 text-[12px] leading-[1.6] text-slate-500">
                © {new Date().getFullYear()} CodeBloom. All rights reserved.
              </Text>
            </Section>
          </Container>
        </Body>
      </Tailwind>
    </Html>
  );
}
