package gregicality.multiblocks.api.metatileentity;

import gregtech.api.metatileentity.ITieredMetaTileEntity;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;

import gregicality.multiblocks.api.capability.IUpgradeHatch;

@SuppressWarnings("InstantiationOfUtilityClass")
public final class GCYMMultiblockAbility {

    public static final MultiblockAbility<ITieredMetaTileEntity> TIERED_HATCH = new MultiblockAbility<>("tiered_hatch");
    public static final MultiblockAbility<IUpgradeHatch> UPGRADE_HATCH = new MultiblockAbility<>("upgrade_hatch");

    private GCYMMultiblockAbility() {}
}
