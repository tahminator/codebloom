import { UserTagTag } from "@/lib/api/types/autogen/schema";
import { ApiUtils } from "@/lib/api/utils";
import { useCallback } from "react";
import { useSearchParams } from "react-router-dom";
import { useImmer } from "use-immer";

export type TagEnumToBooleanFilterObject = Record<UserTagTag, boolean>;

function getUrlKey(tagEnum: UserTagTag) {
  return ApiUtils.getMetadataByTagEnum(tagEnum).apiKey;
}

export function useFilters() {
  const [searchParams, setSearchParams] = useSearchParams();
  const [filters, setFilters] = useImmer<TagEnumToBooleanFilterObject>(
    () =>
      Object.fromEntries(
        Object.values(UserTagTag)
          .filter(ApiUtils._isSupportedTagEnum)
          .map((tagEnum) => [
            tagEnum,
            searchParams.get(getUrlKey(tagEnum)) === "true",
          ]),
      ) as TagEnumToBooleanFilterObject,
  );

  const toggleFilter = useCallback(
    (tagEnum: UserTagTag) => {
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

  return { filters, toggleFilter };
}
