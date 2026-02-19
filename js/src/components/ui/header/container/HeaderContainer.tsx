import classes from "@/components/ui/header/Header.module.css";
import { theme } from "@/lib/theme";
import { Box } from "@mantine/core";
import { motion, MotionValue, useScroll, useTransform } from "motion/react";
import { ReactNode } from "react";

const SCROLL_RANGE: [number, number] = [0, 150];

interface HeaderContainerProps {
  children: (animations: {
    logoSize: MotionValue<number>;
    textOpacity: MotionValue<number>;
    textWidth: MotionValue<string>;
    fontSize: MotionValue<string>;
  }) => ReactNode;
}

export default function HeaderContainer({ children }: HeaderContainerProps) {
  const { scrollY } = useScroll();

  const headerPadding = useTransform(scrollY, SCROLL_RANGE, ["1rem", "1rem"]);
  const headerMarginHorizontal = useTransform(scrollY, SCROLL_RANGE, [
    "2%",
    "15%",
  ]);
  const logoSize = useTransform(scrollY, SCROLL_RANGE, [45, 35]);
  const textOpacity = useTransform(scrollY, SCROLL_RANGE, [1, 0]);
  const textWidth = useTransform(scrollY, SCROLL_RANGE, ["20rem", "0rem"]);
  const fontSize = useTransform(scrollY, SCROLL_RANGE, ["16px", "0px"]);

  return (
    <Box
      pos={"sticky"}
      top={0}
      style={{
        zIndex: 100,
      }}
    >
      <motion.header
        className={classes.header}
        style={{
          background: theme.other.codebloomGray,
          paddingTop: headerPadding,
          paddingBottom: headerPadding,
          paddingLeft: "2rem",
          paddingRight: "2rem",
          marginTop: "0.5rem",
          marginBottom: "0.5rem",
          marginLeft: headerMarginHorizontal,
          marginRight: headerMarginHorizontal,
        }}
      >
        {children({ logoSize, textOpacity, textWidth, fontSize })}
      </motion.header>
    </Box>
  );
}
