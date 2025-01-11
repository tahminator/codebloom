import LogoutButton from "@/components/ui/auth/LogoutButton";
import Logo from "@/logo.png";
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
import classes from "./Header.module.css";

export default function Header() {
  const [drawerOpened, { toggle: toggleDrawer, close: closeDrawer }] =
    useDisclosure(false);

  return (
    <Box
      style={{
        background: "#303030",
      }}
    >
      <header className={classes.header}>
        <Group>
          <img src={Logo} width={45} alt="Logo" />
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
        <Group visibleFrom="sm">
          <LogoutButton />
        </Group>
        <Burger opened={drawerOpened} onClick={toggleDrawer} hiddenFrom="sm" />
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
          <LogoutButton className="w-full" />
        </Flex>
      </Drawer>
    </Box>
  );
}
