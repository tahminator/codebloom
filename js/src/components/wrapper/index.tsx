import { BoxRef } from "@/components/wrapper/box";
import { TextRef } from "@/components/wrapper/text";
import { motion } from "motion/react";

export const Text = motion(TextRef);
export const Box = motion(BoxRef);

// eslint-disable-next-line react-refresh/only-export-components
export const MM = {
  Text,
  Box,
};
