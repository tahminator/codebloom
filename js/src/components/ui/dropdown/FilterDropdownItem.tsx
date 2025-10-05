import { Checkbox, Menu, MenuItemProps, Switch } from "@mantine/core";
import { ReactNode } from "react";

interface FilterDropdownItemProps extends MenuItemProps {
  value: boolean;
  toggle: () => void;
  name: ReactNode | (() => ReactNode);
  switchMode?: boolean;
}

/**
 * Custom component for each item in the filter dropdown menu.
 *
 * @param value - The current checked state of the checkbox.
 * @param toggle - Function to toggle the checkbox state.
 * @param name - The label for the item. This can either be a ReactNode or a function that returns ReactNode.
 *
 * @returns A Menu.Item component with a Checkbox and label.
 */
export default function FilterDropdownItem({
  value,
  toggle,
  name,
  disabled,
  switchMode = false,
  ...props
}: FilterDropdownItemProps) {
  return (
    <Menu.Item
      onClick={toggle}
      closeMenuOnClick={false}
      disabled={disabled}
      {...props}
      leftSection={
        switchMode ?
          <Switch
            // Switch requires onClick to be passed in, while Checkbox doesn't
            onClick={toggle}
            checked={value}
            disabled={disabled}
            withThumbIndicator={false}
          />
        : <Checkbox checked={value} disabled={disabled} />
      }
    >
      <div
        style={{
          display: "flex",
          alignItems: "center",
          gap: "8px",
          width: "100%",
        }}
      >
        {typeof name === "function" ? name() : name}
      </div>
    </Menu.Item>
  );
}
