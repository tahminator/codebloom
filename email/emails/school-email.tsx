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
 actionUrl: string;
 logo: string;
 name: string;
 buttonText: string;
 supportEmail: string;
 colors?: {
   primary: string;
   bg: string;
   text: string;
   muted: string;
 };
};


export default function SchoolEmail({
 actionUrl = "https://codebloom.patinanetwork.org",
 logo = "https://codebloom.patinanetwork.org/logo.png",
 name = "Code Bloom",
 buttonText = "Verify Email",
 supportEmail = "support@patinanetwork.org",
 colors,
}: SchoolEmailProps) {
 const palette = {
   primary: colors?.primary ?? "#00b36a",
   bg: colors?.bg ?? "#FFFFFF",
   text: colors?.text ?? "#0F172A",
   muted: colors?.muted ?? "#475569",
 };


 const dividerColor = "rgba(15,23,42,0.08)";
 const footerBg = "#18a869";


 return (
   <Html>
     <Head />
     <Preview>Confirm your email to activate your Code Bloom account</Preview>
     <Tailwind>
       <Body
         className="m-0 p-0"
         style={{ backgroundColor: "#F8FAFC", color: palette.text }}
       >
         <Container className="mx-auto p-0" style={{ maxWidth: 560 }}>
           <Section
             className="w-full"
             style={{ height: 6, backgroundColor: palette.primary }}
           />
           <Section className="px-8 pt-7 pb-2">
             <Row align="center">
               <Column align="left" className="w-[64px]">
                 <Img
                   src={logo}
                   alt={name}
                   width={64}
                   height={64}
                   style={{ display: "block" }}
                 />
               </Column>
               <Column align="left">
                 <Heading
                   as="h1"
                   className="m-0 text-[36px] font-bold"
                   style={{
                     color: palette.primary,
                     lineHeight: "1.1",
                     letterSpacing: "-0.2px",
                   }}
                 >
                   {name}
                 </Heading>
               </Column>
             </Row>
             <Heading
               as="h2"
               className="mt-5 mb-1 text-[16px] font-semibold"
               style={{ color: palette.text }}
             >
               Welcome to {name}!
             </Heading>
             <Text
               className="text-[13px] mt-1"
               style={{ color: palette.muted, lineHeight: "1.6" }}
             >
               Thanks for signing up. To get started, please confirm your school email by clicking the button below.
             </Text>
             <Section className="mt-4 text-center">
               <Button
                   href={actionUrl}
                   className="inline-block rounded-xl px-6 py-[12px] no-underline"
                   style={{
                     backgroundColor: palette.primary,
                     color: "#FFFFFF",
                     boxShadow: "0 2px 6px rgba(0,0,0,0.08)",
                   }}
               >
                 {buttonText}
               </Button>
               <Text
                 className="mt-4 text-xs italic"
                 style={{ color: "#64748B", lineHeight: "1.6" }}
               >
                 This link is valid for one hour.
               </Text>
             </Section>
           </Section>
           <Hr className="my-0" style={{ borderColor: dividerColor }} />
           <Section className="px-8 pt-5 pb-3 text-center">
             <Text
               className="mt-1 text-xs"
               style={{ color: palette.muted, lineHeight: "1.6" }}
             >
               If the button doesn’t work, you can log in directly here:
               <br />
               <Link
                 href={actionUrl}
                 className="underline"
                 style={{ color: palette.primary }}
               >
                 {actionUrl}
               </Link>
             </Text>
           </Section>
           <Hr className="my-0" style={{ borderColor: dividerColor }} />
           <Section
             className="px-8 py-6 text-center"
             style={{ backgroundColor: footerBg }}
           >
             <Text className="m-0 text-xs" style={{ color: "#FFFFFF" }}>
               Need help? Contact{" "}
               <Link
                 href={`mailto:${supportEmail}`}
                 className="underline font-semibold"
                 style={{ color: "#FFFFFF" }}
               >
                 {supportEmail}
               </Link>
             </Text>
           </Section>
           <Section className="px-8 py-4 text-center">
             <Text
               className="m-0 text-[12px]"
               style={{ color: "#64748B", lineHeight: "1.6" }}
             >
               © {new Date().getFullYear()} {name}. All rights reserved.
             </Text>
           </Section>
         </Container>
       </Body>
     </Tailwind>
   </Html>
 );
}