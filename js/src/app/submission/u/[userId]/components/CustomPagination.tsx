import { Button, TextInput } from "@mantine/core";
import { useState } from "react";

const MAX_VISIBLE_PAGES = 5;

const getPageNumbers = (
  currentPage: number,
  totalPages: number
): (number | "...")[] => {
  // Show regular page buttons if pages hasn't surprassed max visible pages yet.
  if (totalPages <= MAX_VISIBLE_PAGES) {
    return Array.from({ length: totalPages }, (_, i) => i + 1);
  }

  // If the current page is greater than 3, start showing a type to jump on just the right side.
  if (currentPage < 3) {
    return [1, 2, 3, "...", totalPages];
  }

  /// If we are currently near the end of the pages, show the type to jump on just the left side.
  if (currentPage > totalPages - 2) {
    return [1, "...", totalPages - 2, totalPages - 1, totalPages];
  }

  // If we are somewhere in the middle of the above 2 situations, show type to jump on both left and right side.
  return [
    1,
    "...",
    currentPage - 1,
    currentPage,
    currentPage + 1,
    "...",
    totalPages,
  ];
};

export default function CustomPagination({
  goTo,
  pages,
  currentPage,
}: {
  goTo: (pageNumber: number) => void;
  pages: number;
  currentPage: number;
}) {
  const pageNumbers = getPageNumbers(currentPage, pages);
  const [customPage, setCustomPage] = useState("");

  const handleCustomPageSubmit = () => {
    const pageNum = Math.max(1, Math.min(pages, Number(customPage))); // Clamp value
    if (!isNaN(pageNum)) {
      goTo(pageNum);
      setCustomPage(""); // Reset input after navigation
    }
  };

  return (
    <div style={{ display: "flex", alignItems: "center", gap: "5px" }}>
      {pageNumbers.map((page, index) =>
        page === "..." ? (
          <TextInput
            key={index}
            value={customPage}
            onChange={(e) => setCustomPage(e.target.value)}
            onKeyDown={(e) => e.key === "Enter" && handleCustomPageSubmit()}
            placeholder="..."
            style={{ width: 50, textAlign: "center" }}
          />
        ) : (
          <Button
            key={index}
            onClick={() => goTo(page)}
            style={{
              fontWeight: currentPage === page ? "bold" : "normal",
              background: currentPage === page ? "gray" : "white",
            }}
          >
            {page}
          </Button>
        )
      )}
    </div>
  );
}
