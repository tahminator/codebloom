import { Checkbox, Menu } from "@mantine/core";

interface FilterDropdownItemProps {
  value: boolean;
  toggle: () => void;
  name: string;
}

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
