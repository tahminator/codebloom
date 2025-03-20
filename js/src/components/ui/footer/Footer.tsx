import { ActionIcon, Text } from "@mantine/core";
import { ReactNode } from "react";
import { FaInstagram, FaLinkedin, FaGithub } from "react-icons/fa";

import styles from "./Footer.module.css";

import Logo from "/logo.png";

export function Footer() {
  return (
    <div className={styles.footer}>
      <div className={styles.footerContents}>
        <div>
          <img src={Logo} width={45} alt="Logo" />
        </div>
        <Text px={30} fs="italic">
          {
            "CodeBloom is a LeetCode motivation site for Patina Network members."
          }
        </Text>
        <div className={styles.footerLinks}>
          <FooterIconLink
            href={"https://www.linkedin.com/company/patinanetwork"}
          >
            <FaLinkedin size={24} />
          </FooterIconLink>
          <FooterIconLink href={"https://www.instagram.com/patinanetwork"}>
            <FaInstagram size={24} />
          </FooterIconLink>
          <FooterIconLink href={"https://github.com/tahminator/codebloom"}>
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
}: {
  href: string;
  children: ReactNode;
}) {
  return (
    <ActionIcon
      component="a"
      href={href}
      target="_blank"
      color={"dark.0"}
      variant="transparent"
      size="lg"
    >
      {children}
    </ActionIcon>
  );
}
