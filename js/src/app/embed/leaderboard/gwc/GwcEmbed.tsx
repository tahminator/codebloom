import { Box } from "@mantine/core";

import GwcEmbedView from "./_components/GwcEmbedView";

export default function GwcEmbedContainer() {
  return (
    <>
      <div className="flex-grow">
        <Box p={"lg"}>
          <GwcEmbedView />
        </Box>
      </div>
    </>
  );
}
