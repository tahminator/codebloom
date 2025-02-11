import { UnstyledButton, UnstyledButtonProps } from "@mantine/core";
import { forwardRef } from "react";

import classes from "./PatinaButton.module.css";

export const PatinaButton = forwardRef<HTMLButtonElement, UnstyledButtonProps>(
  (props, ref) => (
    <UnstyledButton {...props} ref={ref} className={classes.link} />
  ),
);
