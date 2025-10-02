import { Box, Center, Skeleton, Table } from "@mantine/core";

// TODO - Make this nicer
export default function UserSubmissionsSkeleton() {
  // <Box style={{ overflowX: "auto" }} maw={"100%"} miw={"66%"}>
  return (
    <>
      <Box style={{ overflowX: "auto" }} maw={"100%"} miw={"66%"} p="xs">
        <Skeleton
          visible
          width="80px"
          height="36px"
          ml="auto"
          display="block"
        ></Skeleton>
        <Center mb="md">
          <Skeleton
            visible
            width="100%"
            height="38px"
            style={{ marginTop: "0.5rem" }}
          >
            <div style={{ width: "100%", height: "36px" }} />
          </Skeleton>
        </Center>
        <Table
          verticalSpacing={"lg"}
          horizontalSpacing={"xs"}
          withRowBorders={false}
          striped
          my={"sm"}
          pos={"relative"}
        >
          <Table.Thead>
            <Table.Tr>
              <Table.Th>
                <Skeleton visible width={"1.75rem"} height={"0.75rem"} />
              </Table.Th>
              <Table.Th>
                <Skeleton visible width={"1.75rem"} height={"0.75rem"} />
              </Table.Th>
              <Table.Th>
                <Skeleton visible width={"1.75rem"} height={"0.75rem"} />
              </Table.Th>
              <Table.Th>
                <Skeleton visible width={"1.75rem"} height={"0.75rem"} />
              </Table.Th>
              <Table.Th>
                <Skeleton visible width={"1.75rem"} height={"0.75rem"} />
              </Table.Th>
            </Table.Tr>
          </Table.Thead>
          <Table.Tbody>
            {Array(20)
              .fill(0)
              .map((_, index) => (
                <Table.Tr key={index}>
                  <Table.Td>
                    <Skeleton visible width={"1rem"} height={"1rem"} />
                  </Table.Td>
                  <Table.Td>
                    <Skeleton visible width={"8rem"} height={"1.75rem"} />
                  </Table.Td>
                  <Table.Td>
                    <Skeleton visible width={"2rem"} height={"1rem"} />
                  </Table.Td>
                  <Table.Td>
                    <Skeleton visible width={"1.75rem"} height={"0.75rem"} />
                  </Table.Td>
                  <Table.Td>
                    <Skeleton visible width={"1.75rem"} height={"0.75rem"} />
                  </Table.Td>
                </Table.Tr>
              ))}
          </Table.Tbody>
        </Table>
      </Box>
    </>
  );
}
