import { MOCK_USER_ID } from "@/__mock__/user/index.ts";
import ProfilePicture from "@/app/user/[userId]/_components/UserProfile/ProfilePicture/ProfilePicture.tsx";
import { TestUtils, TestUtilTypes } from "@/lib/test";
import { screen, cleanup } from "@testing-library/react";

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
