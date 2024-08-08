package gregicality.multiblocks.api.capability.impl;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import gregtech.api.GTValues;
import gregtech.api.capability.impl.MultiblockRecipeLogic;
import gregtech.api.metatileentity.ITieredMetaTileEntity;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.recipes.Recipe;
import gregtech.api.recipes.recipeproperties.IRecipePropertyStorage;

import gregicality.multiblocks.api.capability.IUpgradeableMultiblock;
import gregicality.multiblocks.api.metatileentity.GCYMMultiblockAbility;
import gregicality.multiblocks.api.metatileentity.GCYMRecipeMapMultiblockController;
import gregicality.multiblocks.common.GCYMConfigHolder;

public class GCYMMultiblockRecipeLogic extends MultiblockRecipeLogic {

    public GCYMMultiblockRecipeLogic(RecipeMapMultiblockController tileEntity) {
        super(tileEntity);
    }

    @Override
    public @NotNull RecipeMapMultiblockController getMetaTileEntity() {
        return (RecipeMapMultiblockController) super.getMetaTileEntity();
    }

    @Override
    public int getParallelLimit() {
        if (metaTileEntity instanceof IUpgradeableMultiblock &&
                ((IUpgradeableMultiblock) metaTileEntity).isUpgradeable())
            return ((IUpgradeableMultiblock) metaTileEntity).getTotalParallel();
        return 1;
    }

    @Override
    protected boolean canProgressRecipe() {
        return ((IUpgradeableMultiblock) metaTileEntity).hasMemoryCapacity() && super.canProgressRecipe();
    }

    @Override
    public boolean checkRecipe(@NotNull Recipe recipe) {
        return ((IUpgradeableMultiblock) metaTileEntity).hasMemoryCapacity() && super.checkRecipe(recipe);
    }

    @Override
    protected void modifyOverclockPre(int @NotNull [] values, @NotNull IRecipePropertyStorage storage) {
        super.modifyOverclockPre(values, storage);

        double finalEUDiscount;

        finalEUDiscount = ((IUpgradeableMultiblock) metaTileEntity).getTotalEUtDiscount();

        values[0] = (int) (values[0] * finalEUDiscount);
    }

    @Override
    protected void modifyOverclockPost(int[] overclockResults, @NotNull IRecipePropertyStorage storage) {
        super.modifyOverclockPost(overclockResults, storage);

        double processingSpeedModifier;

        processingSpeedModifier = ((IUpgradeableMultiblock) metaTileEntity).getUpgradeSpeedBonus();

        overclockResults[1] = (int) (overclockResults[1] * processingSpeedModifier);
    }

    @Override
    public long getMaxVoltage() {
        if (!GCYMConfigHolder.globalMultiblocks.enableTieredCasings)
            return super.getMaxVoltage();

        if (getMetaTileEntity() instanceof GCYMRecipeMapMultiblockController controller && !controller.isTiered())
            return super.getMaxVoltage();

        List<ITieredMetaTileEntity> list = getMetaTileEntity().getAbilities(GCYMMultiblockAbility.TIERED_HATCH);

        if (list.isEmpty())
            return super.getMaxVoltage();

        return Math.min(GTValues.V[list.get(0).getTier()], super.getMaxVoltage());
    }
}
