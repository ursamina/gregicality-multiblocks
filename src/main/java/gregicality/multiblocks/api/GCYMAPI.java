package gregicality.multiblocks.api;

import net.minecraft.block.state.IBlockState;

import gregicality.multiblocks.common.block.blocks.BlockSuperconductorCoil;
import gregicality.multiblocks.common.block.blocks.BlockTieredComponent;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

public class GCYMAPI {

    public static final Object2ObjectMap<IBlockState, BlockSuperconductorCoil.CasingType> SUPERCONDUCTOR_COILS = new Object2ObjectOpenHashMap<>();
    public static final Object2ObjectMap<IBlockState, BlockTieredComponent.CasingType> TIERED_COMPONENTS = new Object2ObjectOpenHashMap<>();
}
