import { MOCK_USER_ID } from "@/__mock__/user/index.ts";
import { screen, cleanup } from "@testing-library/react";
import { TestUtils, TestUtilTypes } from "@/lib/test";
import ProfilePicture from "./ProfilePicture.tsx";

describe("ProfilePicture succeeded", () => {
  afterEach(() => {
    cleanup();
  });

  let renderProviderFn: TestUtilTypes.RenderWithAllProvidersFn | null = null;
  beforeEach(() => {
    renderProviderFn = TestUtils.getRenderWithAllProvidersFn();
  });

  it("should render skeleton profile picture initially", () => {
    renderProviderFn?.(<ProfilePicture userId={MOCK_USER_ID} />);
    const element = screen.getByTestId("user-profile-skeleton-picture");
    expect(element).toBeInTheDocument();
    expect(element).toBeVisible();
  });
});
