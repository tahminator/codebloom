import HeaderContainer from "@/components/ui/header/container/HeaderContainer";
import { TestUtils, TestUtilTypes } from "@/lib/test";
import { cleanup, screen } from "@testing-library/react";

describe("HeaderContainer", () => {
  afterEach(() => {
    cleanup();
  });

  let renderProviderFn: TestUtilTypes.RenderWithAllProvidersFn | null = null;
  beforeEach(() => {
    renderProviderFn = TestUtils.getRenderWithAllProvidersFn();
  });

  it("should render header and children", () => {
    renderProviderFn?.(
      <HeaderContainer>
        {() => <div data-testid="header-child">Hello</div>}
      </HeaderContainer>,
    );

    const header = screen.getByRole("banner");
    expect(header).toBeInTheDocument();
    expect(header).toBeVisible();
    expect(screen.getByTestId("header-child")).toBeInTheDocument();
    screen.debug();
    expect(header.style.background.includes("rgb(48, 48, 48)")).toBe(true);
  });

  it("should pass animation motion values to children", () => {
    renderProviderFn?.(
      <HeaderContainer>
        {(animations) => (
          <div>
            <span data-testid="logo-size">
              {String(animations.logoSize.get())}
            </span>
            <span data-testid="text-opacity">
              {String(animations.textOpacity.get())}
            </span>
            <span data-testid="text-width">
              {String(animations.textWidth.get())}
            </span>
            <span data-testid="font-size">
              {String(animations.fontSize.get())}
            </span>
          </div>
        )}
      </HeaderContainer>,
    );

    expect(screen.getByTestId("logo-size").textContent).toBe("45");
    expect(screen.getByTestId("text-opacity").textContent).toBe("1");
    expect(screen.getByTestId("text-width").textContent).toBe("20rem");
    expect(screen.getByTestId("font-size").textContent).toBe("16px");
  });
});
