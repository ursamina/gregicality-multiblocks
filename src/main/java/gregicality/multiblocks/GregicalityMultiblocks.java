package gregicality.multiblocks;

import static gregicality.multiblocks.api.GCYMAPI.SUPERCONDUCTOR_COILS;
import static gregicality.multiblocks.api.GCYMAPI.TIERED_COMPONENTS;
import static gregicality.multiblocks.common.block.GCYMMetaBlocks.SUPERCONDUCTOR_COIL;
import static gregicality.multiblocks.common.block.GCYMMetaBlocks.TIERED_COMPONENT;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import org.jetbrains.annotations.NotNull;

import gregtech.GTInternalTags;

import gregicality.GCYMInternalTags;
import gregicality.multiblocks.api.utils.GCYMLog;
import gregicality.multiblocks.common.CommonProxy;
import gregicality.multiblocks.common.block.GCYMMetaBlocks;
import gregicality.multiblocks.common.block.blocks.BlockSuperconductorCoil;
import gregicality.multiblocks.common.block.blocks.BlockTieredComponent;
import gregicality.multiblocks.common.metatileentities.GCYMMetaTileEntities;

@Mod(modid = GregicalityMultiblocks.MODID,
     name = GregicalityMultiblocks.NAME,
     version = GregicalityMultiblocks.VERSION,
     dependencies = GTInternalTags.DEP_VERSION_STRING)
public class GregicalityMultiblocks {

    public static final String MODID = "gcym";
    public static final String NAME = "Gregicality Multiblocks";
    public static final String VERSION = GCYMInternalTags.VERSION;

    @SidedProxy(modId = MODID,
                clientSide = "gregicality.multiblocks.common.ClientProxy",
                serverSide = "gregicality.multiblocks.common.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void onPreInit(@NotNull FMLPreInitializationEvent event) {
        GCYMLog.init(event.getModLog());

        GCYMMetaBlocks.init();
        GCYMMetaTileEntities.init();

        for (BlockSuperconductorCoil.CasingType type : BlockSuperconductorCoil.CasingType.values()) {
            SUPERCONDUCTOR_COILS.put(SUPERCONDUCTOR_COIL.getState(type), type);
        }
        for (BlockTieredComponent.CasingType type : BlockTieredComponent.CasingType.values()) {
            TIERED_COMPONENTS.put(TIERED_COMPONENT.getState(type), type);
        }

        proxy.preLoad();
    }
}
