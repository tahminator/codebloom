import { GotoAdminPageButton } from "@/components/ui/admin-button/AdminButton";
import styles from "@/components/ui/footer/Footer.module.css";
import { Text, Anchor, Flex, Box, Stack } from "@mantine/core";
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
    <Box ref={containerRef} className={styles.footerWrapper}>
      <motion.div className={styles.footer} style={{ y }}>
        <Box className={styles.footerContents}>
          <Box
            visibleFrom={"sm"}
            data-testid={"footer-logo-mission-desktop"}
            className={styles.footerLeft}
          >
            <Flex align={"center"}>
              <img src={Logo} width={45} alt={"Logo"} />
              <Text fw={550} size={"md"}>
                Codebloom
              </Text>
            </Flex>
            <Flex>
              <Text pl={"xs"} c={"dimmed"} size={"sm"}>
                {missionText}
              </Text>
            </Flex>
            <GotoAdminPageButton />
          </Box>
          <Box className={styles.footerRight}>
            <Box hiddenFrom={"sm"} data-testid={"footer-logo-mission-mobile"}>
              <Flex align={"center"}>
                <img src={Logo} width={45} alt={"Logo"} />
                <Text fw={550} size={"md"}>
                  Codebloom
                </Text>
              </Flex>
              <Flex>
                <Text c={"dimmed"} size={"sm"}>
                  {missionText}
                </Text>
              </Flex>
              <GotoAdminPageButton />
            </Box>
            <Box data-testid={"footer-links-section"}>
              <Stack gap={4}>
                <Text fw={550}>About</Text>
                <AnchorLink href={"/privacy"} ariaLabel={"Privacy Policy"}>
                  Privacy Policy
                </AnchorLink>
                <AnchorLink
                  href={"https://github.com/tahminator/codebloom"}
                  ariaLabel={"CodeBloom GitHub"}
                >
                  <Flex align={"center"} gap={"xs"}>
                    <Text>View GitHub</Text>
                    <FaGithub size={16} color={"white"} />
                  </Flex>
                </AnchorLink>
              </Stack>
            </Box>
            <Box>
              <Stack gap={4}>
                <Text fw={550}>Community</Text>
                <AnchorLink
                  href={"https://www.instagram.com/patinanetwork"}
                  ariaLabel={"Patina Network Instagram"}
                >
                  <Flex align={"center"} gap={"xs"}>
                    <Text>Follow on Instagram</Text>
                    <FaInstagram size={16} color={"white"} />
                  </Flex>
                </AnchorLink>
                <AnchorLink
                  href={"https://discord.com/invite/jKaPfHtcaD"}
                  ariaLabel={"Patina Network Discord"}
                >
                  <Flex align={"center"} gap={"xs"}>
                    <Text>Join our Discord</Text>
                    <FaDiscord size={16} color={"white"} />
                  </Flex>
                </AnchorLink>
              </Stack>
            </Box>
          </Box>
        </Box>
      </motion.div>
    </Box>
  );
}

function AnchorLink({
  href,
  children,
  ariaLabel,
}: {
  href: string;
  children: ReactNode;
  ariaLabel: string;
}) {
  return (
    <Anchor
      href={href}
      c={"dimmed"}
      size={"sm"}
      variant={"subtle"}
      target={"_blank"}
      aria-label={ariaLabel}
      rel={"noopener noreferrer"}
    >
      {children}
    </Anchor>
  );
}
