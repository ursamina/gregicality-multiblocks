package gregicality.multiblocks.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.jetbrains.annotations.NotNull;

import gregtech.common.blocks.MetaBlocks;

import gregicality.multiblocks.common.block.blocks.BlockLargeMultiblockCasing;
import gregicality.multiblocks.common.block.blocks.BlockSuperconductorCoil;
import gregicality.multiblocks.common.block.blocks.BlockTieredComponent;
import gregicality.multiblocks.common.block.blocks.BlockUniqueCasing;

public final class GCYMMetaBlocks {

    private GCYMMetaBlocks() {}

    public static BlockUniqueCasing UNIQUE_CASING;
    public static BlockLargeMultiblockCasing LARGE_MULTIBLOCK_CASING;

    public static BlockSuperconductorCoil SUPERCONDUCTOR_COIL;
    public static BlockTieredComponent TIERED_COMPONENT;

    public static void init() {
        UNIQUE_CASING = new BlockUniqueCasing();
        UNIQUE_CASING.setRegistryName("unique_casing");
        LARGE_MULTIBLOCK_CASING = new BlockLargeMultiblockCasing();
        LARGE_MULTIBLOCK_CASING.setRegistryName("large_multiblock_casing");
        SUPERCONDUCTOR_COIL = new BlockSuperconductorCoil();
        SUPERCONDUCTOR_COIL.setRegistryName("superconductor_coil");
        TIERED_COMPONENT = new BlockTieredComponent();
        TIERED_COMPONENT.setRegistryName("tiered_component");
    }

    @SideOnly(Side.CLIENT)
    public static void registerItemModels() {
        UNIQUE_CASING.onModelRegister();
        registerItemModel(LARGE_MULTIBLOCK_CASING);
        registerItemModel(SUPERCONDUCTOR_COIL);
        registerItemModel(TIERED_COMPONENT);
    }

    @SideOnly(Side.CLIENT)
    private static void registerItemModel(@NotNull Block block) {
        for (IBlockState state : block.getBlockState().getValidStates()) {
            // noinspection ConstantConditions
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block),
                    block.getMetaFromState(state),
                    new ModelResourceLocation(block.getRegistryName(),
                            MetaBlocks.statePropertiesToString(state.getProperties())));
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends Comparable<T>> @NotNull String getPropertyName(@NotNull IProperty<T> property,
                                                                             Comparable<?> value) {
        return property.getName((T) value);
    }
}
