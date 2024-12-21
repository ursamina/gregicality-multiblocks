package gregicality.multiblocks.common.metatileentities;

import static gregicality.multiblocks.api.utils.GCYMUtil.gcymId;
import static gregtech.common.metatileentities.MetaTileEntities.registerMetaTileEntity;

import gregicality.multiblocks.common.metatileentities.multiblock.standard.*;

public final class GCYMMetaTileEntities {

    public static MetaTileEntityAlloyBlastSmelter ALLOY_BLAST_SMELTER;

    public static MetaTileEntityLargeArcFurnace LARGE_ARC_FURNACE;
    public static MetaTileEntityLargeAssembler LARGE_ASSEMBLER;

    private GCYMMetaTileEntities() {}

    public static void init() {
        // Multiblocks
        ALLOY_BLAST_SMELTER = registerMetaTileEntity(2000,
                new MetaTileEntityAlloyBlastSmelter(gcymId("alloy_blast_smelter")));

        LARGE_ARC_FURNACE = registerMetaTileEntity(2001,
                new MetaTileEntityLargeArcFurnace(gcymId("large_arc_furnace")));
        LARGE_ASSEMBLER = registerMetaTileEntity(2002,
                new MetaTileEntityLargeAssembler(gcymId("large_assembler")));
    }
}
