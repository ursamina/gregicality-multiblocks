package gregicality.multiblocks.common.metatileentities.multiblock.standard;

import static gregicality.multiblocks.api.pattern.GCYMTraceabilityPredicate.superconductorCoils;
import static gregicality.multiblocks.api.pattern.GCYMTraceabilityPredicate.tieredComponents;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;

import org.jetbrains.annotations.NotNull;

import gregtech.api.capability.impl.MultiblockRecipeLogic;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.recipes.recipeproperties.IRecipePropertyStorage;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;

import gregicality.multiblocks.api.metatileentity.GCYMMultiblockController;
import gregicality.multiblocks.api.render.GCYMTextures;
import gregicality.multiblocks.common.block.GCYMMetaBlocks;
import gregicality.multiblocks.common.block.blocks.BlockLargeMultiblockCasing;

public class MetaTileEntityLargeArcFurnace extends GCYMMultiblockController {

    private static final int OC_PARALLELS = 3;

    public MetaTileEntityLargeArcFurnace(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, RecipeMaps.ARC_FURNACE_RECIPES);
        this.recipeMapWorkable = new LargeMaceratorRecipeLogic(this);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity metaTileEntityHolder) {
        return new MetaTileEntityLargeArcFurnace(this.metaTileEntityId);
    }

    @Override
    protected @NotNull BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("#XXX#", "#XXX#", "#XXX#", "#XXX#")
                .aisle("XXXXX", "XCACX", "XCACX", "XXXXX")
                .aisle("XXXXX", "XATAX", "XATAX", "XXMXX")
                .aisle("XXXXX", "XACAX", "XACAX", "XXXXX")
                .aisle("#XXX#", "#XSX#", "#XXX#", "#XXX#")
                .where('S', selfPredicate())
                .where('X', states(getCasingState()).setMinGlobalLimited(45)
                        .or(autoAbilities(true, true, true, true, true, true, false)))
                .where('C', superconductorCoils())
                .where('M', abilities(MultiblockAbility.MUFFLER_HATCH))
                .where('T', tieredComponents())
                .where('A', air())
                .where('#', any())
                .build();
    }

    private static IBlockState getCasingState() {
        return GCYMMetaBlocks.LARGE_MULTIBLOCK_CASING
                .getState(BlockLargeMultiblockCasing.CasingType.HIGH_TEMPERATURE_CASING);
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return GCYMTextures.BLAST_CASING;
    }

    @Override
    protected @NotNull OrientedOverlayRenderer getFrontOverlay() {
        return GCYMTextures.LARGE_ARC_FURNACE_OVERLAY;
    }

    @Override
    public boolean canBeDistinct() {
        return true;
    }

    private class LargeMaceratorRecipeLogic extends MultiblockRecipeLogic {

        public LargeMaceratorRecipeLogic(RecipeMapMultiblockController tileEntity) {
            super(tileEntity);
        }

        @Override
        public int getParallelLimit() {
            return switch (componentTier) {
                case 4, 5 -> super.getNumberOfOCs(recipeEUt) * OC_PARALLELS * 2;
                default -> super.getNumberOfOCs(recipeEUt) * OC_PARALLELS;
            };
        }

        @Override
        protected void modifyOverclockPost(int[] resultOverclock, @NotNull IRecipePropertyStorage storage) {
            super.modifyOverclockPost(resultOverclock, storage);

            int coilTier = ((MetaTileEntityLargeArcFurnace) metaTileEntity).getComponentTier();

            if (coilTier < 2)
                return;

            switch (coilTier) {
                case 2 -> resultOverclock[0] = (int) (0.75 * resultOverclock[0]);
                case 3, 4 -> {
                    resultOverclock[0] = (int) (0.75 * resultOverclock[0]);
                    resultOverclock[1] = (int) (resultOverclock[1] * (1 - superconductorTier * 0.025));
                }
                case 5 -> {
                    resultOverclock[0] = (int) (0.75 * resultOverclock[0]);
                    resultOverclock[1] = (int) (resultOverclock[1] * (1 - superconductorTier * 0.05));
                }
            }
            resultOverclock[1] = Math.max(1, resultOverclock[1]);
        }
    }
}
