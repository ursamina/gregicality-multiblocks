package gregicality.multiblocks.loaders.recipe;

import static gregicality.multiblocks.api.unification.GCYMMaterials.*;
import static gregtech.api.GTValues.*;
import static gregtech.api.unification.material.Materials.*;
import static gregtech.api.unification.ore.OrePrefix.*;

import gregtech.api.recipes.ModHandler;
import gregtech.api.unification.material.MarkerMaterials;
import gregtech.api.unification.stack.UnificationEntry;
import gregtech.common.metatileentities.MetaTileEntities;

import gregicality.multiblocks.common.metatileentities.GCYMMetaTileEntities;

public final class GCYMMetaTileEntityLoader {

    private GCYMMetaTileEntityLoader() {}

    public static void init() {
        ModHandler.addShapedRecipe(true, "alloy_blast_smelter", GCYMMetaTileEntities.ALLOY_BLAST_SMELTER.getStackForm(),
                "TCT", "WSW", "TCT",
                'T', new UnificationEntry(plate, TantalumCarbide),
                'C', new UnificationEntry(circuit, MarkerMaterials.Tier.EV),
                'S', MetaTileEntities.ALLOY_SMELTER[EV].getStackForm(),
                'W', new UnificationEntry(cableGtSingle, Aluminium));
    }
}
