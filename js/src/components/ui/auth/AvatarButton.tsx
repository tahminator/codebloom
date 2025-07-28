import { Menu, Avatar, Divider } from "@mantine/core";

import LogoutButton from "./LogoutButton";
import SettingsButton from "./SettingsButton";

export default function AvatarDropdown({
  src,
  initial,
}: {
  src: string;
  initial?: string;
}) {
  // if you don't set an avatar, this is your avatar link
  const showInitial =
    !src || src === "https://assets.leetcode.com/users/default_avatar.jpg";
  const isProduction = import.meta.env.PROD;
  return (
    <Menu shadow="md" width={"xl"}>
      <Menu.Target>
        {showInitial ?
          <Avatar component="button" color="green">
            {initial}
          </Avatar>
        : <Avatar component="button" src={src} />}
      </Menu.Target>

      <Menu.Dropdown>
        {!isProduction && (
          <>
            <Menu.Item>
              <SettingsButton />
            </Menu.Item>
            <Divider />
          </>
        )}
        <Menu.Item>
          <LogoutButton />
        </Menu.Item>
      </Menu.Dropdown>
    </Menu>
  );
}
