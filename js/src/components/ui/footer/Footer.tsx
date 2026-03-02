import { GotoAdminPageButton } from "@/components/ui/admin-button/AdminButton";
import styles from "@/components/ui/footer/Footer.module.css";
import { ActionIcon, Text, Anchor, Flex, Box } from "@mantine/core";
import { motion, useScroll, useTransform } from "motion/react";
import { ReactNode, useRef } from "react";
import { FaInstagram, FaDiscord, FaGithub } from "react-icons/fa";

import Logo from "/logo.png";

export function Footer() {
  const containerRef = useRef<HTMLDivElement>(null);
  const { scrollYProgress } = useScroll({
    target: containerRef,
    offset: ["start end", "end end"],
  });
  const missionText = "LeetCode motivation site for Patina Network";

  const y = useTransform(scrollYProgress, [0, 1], ["-100%", "0%"]);

  return (
    <div ref={containerRef} className={styles.footerWrapper}>
      <motion.div className={styles.footer} style={{ y }}>
        <div className={styles.footerContents}>
          <div
            data-testid={"footer-logo-mission-desktop"}
            className={`mantine-visible-from-sm ${styles.footerLeft}`}
          >
            <Flex>
              <img src={Logo} width={45} alt={"Logo"} />
              <Text pt={8} pl={6} fw={550} size={"md"}>
                Codebloom
              </Text>
            </Flex>
            <Flex>
              <Text pl={8} c={"dimmed"} size={"sm"}>
                {missionText}
              </Text>
            </Flex>
            <GotoAdminPageButton />
          </div>
          <div className={styles.footerRight}>
            <div
              data-testid={"footer-logo-mission-mobile"}
              className={`mantine-hidden-from-sm`}
            >
              <Flex>
                <img src={Logo} width={45} alt={"Logo"} />
                <Text pt={8} pl={6} fw={550} size={"md"}>
                  Codebloom
                </Text>
              </Flex>
              <Flex>
                <Text c={"dimmed"} size={"sm"}>
                  {missionText}
                </Text>
              </Flex>
              <GotoAdminPageButton />
            </div>
            <div data-testid={"footer-links-section"}>
              <Text fw={550} pb={4}>
                About
              </Text>
              <Box pt={1} pb={3}>
                <Anchor href="/privacy" c="dimmed" size="sm" variant="subtle">
                  Privacy Policy
                </Anchor>
              </Box>
              <Flex align="center">
                <Anchor
                  href={"https://github.com/tahminator/codebloom"}
                  c={"dimmed"}
                  size={"sm"}
                  variant={"subtle"}
                >
                  <Text>View GitHub</Text>
                </Anchor>
                <FooterIconLink
                  href={"https://github.com/tahminator/codebloom"}
                  ariaLabel={"CodeBloom GitHub"}
                >
                  <FaGithub size={16} />
                </FooterIconLink>
              </Flex>
            </div>
            <div>
              <Text fw={550} pb={4}>
                Community
              </Text>
              <Flex align={"center"}>
                <Anchor
                  href={"https://www.instagram.com/patinanetwork"}
                  c={"dimmed"}
                  size={"sm"}
                  variant={"subtle"}
                >
                  <Text>Follow on Instagram</Text>
                </Anchor>
                <FooterIconLink
                  href={"https://www.instagram.com/patinanetwork"}
                  ariaLabel={"Patina Network Instagram"}
                >
                  <FaInstagram size={16} />
                </FooterIconLink>
              </Flex>
              <Flex align={"center"}>
                <Anchor
                  href={"https://discord.com/invite/jKaPfHtcaD"}
                  c={"dimmed"}
                  size={"sm"}
                  variant={"subtle"}
                >
                  <Text>Join our Discord</Text>
                </Anchor>
                <FooterIconLink
                  href={"https://discord.com/invite/jKaPfHtcaD"}
                  ariaLabel={"Patina Network Discord"}
                >
                  <FaDiscord size={16} />
                </FooterIconLink>
              </Flex>
            </div>
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
      size="md"
      aria-label={ariaLabel}
      rel="noopener noreferrer"
    >
      {children}
    </ActionIcon>
  );
}
