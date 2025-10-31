import { Button, ButtonProps, Menu } from "@mantine/core";
import React, { useState } from "react";

// Default z-index for dropdown to appear above loading overlays (which use z-index 1000)
const DEFAULT_DROPDOWN_Z_INDEX = 1001;

interface FilterDropdownProps extends ButtonProps {
  buttonName: string;
  children: React.ReactNode;
  zIndex?: number;
}

/**
 * FilterDropdown component for toggling a menu with filter options.
 *
 * @param buttonName - The label for the dropdown button.
 * @param children - The content to display inside the dropdown menu.
 * @param zIndex - The z-index for the dropdown menu (default: 1001 to appear above loading overlays).
 * @returns A component that toggles a menu with filter options.
 */
export default function FilterDropdown({
  buttonName,
  children,
  zIndex = DEFAULT_DROPDOWN_Z_INDEX,
  ...props
}: FilterDropdownProps) {
  const [opened, setOpened] = useState(false);

  return (
    <Menu
      opened={opened}
      onChange={setOpened}
      position="left-end"
      withArrow
      zIndex={zIndex}
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
