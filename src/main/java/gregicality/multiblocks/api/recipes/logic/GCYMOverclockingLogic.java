package gregicality.multiblocks.api.recipes.logic;

import static gregtech.api.recipes.logic.OverclockingLogic.*;

import org.jetbrains.annotations.NotNull;

public class GCYMOverclockingLogic {

    public static int @NotNull [] largeAssemblerOverclockingLogic(int recipeEUt, long maximumVoltage,
                                                                  int recipeDuration,
                                                                  int maxOverclocks) {
        int amountPerfectOC = (int) maxOverclocks / 3;

        // perfect overclock for every 1800k over recipe temperature
        if (amountPerfectOC > 0) {
            // use the normal overclock logic to do perfect OCs up to as many times as calculated
            int[] overclock = standardOverclockingLogic(recipeEUt, maximumVoltage, recipeDuration, amountPerfectOC,
                    PERFECT_OVERCLOCK_DURATION_DIVISOR, STANDARD_OVERCLOCK_VOLTAGE_MULTIPLIER);

            // overclock normally as much as possible after perfects are exhausted
            return standardOverclockingLogic(overclock[0], maximumVoltage, overclock[1],
                    maxOverclocks - amountPerfectOC, STANDARD_OVERCLOCK_DURATION_DIVISOR,
                    STANDARD_OVERCLOCK_VOLTAGE_MULTIPLIER);
        }

        // no perfects are performed, do normal overclocking
        return standardOverclockingLogic(recipeEUt, maximumVoltage, recipeDuration, maxOverclocks,
                STANDARD_OVERCLOCK_DURATION_DIVISOR, STANDARD_OVERCLOCK_VOLTAGE_MULTIPLIER);
    }
}
