import { Menu, Avatar } from "@mantine/core";
import { Link } from "react-router-dom";

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
            <Menu.Item component={Link} to={"/settings"} w-full>
              Settings
            </Menu.Item>
            <Menu.Divider />
          </>
        )}
        <Menu.Item
          component={Link}
          to={"/api/auth/logout"}
          reloadDocument
          w-full
          color="red"
        >
          Logout
        </Menu.Item>
      </Menu.Dropdown>
    </Menu>
  );
}
