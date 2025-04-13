import { useAuthQuery } from "@/lib/api/queries/auth";
import { ActionIcon, Text, Button } from "@mantine/core";
import { ReactNode } from "react";
import { FaInstagram, FaLinkedin, FaGithub } from "react-icons/fa";

import styles from "./Footer.module.css";

import Logo from "/logo.png";

export function Footer() {
  const { data } = useAuthQuery();
  if (!data) return null;
  return (
    <div className={styles.footer}>
      <div className={styles.footerContents}>
        <div>
          <img src={Logo} width={45} alt="Logo" />
        </div>
        {data.isAdmin && (
          <Button
            component="a"
            href="/admin"
            mt={0}
            size="xs"
            variant="outline"
            style={{}}
          >
            Admin
          </Button>
        )}
        <Text px={30} fs="italic" visibleFrom={"sm"}>
          {
            "CodeBloom is a LeetCode motivation site for Patina Network members."
          }
        </Text>
        <div className={styles.footerLinks}>
          <FooterIconLink
            href={"https://www.linkedin.com/company/patinanetwork"}
            ariaLabel={"Patina Network LinkedIn"}
          >
            <FaLinkedin size={24} />
          </FooterIconLink>
          <FooterIconLink
            href={"https://www.instagram.com/patinanetwork"}
            ariaLabel={"Patina Network Instagram"}
          >
            <FaInstagram size={24} />
          </FooterIconLink>
          <FooterIconLink
            href={"https://github.com/tahminator/codebloom"}
            ariaLabel={"CodeBloom GitHub"}
          >
            <FaGithub size={24} />
          </FooterIconLink>
        </div>
      </div>
    </div>
  );
}

function FooterIconLink({
  href,
  children,
  ariaLabel,
}: {
  href: string;
  children: ReactNode;
  ariaLabel: string;
}) {
  return (
    <ActionIcon
      component="a"
      href={href}
      target="_blank"
      color={"dark.0"}
      variant="transparent"
      size="lg"
      aria-label={ariaLabel}
    >
      {children}
    </ActionIcon>
  );
}
