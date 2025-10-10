import { Box } from "@mantine/core";

import GwcEmbedView from "./_components/GwcEmbedView";

export default function GwcEmbedContainer() {
  return (
    <>
      <Box className="grow">
        <Box p={"lg"}>
          <GwcEmbedView />
        </Box>
      </Box>
    </>
  );
}
