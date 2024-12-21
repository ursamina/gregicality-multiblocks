package gregicality.multiblocks.common.block.blocks;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import org.jetbrains.annotations.NotNull;

import gregtech.api.block.VariantBlock;

public class BlockSuperconductorCoil extends VariantBlock<BlockSuperconductorCoil.CasingType> {

    public BlockSuperconductorCoil() {
        super(Material.IRON);
        setTranslationKey("superconductor_coil");
        setHardness(5.0f);
        setResistance(10.0f);
        setSoundType(SoundType.METAL);
        setHarvestLevel("wrench", 2);
        setDefaultState(getState(CasingType.COIL_LV));
    }

    @Override
    public boolean canCreatureSpawn(@NotNull IBlockState state, @NotNull IBlockAccess world, @NotNull BlockPos pos,
                                    @NotNull EntityLiving.SpawnPlacementType type) {
        return false;
    }

    public enum CasingType implements IStringSerializable {

        COIL_LV("low_voltage"),
        COIL_MV("medium_voltage"),
        COIL_HV("high_voltage"),
        COIL_EV("extreme_voltage"),
        COIL_IV("insane_voltage"),
        COIL_LuV("ludicrous_voltage"),
        COIL_ZPM("zero_point_module"),
        COIL_UV("ultimate_voltage"),
        COIL_UHV("ultra_high_voltage");

        private final String name;

        CasingType(String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getName() {
            return this.name;
        }
    }
}
