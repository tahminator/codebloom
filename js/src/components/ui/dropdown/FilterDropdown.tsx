import { Button, ButtonProps, Menu } from "@mantine/core";
import React, { useState } from "react";

interface FilterDropdownProps extends ButtonProps {
  buttonName: string;
  children: React.ReactNode;
}

/**
 * FilterDropdown component for toggling a menu with filter options.
 *
 * @param buttonName - The label for the dropdown button.
 * @returns A component that toggles a menu with filter options.
 */
export default function FilterDropdown({
  buttonName,
  children,
  ...props
}: FilterDropdownProps) {
  const [opened, setOpened] = useState(false);

  return (
    <Menu
      opened={opened}
      onChange={setOpened}
      position="left-end"
      withArrow
      zIndex={1001}
    >
      <Menu.Target>
        <Button onClick={() => setOpened((prev) => !prev)} {...props}>
          {buttonName}
        </Button>
      </Menu.Target>
      <Menu.Dropdown>{children}</Menu.Dropdown>
    </Menu>
  );
}
