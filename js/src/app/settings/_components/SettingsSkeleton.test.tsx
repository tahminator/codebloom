import SettingsSkeleton from "@/app/settings/_components/SettingsSkeleton";
import { TestUtils, TestUtilTypes } from "@/lib/test";
import { screen } from "@testing-library/react";

describe("SettingsSkeleton", () => {
  let renderProviderFn: TestUtilTypes.RenderWithAllProvidersFn | null = null;

  beforeEach(() => {
    renderProviderFn = TestUtils.getRenderWithAllProvidersFn();
  });

  it("should render title skeleton", () => {
    renderProviderFn?.(<SettingsSkeleton />);
    const element = screen.getByTestId("settings-skeleton-title");
    expect(element).toBeInTheDocument();
    expect(element).toBeVisible();
  });

  it("should render verify school card skeleton", () => {
    renderProviderFn?.(<SettingsSkeleton />);
    const element = screen.getByTestId("settings-skeleton-verify-school-card");
    expect(element).toBeInTheDocument();
    expect(element).toBeVisible();
  });

  it("should render verify school title skeleton", () => {
    renderProviderFn?.(<SettingsSkeleton />);
    const element = screen.getByTestId("settings-skeleton-verify-school-title");
    expect(element).toBeInTheDocument();
    expect(element).toBeVisible();
  });

  it("should render verify school description 1 skeleton", () => {
    renderProviderFn?.(<SettingsSkeleton />);
    const element = screen.getByTestId(
      "settings-skeleton-verify-school-description-1",
    );
    expect(element).toBeInTheDocument();
    expect(element).toBeVisible();
  });

  it("should render verify school description 2 skeleton", () => {
    renderProviderFn?.(<SettingsSkeleton />);
    const element = screen.getByTestId(
      "settings-skeleton-verify-school-description-2",
    );
    expect(element).toBeInTheDocument();
    expect(element).toBeVisible();
  });

  it("should render verify school list skeletons", () => {
    renderProviderFn?.(<SettingsSkeleton />);
    const elements = screen.queryAllByTestId(
      "settings-skeleton-verify-school-list",
    );
    expect(elements).toHaveLength(9);
    for (const element of elements) {
      expect(element).toBeInTheDocument();
      expect(element).toBeVisible();
    }
  });

  it("should render verify now button skeleton", () => {
    renderProviderFn?.(<SettingsSkeleton />);
    const element = screen.getByTestId("settings-skeleton-verify-now-button");
    expect(element).toBeInTheDocument();
    expect(element).toBeVisible();
  });

  it("should render change profile card skeleton", () => {
    renderProviderFn?.(<SettingsSkeleton />);
    const element = screen.getByTestId("settings-skeleton-change-profile-card");
    expect(element).toBeInTheDocument();
    expect(element).toBeVisible();
  });

  it("should render change profile title skeleton", () => {
    renderProviderFn?.(<SettingsSkeleton />);
    const element = screen.getByTestId(
      "settings-skeleton-change-profile-title",
    );
    expect(element).toBeInTheDocument();
    expect(element).toBeVisible();
  });

  it("should render change profile description skeletons", () => {
    renderProviderFn?.(<SettingsSkeleton />);
    const description1 = screen.getByTestId(
      "settings-skeleton-change-profile-description-1",
    );
    const description2 = screen.getByTestId(
      "settings-skeleton-change-profile-description-2",
    );
    const description3 = screen.getByTestId(
      "settings-skeleton-change-profile-description-3",
    );
    expect(description1).toBeInTheDocument();
    expect(description1).toBeVisible();
    expect(description2).toBeInTheDocument();
    expect(description2).toBeVisible();
    expect(description3).toBeInTheDocument();
    expect(description3).toBeVisible();
  });

  it("should render log out card skeleton", () => {
    renderProviderFn?.(<SettingsSkeleton />);
    const element = screen.getByTestId("settings-skeleton-log-out-card");
    expect(element).toBeInTheDocument();
    expect(element).toBeVisible();
  });

  it("should render log out title skeleton", () => {
    renderProviderFn?.(<SettingsSkeleton />);
    const element = screen.getByTestId("settings-skeleton-log-out-title");
    expect(element).toBeInTheDocument();
    expect(element).toBeVisible();
  });

  it("should render log out description skeleton", () => {
    renderProviderFn?.(<SettingsSkeleton />);
    const element = screen.getByTestId("settings-skeleton-log-out-description");
    expect(element).toBeInTheDocument();
    expect(element).toBeVisible();
  });

  it("should render log out all sessions button skeleton", () => {
    renderProviderFn?.(<SettingsSkeleton />);
    const element = screen.getByTestId(
      "settings-skeleton-log-out-all-sessions-button",
    );
    expect(element).toBeInTheDocument();
    expect(element).toBeVisible();
  });
});
