import { ApiUtils } from "@/lib/api/utils";
import { ApiTypeUtils } from "@/lib/api/utils/types";
import { useCallback, useMemo } from "react";
import { useSearchParams } from "react-router-dom";
import { useImmer } from "use-immer";

export type TagEnumToBooleanFilterObject = Record<
  ApiTypeUtils.FilteredUserTagTag,
  boolean
>;

export type ToggleTagEnumFn = (
  tagEnum: ApiTypeUtils.FilteredUserTagTag,
) => void;

function getUrlKey(tagEnum: ApiTypeUtils.FilteredUserTagTag) {
  return ApiUtils.getMetadataByTagEnum(tagEnum).apiKey;
}

/**
 * React hook that manages user tag filters.
 *
 * Each supported `UserTagTag` is represented as a boolean in the `filters` object.
 * When a filter is toggled, both the internal state and the URL search params update
 * to reflect the new value.
 *
 * Furthermore, this hook automatically initializes the filter state from the current
 * URL params and updates whenever filters are toggled.
 *
 * @returns An object containing:
 * - `filters`: an object with each key of `UserTagTag` mapping to a value to its current enabled/disabled state
 * - `toggleFilter`: a function to toggle an individual tag filter
 *
 * @example
 * ```tsx
 * const { filters, toggleFilter } = useFilters();
 *
 * return (
      <SegmentedControl
        value={filters.Patina ? "patina" : "all"}
        data={[
          { label: "All", value: "all" },
          { label: "Patina", value: "patina" },
        ]}
        onChange={() => toggleFilter(UserTagTag.Patina)}
      />
 * );
 * ```
 */
export function useFilters() {
  const [searchParams, setSearchParams] = useSearchParams();
  const [filters, setFilters] = useImmer<TagEnumToBooleanFilterObject>(() => {
    const kvTuples = ApiUtils.getAllSupportedTagEnums().map((tagEnum) => [
      tagEnum,
      searchParams.get(getUrlKey(tagEnum)) === "true",
    ]) as [ApiTypeUtils.FilteredUserTagTag, boolean][]; // slight type trickery because TS
    // doesn't know which type is a key and which is a value; we help it out here a little.

    return Object.typedFromEntries(kvTuples);
  });

  const toggleFilter: ToggleTagEnumFn = useCallback(
    (tagEnum: ApiTypeUtils.FilteredUserTagTag) => {
      const key = getUrlKey(tagEnum);
      const prevValue = filters[tagEnum];

      setFilters((prev) => {
        prev[tagEnum] = !prevValue;
      });

      setSearchParams((prev) => {
        if (!prevValue) {
          prev.set(key, "true");
        } else {
          prev.delete(key);
        }
        return prev;
      });
    },
    [filters, setFilters, setSearchParams],
  );

  const clearFilters = useCallback(() => {
    setFilters((prev) => {
      for (const tagEnum of Object.typedKeys(prev)) {
        if (prev[tagEnum] == true) prev[tagEnum] = false;
      }
    });

    const newSearchParams = new URLSearchParams(searchParams);
    for (const tagEnum of Object.typedKeys(filters)) {
      newSearchParams.delete(getUrlKey(tagEnum));
    }
    setSearchParams(newSearchParams);
  }, [filters, setFilters, searchParams, setSearchParams]);

  const isAnyFilterEnabled: boolean = useMemo(() => {
    return Object.values(filters).some(Boolean);
  }, [filters]);

  return { filters, toggleFilter, clearFilters, isAnyFilterEnabled };
}
