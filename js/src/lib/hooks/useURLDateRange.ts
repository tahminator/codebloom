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

// See 618: Hacky solution to stagger the render loop between startDate and endDate
// so that they don't work with stale data and override each other.
const STAGGER_MS = 5;

/**
 * Wrapper over {@code useURLState} that allows empty date ranges in URL but is represented
 * as `undefined` within the codebase.
 *
 * @returns {DateRangeStateObject} A paginator object containing:
 * - `startDate` (string | undefined): Start date.
 * - `endDate` (string | undefined): End date.
 * - `setStartDate` (function): Set start date.
 * - `setEndDate` (function): Set end date.
 * - `debouncedStartDate` (string | undefined): Start date, debounced
 * - `debouncedEndDate` (string | undefined): End date, debounced
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
      debounce: Math.max(0, debounce),
    },
  );
  const [_endDate, _setEndDate, _debouncedEndDate] = useURLState<string>(
    "endDate",
    "",
    {
      enabled,
      debounce: Math.max(0, debounce) + STAGGER_MS,
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
