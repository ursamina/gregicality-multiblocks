package gregicality.multiblocks.api.capability;

public interface IUpgradeableMultiblock {

    /**
     *
     * @return whether the multiblock can use parallel recipes
     */
    default boolean isUpgradeable() {
        return false;
    }

    /**
     *
     * @return the maximum amount of parallel recipes the multiblock can use
     */
    default int getTotalParallel() {
        return 1;
    }

    default double getTotalEUtDiscount() {
        return 0.5;
    }

    default double getUpgradeSpeedBonus() {
        return -1;
    }

    default boolean isLaserHatchUpgrade() {
        return false;
    }

    default boolean hasMemoryCapacity() {
        return true;
    };
}
