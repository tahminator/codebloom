import { Button, ButtonProps, Menu } from "@mantine/core";
import React, { useState } from "react";

interface FilterDropdownProps extends ButtonProps {
  buttonName: string;
  children: React.ReactNode;
}

export default function FilterDropdown({
  buttonName,
  children,
  ...props
}: FilterDropdownProps) {
  const [opened, setOpened] = useState(false);

  return (
    <Menu opened={opened} onChange={setOpened}>
      <Menu.Target>
        <Button onClick={() => setOpened((prev) => !prev)} {...props}>
          {buttonName}
        </Button>
      </Menu.Target>
      <Menu.Dropdown>{children}</Menu.Dropdown>
    </Menu>
  );
}
