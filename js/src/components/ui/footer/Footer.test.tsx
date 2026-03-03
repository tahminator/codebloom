import { Footer } from "@/components/ui/footer/Footer";
import { TestUtils, TestUtilTypes } from "@/lib/test";
import { screen } from "@testing-library/react";

describe("Footer rendered successfully", () => {
  let renderProviderFn: TestUtilTypes.RenderWithAllProvidersFn | null = null;
  beforeEach(() => {
    renderProviderFn = TestUtils.getRenderWithAllProvidersFn();
  });

  it("should render mission text", () => {
    renderProviderFn?.(<Footer />);
    const elements = screen.getAllByText(
      "LeetCode motivation site for Patina Network",
    );
    expect(elements).toHaveLength(2);
    for (const element of elements) {
      expect(element).toBeInTheDocument();
      expect(element).toBeVisible();
    }
  });

  it("should render links section", () => {
    renderProviderFn?.(<Footer />);
    const element = screen.getByTestId("footer-links-section");
    expect(element).toBeInTheDocument();
    expect(element).toBeVisible();
  });

  it("should have correct github link", () => {
    renderProviderFn?.(<Footer />);
    const element = screen.getByLabelText("CodeBloom GitHub");
    expect(element).toBeInTheDocument();
    expect(element).toBeVisible();
    expect(element).toHaveAttribute(
      "href",
      "https://github.com/tahminator/codebloom",
    );
  });
});
