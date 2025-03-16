import CustomPagination from "@/components/ui/table/CustomPagination";
import { Center, Flex, Button } from "@mantine/core";
import { FaArrowLeft, FaArrowRight } from "react-icons/fa";

/**
 * A pre-build standalone paginator that includes pagination buttons to jump to a specific file as well as
 * paginate left and right buttons.
 */
export default function Paginator({
  pages,
  currentPage,
  goTo,
  goBack,
  goForward,
  hasNextPage,
}: {
  pages: number;
  currentPage: number;
  goTo: (page: number) => void;
  goBack: () => void;
  goForward: () => void;
  hasNextPage: boolean;
}) {
  return (
    <Center my={"sm"}>
      <Flex direction={"row"} gap={"sm"}>
        <Button
          disabled={currentPage === 1}
          onClick={goBack}
          size={"compact-sm"}
        >
          <FaArrowLeft />
        </Button>
        <CustomPagination goTo={goTo} pages={pages} currentPage={currentPage} />
        <Button
          disabled={!hasNextPage || currentPage >= pages}
          onClick={() => {
            if (hasNextPage || currentPage >= pages) {
              goForward();
            }
          }}
          size={"compact-sm"}
        >
          <FaArrowRight />
        </Button>
      </Flex>
    </Center>
  );
}
