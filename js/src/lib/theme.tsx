import {
  createTheme,
  CSSVariablesResolver,
  DEFAULT_THEME,
  mergeMantineTheme,
  rem,
  Tooltip,
} from "@mantine/core";

/**
 * Custom mantine theme override
 *
 * This theme customization includes setting the "Figtree" font family for both
 * body text and headings, with specific sizes and weights for various heading levels.
 *
 * Font: Figtree
 * Weights: 400 (Regular), 600 (Semibold), 700 (Bold)
 *
 *
 * {@link https://mantine.dev/theming/theme-object/ Theme Object Docs}
 * {@link https://github.com/mantinedev/mantine/blob/master/packages/%40mantine/core/src/core/MantineProvider/default-theme.ts Default Theme}
 * {@link https://github.com/mantinedev/mantine/blob/master/packages/%40mantine/core/src/core/MantineProvider/default-colors.ts Default Colors}
 */
export const themeOverride = createTheme({
  components: {
    Tooltip: Tooltip.extend({
      defaultProps: {
        events: { hover: true, focus: true, touch: true },
      },
    }),
  },
  autoContrast: true,
  primaryColor: "patina",
  colors: {
    patina: [
      "#e2fff3",
      "#cffae8",
      "#a4f4d0",
      "#75edb8",
      "#4DFFB0", // Primary
      "#33e495",
      "#20e28d",
      "#0ac87a",
      "#00b36b",
      "#009b59",
    ],
    dark: [
      "#FFFFFF", // dark-0
      // '#C1C2C5', // dark-0
      "#A6A7AB", // dark-1
      "#909296", // dark-2
      "#5C5F66", // dark-3
      "#373A40", // dark-4
      "#2C2E33", // dark-5
      "#25262B", // dark-6
      "#1A1B1E", // dark-7 Default Dark Mode Background
      "#141517", // dark-8
      "#101113", // dark-9
    ],
  },
  headings: {
    fontFamily: "Figtree, sans-serif",
    sizes: {
      h1: { fontSize: rem(64), fontWeight: "700" },
      h2: { fontSize: rem(48), fontWeight: "700" },
      h3: { fontSize: rem(34), fontWeight: "700" },
      h4: { fontSize: rem(22), fontWeight: "700" },
      h5: { fontSize: rem(16), fontWeight: "600" },
    },
  },
  fontFamily: "Figtree, sans-serif",
  fontSizes: {
    xs: rem(12),
    sm: rem(14),
    md: rem(16), // Body text size
    lg: rem(20),
    xl: rem(24),
  },
  lineHeights: {
    xs: "1.4",
    sm: "1.45",
    md: "1.55",
    lg: "1.6",
    xl: "1.65",
  },
  radius: {
    xs: rem(2),
    sm: rem(4),
    md: rem(8),
    lg: rem(16),
    xl: rem(32),
  },
  spacing: {
    xs: rem(10),
    sm: rem(12),
    md: rem(16),
    lg: rem(20),
    xl: rem(32),
  },
  breakpoints: {
    xs: "36em", // 576px - Mobile
    sm: "48em", // 768px - Half Screen Default Mac?
    md: "62em", // 992px - Half Screen 1920x1080p
    lg: "75em", // 1200px
    xl: "88em", // 1408px - Anything not fullscreen
  },
  other: {
    patinaGreenDark: "#03664D",
    patinaGreenLight: "#4DFFB0",
    patinaBlueDark: "#1550C4",
    patinaBlueLight: "#47FFFF",
    patinaRedDark: "#BB352C",
    patinaRedLight: "#FF3D3D",
    contentContainerWidth: 1200,
  },
});

export const theme = mergeMantineTheme(DEFAULT_THEME, themeOverride);

/**
 * Add custom CSS variables to keep consistency across the codebase.
 * Generally these should be used instead of raw values.
 * Usage: color: var(--mantine-color-patina-green-light)
 */
export const resolver: CSSVariablesResolver = (computedTheme) => ({
  variables: {
    "--mantine-color-patina-green-light": computedTheme.other.patinaGreenLight,
    "--mantine-color-patina-blue-light": computedTheme.other.patinaBlueLight,
    "--mantine-color-patina-red-light": computedTheme.other.patinaRedLight,
    "--mantine-color-patina-green-dark": computedTheme.other.patinaGreenDark,
    "--mantine-color-patina-blue-dark": computedTheme.other.patinaBlueDark,
    "--mantine-color-patina-red-dark": computedTheme.other.patinaRedDark,
  },
  light: {},
  dark: {},
});
