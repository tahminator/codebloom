import { Checkbox, Menu, MenuItemProps } from "@mantine/core";

interface FilterDropdownItemProps extends MenuItemProps {
  value: boolean;
  toggle: () => void;
  name: string;
}

/**
 * Custom component for each item in the filter dropdown menu.
 *
 * @param value - The current checked state of the checkbox.
 * @param toggle - Function to toggle the checkbox state.
 * @param name - The label for the item.
 *
 * @returns A Menu.Item component with a Checkbox and label.
 */
export default function FilterDropdownItem({
  value,
  toggle,
  name,
}: FilterDropdownItemProps) {
  return (
    <Menu.Item onClick={toggle} closeMenuOnClick={false}>
      <Checkbox checked={value} />
      {name}
    </Menu.Item>
  );
}
