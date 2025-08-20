import {
  Body,
  Button,
  Container,
  Head,
  Heading,
  Hr,
  Html,
  Link,
  Preview,
  Section,
  Text,
} from '@react-email/components';
import * as React from 'react';

interface SchoolVerificationEmailProps {
  verificationLink: string;
  recipientEmail: string;
}

export const SchoolVerificationEmail = ({
  verificationLink,
  recipientEmail,
}: SchoolVerificationEmailProps) => (
  <Html>
    <Head />
    <Preview>Verify your school email with Codebloom</Preview>
    <Body style={main}>
      <Container style={container}>
        <Heading style={h1}>Welcome to Codebloom! 🌟</Heading>
        
        <Text style={text}>
          Hi there! We're excited to help you connect with your school community on Codebloom.
        </Text>
        
        <Text style={text}>
          To verify your school email address <strong>{recipientEmail}</strong>, please click the button below:
        </Text>

        <Section style={buttonContainer}>
          <Button style={button} href={verificationLink}>
            Verify School Email
          </Button>
        </Section>

        <Text style={text}>
          Or copy and paste this URL into your browser:
        </Text>
        <Link href={verificationLink} style={link}>
          {verificationLink}
        </Link>

        <Hr style={hr} />

        <Text style={footer}>
          <strong>Important:</strong> This verification link will expire in 1 hour. If it expires, 
          you'll need to request a new one from your account settings.
        </Text>

        <Text style={footer}>
          If you didn't request this verification, you can safely ignore this email.
        </Text>

        <Text style={footer}>
          Happy coding! 💻<br />
          The Codebloom Team
        </Text>
      </Container>
    </Body>
  </Html>
);

export default SchoolVerificationEmail;

const main = {
  backgroundColor: '#f6f9fc',
  fontFamily:
    '-apple-system,BlinkMacSystemFont,"Segoe UI",Roboto,Oxygen-Sans,Ubuntu,Cantarell,"Helvetica Neue",sans-serif',
};

const container = {
  margin: '0 auto',
  padding: '20px 0 48px',
  width: '580px',
  maxWidth: '100%',
};

const h1 = {
  color: '#1a1a1a',
  fontSize: '24px',
  fontWeight: '600',
  lineHeight: '1.25',
  margin: '16px 0',
};

const text = {
  color: '#444',
  fontSize: '16px',
  lineHeight: '1.5',
  margin: '16px 0',
};

const buttonContainer = {
  textAlign: 'center' as const,
  margin: '32px 0',
};

const button = {
  backgroundColor: '#5469d4',
  borderRadius: '8px',
  color: '#fff',
  display: 'inline-block',
  fontSize: '16px',
  fontWeight: '600',
  lineHeight: '1.5',
  padding: '12px 24px',
  textDecoration: 'none',
  textAlign: 'center' as const,
};

const link = {
  color: '#5469d4',
  fontSize: '14px',
  textDecoration: 'underline',
  wordBreak: 'break-all' as const,
};

const hr = {
  borderColor: '#ddd',
  margin: '32px 0',
};

const footer = {
  color: '#666',
  fontSize: '14px',
  lineHeight: '1.5',
  margin: '8px 0',
};