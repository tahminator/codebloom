# Scores

Codebloom has some custom algebraic functions to determine how points should be allocated depending on various properties.

> [!NOTE]
> We are always trying to improve our scoring formula, so if you have any suggestions - please open an [issue](https://github.com/tahminator/codebloom/issues/new) here.

The actual file used to calculate scores is called [`ScoreCalculator.java`](../../src/main/java/org/patinanetwork/codebloom/common/leetcode/score/ScoreCalculator.java). This class essentially requires 3 properties to produce a score:

- Difficulty (`EASY`, `MEDIUM`, `HARD`)
- Acceptance Rate (`0.0` to `1.0`)
- Multiplier (usually `1.0` unless it's a POTD problem which is then defined off mapped values based on problem difficulty + 5% deviation)

You may notice that we call a `purpleFunction`, `blueFunction`, etc. These are custom algebraic functions we have defined based off of previous feedback & internal standards for what we think makes the most sense.

These functions have a Desmos link to correlate the code to the graph inside of [`MultiplierFunctions.java`](../../src/main/java/org/patinanetwork/codebloom/common/leetcode/score/MultiplierFunctions.java), so feel free to view them to get the latest representation of any changes.

_Last updated: 02/15/2026_

Here is a current screenshot:
<img src="/screenshots/desmos-functions.png" alt="Codebloom - Scoring Functions in Desmos" />

Finally, we then apply a 5% deviation to the final result to have more variance in the outputted scores.
