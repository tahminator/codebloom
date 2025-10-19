import SkeletonButton from "@/components/ui/auth/SkeletonButton";
import { useAuthQuery } from "@/lib/api/queries/auth";
import {
  Box,
  Burger,
  Button,
  Drawer,
  Flex,
  Group,
  Text,
  Title,
} from "@mantine/core";
import { useDisclosure } from "@mantine/hooks";
import { Link } from "react-router-dom";

import AvatarButton from "../auth/AvatarButton";
import classes from "./Header.module.css";

export default function Header() {
  const { data, status } = useAuthQuery();
  const [drawerOpened, { toggle: toggleDrawer, close: closeDrawer }] =
    useDisclosure(false);

  const renderButton = (className?: string) => {
    if (status === "pending") {
      return <SkeletonButton />;
    }

    if (status === "error") {
      return <Text c="red">Sorry, something went wrong.</Text>;
    }

    if (data?.user && data?.session) {
      const profileUrl = data.user.profileUrl;
      const initial =
        data.user.nickname ? data.user.nickname.charAt(0).toUpperCase() : "?";
      return (
        <AvatarButton
          src={profileUrl ?? ""}
          initial={initial}
          userId={data.user.id}
        />
      );
    }

    return (
      <Link to="/login" className={className}>
        <Button className={className}>Login</Button>
      </Link>
    );
  };

  return (
    <Box
      style={{
        background: "#303030",
      }}
    >
      <header className={classes.header}>
        <Link to="/">
          <Group>
            <img src={"/logo.png"} width={45} height={45} alt="Logo" />
            <Title>
              <Text
                gradient={{ from: "rgb(75,233,167)", to: "white" }}
                variant="gradient"
                size="lg"
              >
                CodeBloom
              </Text>
            </Title>
          </Group>
        </Link>
        <Group visibleFrom="sm">
          <Link to="/">
            <Button variant="transparent">Home</Button>
          </Link>
          <Link to="/dashboard">
            <Button variant="transparent">Dashboard</Button>
          </Link>
          <Link to="/leaderboard">
            <Button variant="transparent">Leaderboard</Button>
          </Link>
        </Group>
        <Group visibleFrom="sm">{renderButton()}</Group>
        <Burger
          opened={drawerOpened}
          onClick={toggleDrawer}
          hiddenFrom="sm"
          aria-label={"Menu button"}
        />
      </header>
      <Drawer opened={drawerOpened} onClose={closeDrawer} title="Navigation">
        <Flex direction="column" align="center" gap="md" mt="sm">
          <Link to="/" className="w-full">
            Home
          </Link>
          <Link to="/dashboard" className="w-full">
            Dashboard
          </Link>
          <Link to="/leaderboard" className="w-full">
            Leaderboard
          </Link>
          {renderButton("w-full")}
        </Flex>
      </Drawer>
    </Box>
  );
}
