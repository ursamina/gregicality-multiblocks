package gregicality.multiblocks.common.metatileentities.multiblock.standard;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;

import org.jetbrains.annotations.NotNull;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.interfaces.IGregTechTileEntity;
import gregtech.api.metatileentity.multiblock.IMultiblockPart;
import gregtech.api.pattern.BlockPattern;
import gregtech.api.pattern.FactoryBlockPattern;
import gregtech.api.recipes.RecipeMaps;
import gregtech.client.renderer.ICubeRenderer;
import gregtech.client.renderer.texture.cube.OrientedOverlayRenderer;

import gregicality.multiblocks.api.metatileentity.GCYMRecipeMapMultiblockController;
import gregicality.multiblocks.api.render.GCYMTextures;
import gregicality.multiblocks.common.block.GCYMMetaBlocks;
import gregicality.multiblocks.common.block.blocks.BlockLargeMultiblockCasing;
import gregicality.multiblocks.common.block.blocks.BlockUniqueCasing;

public class MetaTileEntityLargeMacerator extends GCYMRecipeMapMultiblockController {

    public MetaTileEntityLargeMacerator(ResourceLocation metaTileEntityId) {
        super(metaTileEntityId, RecipeMaps.MACERATOR_RECIPES);
        this.setParallelScalar(8);
        this.recipeMapWorkable.setSpeedBonus(0.625);
    }

    @Override
    public MetaTileEntity createMetaTileEntity(IGregTechTileEntity metaTileEntityHolder) {
        return new MetaTileEntityLargeMacerator(this.metaTileEntityId);
    }

    @Override
    protected @NotNull BlockPattern createStructurePattern() {
        return FactoryBlockPattern.start()
                .aisle("XXXXX", "XXXXX", "XXXXX", "XXXXX")
                .aisle("XXXXX", "XCCCX", "XCCCX", "X###X")
                .aisle("XXXXX", "XCTCX", "XCCCX", "X###X")
                .aisle("XXXXX", "XCCCX", "XCCCX", "X###X")
                .aisle("XXXXX", "XXSXX", "XXXXX", "XXXXX")
                .where('S', selfPredicate())
                .where('X', states(getCasingState()).setMinGlobalLimited(55).or(autoAbilities()))
                .where('C', states(getCasingState2()))
                .where('T', tieredCasing().or(states(getCasingState2())))
                .where('#', air())
                .build();
    }

    private static IBlockState getCasingState() {
        return GCYMMetaBlocks.LARGE_MULTIBLOCK_CASING.getState(BlockLargeMultiblockCasing.CasingType.MACERATOR_CASING);
    }

    private static IBlockState getCasingState2() {
        return GCYMMetaBlocks.UNIQUE_CASING.getState(BlockUniqueCasing.UniqueCasingType.CRUSHING_WHEELS);
    }

    @Override
    public ICubeRenderer getBaseTexture(IMultiblockPart iMultiblockPart) {
        return GCYMTextures.MACERATOR_CASING;
    }

    @Override
    protected @NotNull OrientedOverlayRenderer getFrontOverlay() {
        return GCYMTextures.LARGE_MACERATOR_OVERLAY;
    }
}
