import TransitionalButtons from "@/components/ui/button/transitonal/TransitionalButtons";
import { TestUtils, TestUtilTypes } from "@/lib/test";
import { theme } from "@/lib/theme";
import { cleanup, fireEvent, screen, waitFor } from "@testing-library/react";

describe("TransitionalButtons", () => {
  afterEach(() => {
    cleanup();
  });

  let renderProviderFn: TestUtilTypes.RenderWithAllProvidersFn | null = null;
  beforeEach(() => {
    renderProviderFn = TestUtils.getRenderWithAllProvidersFn();
  });

  it("should render both buttons with links and hrefs", () => {
    const buttons = [
      {
        to: "/",
        label: "Home",
      },
      {
        to: "/leaderboard",
        label: "Leaderboard",
      },
    ];

    renderProviderFn?.(<TransitionalButtons buttons={buttons} />);
    const element = screen.getByTestId(
      `transitional-button-${buttons[0]?.label}`,
    );
    expect(element).toBeInTheDocument();
    expect(element).toBeVisible();
    expect(element.getAttribute("href")?.includes(buttons[0]?.to)).toBe(true);
    expect(element.innerHTML.includes(buttons[0]?.label)).toBe(true);

    const element2 = screen.getByTestId(
      `transitional-button-${buttons[1]?.label}`,
    );

    expect(element2).toBeInTheDocument();
    expect(element2).toBeVisible();
    expect(element2.getAttribute("href")?.includes(buttons[1]?.to)).toBe(true);
    expect(element2.innerHTML.includes(buttons[1]?.label)).toBe(true);
  });

  it("hovering on first button should make the hover text black", () => {
    const buttons = [
      {
        to: "/",
        label: "Home",
      },
      {
        to: "/leaderboard",
        label: "Leaderboard",
      },
    ];

    renderProviderFn?.(<TransitionalButtons buttons={buttons} />);
    const element = screen.getByTestId(
      `transitional-button-${buttons[0]?.label}`,
    );

    fireEvent.mouseEnter(element);

    expect(element).toBeInTheDocument();
    expect(element).toBeVisible();

    expect(element.style.color.includes("rgb(16, 17, 19)")).toBe(true);

    const element2 = screen.getByTestId(
      `transitional-button-${buttons[1]?.label}`,
    );

    expect(element2).toBeInTheDocument();
    expect(element2).toBeVisible();
    expect(element2.style.color.includes("rgb(16, 17, 19)")).toBe(false);
  });

  it("hovering on second button should make hover text black", async () => {
    const buttons = [
      {
        to: "/home",
        label: "Home",
      },
      {
        to: "/leaderboard",
        label: "Leaderboard",
      },
    ];

    renderProviderFn?.(<TransitionalButtons buttons={buttons} />);
    const element = screen.getByTestId(
      `transitional-button-${buttons[0]?.label}`,
    );
    const element2 = screen.getByTestId(
      `transitional-button-${buttons[1]?.label}`,
    );
    await waitFor(() => {
      fireEvent.mouseEnter(element2);
    });

    expect(element).toBeInTheDocument();
    expect(element).toBeVisible();
    expect(element.style.color.includes("rgb(16, 17, 19)")).toBe(false);

    expect(element2).toBeInTheDocument();
    expect(element2).toBeVisible();
    expect(element2.style.color.includes("rgb(16, 17, 19)")).toBe(true);
  });

  it("hovering and unhovering on first button", async () => {
    const buttons = [
      {
        to: "/home",
        label: "Home",
      },
      {
        to: "/leaderboard",
        label: "Leaderboard",
      },
    ];

    renderProviderFn?.(<TransitionalButtons buttons={buttons} />);
    const element = screen.getByTestId(
      `transitional-button-${buttons[0]?.label}`,
    );
    const element2 = screen.getByTestId(
      `transitional-button-${buttons[1]?.label}`,
    );
    await waitFor(() => {
      fireEvent.mouseEnter(element);
    });

    expect(element).toBeInTheDocument();
    expect(element).toBeVisible();
    expect(element.style.color.includes("rgb(16, 17, 19)")).toBe(true);

    expect(element2).toBeInTheDocument();
    expect(element2).toBeVisible();
    expect(element2.style.color.includes("rgb(16, 17, 19)")).toBe(false);

    await waitFor(() => {
      fireEvent.mouseLeave(element);
    });

    expect(element).toBeInTheDocument();
    expect(element).toBeVisible();
    await waitFor(() => {
      expect(element.style.color.includes("rgb(16, 17, 19)")).toBe(false);
    });

    expect(element2).toBeInTheDocument();
    expect(element2).toBeVisible();
    await waitFor(() => {
      expect(element2.style.color.includes("rgb(16, 17, 19)")).toBe(false);
    });
  });

  it("clicking on first button should make the select text black", () => {
    const buttons = [
      {
        to: "/",
        label: "Home",
      },
      {
        to: "/leaderboard",
        label: "Leaderboard",
      },
    ];

    renderProviderFn?.(<TransitionalButtons buttons={buttons} />);
    const element = screen.getByTestId(
      `transitional-button-${buttons[0]?.label}`,
    );

    fireEvent.mouseDown(element);

    expect(element).toBeInTheDocument();
    expect(element).toBeVisible();

    expect(element.style.color.includes("rgb(16, 17, 19)")).toBe(true);

    const element2 = screen.getByTestId(
      `transitional-button-${buttons[1]?.label}`,
    );

    expect(element2).toBeInTheDocument();
    expect(element2).toBeVisible();
    expect(element2.style.color.includes("rgb(16, 17, 19)")).toBe(false);
  });

  it("clicking on second button should make the select text black", () => {
    const buttons = [
      {
        to: "/",
        label: "Home",
      },
      {
        to: "/leaderboard",
        label: "Leaderboard",
      },
    ];

    renderProviderFn?.(<TransitionalButtons buttons={buttons} />);
    const element = screen.getByTestId(
      `transitional-button-${buttons[0]?.label}`,
    );
    const element2 = screen.getByTestId(
      `transitional-button-${buttons[1]?.label}`,
    );

    fireEvent.click(element2);

    expect(element).toBeInTheDocument();
    expect(element).toBeVisible();

    expect(element.style.color.includes("rgb(16, 17, 19)")).toBe(false);

    expect(element2).toBeInTheDocument();
    expect(element2).toBeVisible();
    expect(element2.style.color.includes("rgb(16, 17, 19)")).toBe(true);
  });

  it("custom select style should surface in the UI", () => {
    const buttons = [
      {
        to: "/",
        label: "Home",
      },
      {
        to: "/leaderboard",
        label: "Leaderboard",
      },
    ];

    renderProviderFn?.(
      <TransitionalButtons
        buttons={buttons}
        active={{
          color: theme.colors.blue[4],
        }}
      />,
    );
    const element = screen.getByTestId(
      `transitional-button-${buttons[0]?.label}`,
    );
    const element2 = screen.getByTestId(
      `transitional-button-${buttons[1]?.label}`,
    );

    fireEvent.click(element2);

    expect(element).toBeInTheDocument();
    expect(element).toBeVisible();

    expect(element.style.color.includes("rgb(77, 171, 247)")).toBe(false);

    expect(element2).toBeInTheDocument();
    expect(element2).toBeVisible();
    expect(element2.style.color.includes("rgb(77, 171, 247)")).toBe(true);
  });
});
