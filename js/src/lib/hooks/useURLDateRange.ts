import { useURLState } from "@/lib/hooks/useUrlState";
import { useMemo, useCallback } from "react";

type DateRangeStateObject = {
  startDate: string | undefined;
  endDate: string | undefined;
  setStartDate: (date: string | undefined) => void;
  setEndDate: (date: string | undefined) => void;
  debouncedEndDate: string | undefined;
  debouncedStartDate: string | undefined;
};

/**
 * Wrapper over {@code useURLState} that allows empty date ranges in URL but is represented
 * as `undefined` within the codebase.
 *
 * @returns {DateRangeStateObject} A paginator object containing:
 * - `startDate` (number): Start date as `string | undefined`.
 * - `endDate` (number): End date as `string | undefined`.
 * - `setStartDate` (function): Set start date.
 * - `setEndDate` (function): Set end date.
 * - `debouncedStartDate` (number): Start date as `string | undefined`, debounced
 * - `debouncedEndDate` (number): End date as `string | undefined`, debounced
 */
export default function useURLDateRange(
  enabled = false,
  debounce = 500,
): DateRangeStateObject {
  const [_startDate, _setStartDate, _debouncedStartDate] = useURLState<string>(
    "startDate",
    "",
    {
      enabled,
      debounce,
    },
  );
  const [_endDate, _setEndDate, _debouncedEndDate] = useURLState<string>(
    "endDate",
    "",
    {
      enabled,
      debounce,
    },
  );

  const startDate = useMemo(
    () => (!_startDate ? undefined : _startDate),
    [_startDate],
  );

  const endDate = useMemo(() => (!_endDate ? undefined : _endDate), [_endDate]);

  const debouncedStartDate = useMemo(
    () => (!_debouncedStartDate ? undefined : _debouncedStartDate),
    [_debouncedStartDate],
  );

  const debouncedEndDate = useMemo(
    () => (!_debouncedEndDate ? undefined : _debouncedEndDate),
    [_debouncedEndDate],
  );

  const setStartDate = useCallback(
    (date: string | undefined) => {
      return !date ? _setStartDate("") : _setStartDate(date);
    },
    [_setStartDate],
  );

  const setEndDate = useCallback(
    (date: string | undefined) => {
      return !date ? _setEndDate("") : _setEndDate(date);
    },
    [_setEndDate],
  );
  return {
    startDate,
    endDate,
    setStartDate,
    setEndDate,
    debouncedEndDate,
    debouncedStartDate,
  };
}
