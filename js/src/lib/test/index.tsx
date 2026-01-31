import { themeOverride } from "@/lib/theme";
import { MantineProvider } from "@mantine/core";
import { Notifications } from "@mantine/notifications";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { render, RenderResult } from "@testing-library/react";
import { ReactElement } from "react";
import { MemoryRouter } from "react-router";

export namespace TestUtilTypes {
  export type RenderWithAllProvidersFn = (
    ui: ReactElement,
  ) => RenderResult<
    typeof import("/Users/tahminator/personal/github/codebloom/js/node_modules/.pnpm/@testing-library+dom@10.4.1/node_modules/@testing-library/dom/types/queries"),
    HTMLElement,
    HTMLElement
  >;
}

export class TestUtils {
  static getRenderWithAllProvidersFn(): TestUtilTypes.RenderWithAllProvidersFn {
    const queryClient = new QueryClient({
      defaultOptions: {
        queries: {
          retry: false,
        },
      },
    });

    return (ui: ReactElement) => {
      return render(
        <QueryClientProvider client={queryClient}>
          <MantineProvider theme={themeOverride} forceColorScheme={"dark"}>
            <MemoryRouter>{ui}</MemoryRouter>
            <Notifications />
          </MantineProvider>
        </QueryClientProvider>,
      );
    };
  }
}
