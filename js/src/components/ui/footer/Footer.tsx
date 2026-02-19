import { GotoAdminPageButton } from "@/components/ui/admin-button/AdminButton";
import styles from "@/components/ui/footer/Footer.module.css";
import { ActionIcon, Text, Anchor } from "@mantine/core";
import { motion, useScroll, useTransform } from "motion/react";
import { ReactNode, useRef } from "react";
import { FaInstagram, FaLinkedin, FaGithub } from "react-icons/fa";

import Logo from "/logo.png";

export function Footer() {
  const containerRef = useRef<HTMLDivElement>(null);
  const { scrollYProgress } = useScroll({
    target: containerRef,
    offset: ["start end", "end end"],
  });

  const y = useTransform(scrollYProgress, [0, 1], ["-100%", "0%"]);

  return (
    <div ref={containerRef} className={styles.footerWrapper}>
      <motion.div className={styles.footer} style={{ y }}>
        <div className={styles.footerContents}>
          <div className={styles.footerLeft}>
            <img src={Logo} width={45} alt="Logo" />
            <GotoAdminPageButton />
          </div>
          <Text px={30} fs="italic" visibleFrom={"sm"}>
            CodeBloom is a LeetCode motivation site for Patina Network members.
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
            <Anchor
              href="/privacy"
              c="dimmed"
              size="sm"
              variant="subtle"
              underline="always"
              pb={"sm"}
            >
              Privacy Policy
            </Anchor>
          </div>
        </div>
      </motion.div>
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
      mt={-7}
    >
      {children}
    </ActionIcon>
  );
}
