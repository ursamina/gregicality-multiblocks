package gregicality.multiblocks.common.metatileentities.multiblock.standard;

import static gregicality.multiblocks.api.pattern.GCYMTraceabilityPredicate.superconductorCoils;
import static gregicality.multiblocks.api.pattern.GCYMTraceabilityPredicate.tieredComponents;
import static gregicality.multiblocks.api.recipes.logic.GCYMOverclockingLogic.largeAssemblerOverclockingLogic;
import static gregtech.api.util.RelativeDirection.*;

import gregicality.multiblocks.common.block.blocks.BlockSuperconductorCoil;
import gregicality.multiblocks.common.block.blocks.BlockTieredComponent;
import gregtech.api.pattern.PatternMatchContext;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;

import org.jetbrains.annotations.NotNull;

import gregtech.api.capability.impl.MultiblockRecipeLogic;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.RecipeMapMultiblockController;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.recipes.RecipeMaps;
import gregtech.api.recipes.recipeproperties.IRecipePropertyStorage;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;
import gregtech.common.blocks.BlockGlassCasing;
import gregtech.common.blocks.MetaBlocks;

import gregicality.multiblocks.api.GCYMValues;
import gregicality.multiblocks.api.metatileentity.GCYMMultiblockController;
import gregicality.multiblocks.api.render.GCYMTextures;
import gregicality.multiblocks.common.block.GCYMMetaBlocks;
import gregicality.multiblocks.common.block.blocks.BlockLargeMultiblockCasing;

public class MetaTileEntityLargeAssembler extends GCYMMultiblockController {

    private static final int OC_PARALLELS = 2;

    public MetaTileEntityLargeAssembler(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, determineRecipeMaps());
        this.recipeMapWorkable = new LargeAssemblerRecipeLogic(this);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity metaTileEntityHolder) {
        return new MetaTileEntityLargeAssembler(this.metaTileEntityId);
    }

    @Override
    protected void formStructure(PatternMatchContext context) {


        Object type = context.get("SuperconductorTier");
        if (type instanceof BlockSuperconductorCoil.CasingType tier)
            this.superconductorTier = tier.ordinal() + 1;
        else this.superconductorTier = -1;

        Object type2 = context.get("ComponentTier");
        if (type2 instanceof BlockTieredComponent.CasingType tier)
            this.componentTier = tier.ordinal() + 1;
        else this.componentTier = -1;

        super.formStructure(context);

    }

    @Override
    protected @NotNull BlockPattern createStructurePattern() {

        return FactoryBlockPattern.start(FRONT, UP, RIGHT)
                .aisle("XXX", "XXX", "XXX")
                .aisle("XXX", "CAX", "CCX").setRepeatable(3)
                .aisle("XXX", "XXX", "XXX")
                .aisle("XXX", "XTX", "#XX")
                .aisle("XXX", "SYX", "#XX")
                .aisle("XXX", "XTX", "#XX")
                .aisle("XXX", "XXX", "XXX")
                .where('S', selfPredicate())
                .where('X', states(getCasingState()).setMinGlobalLimited(40)
                        .or(autoAbilities(false, true, true, true, true, true, true))
                        .or(this.getComponentTier() == 5 || this.getComponentTier() == -1 ?
                                abilities(MultiblockAbility.INPUT_ENERGY).setExactLimit(1) : abilities(MultiblockAbility.INPUT_ENERGY).setMaxGlobalLimited(2)))
                .where('C', states(getCasingState2()))
                .where('A', air())
                .where('Y', superconductorCoils())
                .where('T', tieredComponents())
                .where('#', any()) // todo fix this piece of shit energy hatch restriction
                .build();
    }

    private static IBlockState getCasingState() {
        return GCYMMetaBlocks.LARGE_MULTIBLOCK_CASING.getState(BlockLargeMultiblockCasing.CasingType.ASSEMBLING_CASING);
    }

    private static IBlockState getCasingState2() {
        return MetaBlocks.TRANSPARENT_CASING.getState(BlockGlassCasing.CasingType.TEMPERED_GLASS);
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return GCYMTextures.ASSEMBLING_CASING;
    }

    @Override
    protected @NotNull OrientedOverlayRenderer getFrontOverlay() {
        return GCYMTextures.LARGE_ASSEMBLER_OVERLAY;
    }

    @Override
    public boolean canBeDistinct() {
        return true;
    }

    private static @NotNull RecipeMap<?> @NotNull [] determineRecipeMaps() {
        RecipeMap<?> cuisineAssemblerMap = RecipeMap.getByName("cuisine_assembler");
        if (Loader.isModLoaded(GCYMValues.GTFO_MODID) && cuisineAssemblerMap != null) {
            return new RecipeMap<?>[] { RecipeMaps.ASSEMBLER_RECIPES, cuisineAssemblerMap };
        }
        return new RecipeMap<?>[] { RecipeMaps.ASSEMBLER_RECIPES };
    }

    private class LargeAssemblerRecipeLogic extends MultiblockRecipeLogic {

        public LargeAssemblerRecipeLogic(RecipeMapMultiblockController tileEntity) {
            super(tileEntity);
        }

        @Override
        public int getParallelLimit() {
            return switch (componentTier) {
                case 3 -> (int) Math
                        .floor(super.getNumberOfOCs(recipeEUt) * OC_PARALLELS * Math.pow(1.3, superconductorTier));
                case 4, 5 -> (int) Math
                        .floor(super.getNumberOfOCs(recipeEUt) * OC_PARALLELS * 2 *
                                Math.pow(1.3, superconductorTier));
                default -> super.getNumberOfOCs(recipeEUt) * OC_PARALLELS;
            };
        }

        @Override
        protected int @NotNull [] runOverclockingLogic(@NotNull IRecipePropertyStorage propertyStorage, int recipeEUt,
                                                       long maxVoltage, int duration, int amountOC) {
            return largeAssemblerOverclockingLogic(
                    Math.abs(recipeEUt),
                    maxVoltage,
                    duration,
                    amountOC);
        }
    }
}
