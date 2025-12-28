import { Footer } from "@/components/ui/footer/Footer";
import Header from "@/components/ui/header/Header";
import { Flex, Box } from "@mantine/core";
import { ReactNode } from "react";

export default function PageShell({ children }: { children: ReactNode }) {
  return (
    <Flex direction={"column"} mih={"100vh"}>
      <Header />
      <Box p={"lg"} flex={1}>
        {children}
      </Box>
      <Footer />
    </Flex>
  );
}
