import { useAuthQuery } from "@/app/login/hooks";
import LogoutButton from "@/components/ui/auth/LogoutButton";
import Toast from "@/components/ui/toast/Toast";
import ToastWithRedirect from "@/components/ui/toast/ToastWithRedirect";
import {
  Box,
  Burger,
  Button,
  Group,
  Loader,
  Title,
  Text,
  Divider,
  Drawer,
} from "@mantine/core";
import { useDisclosure } from "@mantine/hooks";
import { Link } from "react-router-dom";
import classes from "./Dashboard.page.module.css";
import Logo from "@/logo.png";

export default function DashboardPage() {
  const { data, status } = useAuthQuery();

  const [drawerOpened, { toggle: toggleDrawer, close: closeDrawer }] =
    useDisclosure(false);
  if (status === "pending") {
    return (
      <div className="flex flex-col items-center justify-center w-screen h-screen">
        <Loader />
      </div>
    );
  }

  if (status === "error") {
    return <Toast message="Sorry, something went wrong." />;
  }

  const authenticated = !!data.user && !!data.session;

  if (!authenticated) {
    return (
      <ToastWithRedirect to="/login" message="You are not authenticated!" />
    );
  }

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
            <Text>CodeBloom</Text>
          </Title>
        </Group>
        <Group>
          <Link to="/" reloadDocument>
            <Button>Home</Button>
          </Link>
          <Link to="/dashboard" reloadDocument>
            <Button>Dashboard</Button>
          </Link>
        </Group>
        <Group>
          <LogoutButton />
        </Group>
        <Burger opened={drawerOpened} onClick={toggleDrawer} hiddenFrom="sm" />{" "}
      </header>

      <Drawer
        opened={drawerOpened}
        onClose={closeDrawer}
        size="100%"
        padding="md"
        title="CodeBloom"
      >
        <Divider my="sm" />
        <Link to="/" reloadDocument>
          <Button>Home</Button>
        </Link>
        <Divider my="sm" />
        <Link to="/dashboard" reloadDocument>
          <Button>Dashboard</Button>
        </Link>
        <Divider my="sm" />
        <LogoutButton />
      </Drawer>
    </Box>
  );
}
