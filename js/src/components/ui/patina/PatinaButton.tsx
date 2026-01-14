import classes from "@/components/ui/patina/PatinaButton.module.css";
import { UnstyledButton, UnstyledButtonProps } from "@mantine/core";
import { forwardRef } from "react";

export const PatinaButton = forwardRef<HTMLButtonElement, UnstyledButtonProps>(
  (props, ref) => (
    <UnstyledButton {...props} ref={ref} className={classes.link} />
  ),
);
