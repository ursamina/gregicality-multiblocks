package gregicality.multiblocks.common.metatileentities;

import static gregicality.multiblocks.api.utils.GCYMUtil.gcymId;
import static gregtech.common.metatileentities.MetaTileEntities.registerMetaTileEntity;

import gregtech.api.GTValues;
import gregtech.api.GregTechAPI;

import gregicality.multiblocks.common.metatileentities.multiblock.generator.MetaTileEntitySteamEngine;
import gregicality.multiblocks.common.metatileentities.multiblock.memory.MetaTileEntityAdvancedMemoryProvider;
import gregicality.multiblocks.common.metatileentities.multiblock.standard.*;
import gregicality.multiblocks.common.metatileentities.multiblockpart.MetaTileEntityTieredHatch;
import gregicality.multiblocks.common.metatileentities.multiblockpart.MetaTileEntityUpgradeHatch;

public final class GCYMMetaTileEntities {

    public static MetaTileEntityAlloyBlastSmelter ALLOY_BLAST_SMELTER;
    public static MetaTileEntityElectricImplosionCompressor ELECTRIC_IMPLOSION_COMPRESSOR;
    public static MetaTileEntityLargeArcFurnace LARGE_ARC_FURNACE;
    public static MetaTileEntityLargeAssembler LARGE_ASSEMBLER;
    public static MetaTileEntityLargeAutoclave LARGE_AUTOCLAVE;
    public static MetaTileEntityLargeBender LARGE_BENDER;
    public static MetaTileEntityLargeBrewery LARGE_BREWERY;
    public static MetaTileEntityLargeCanner LARGE_CANNER;
    public static MetaTileEntityLargeCentrifuge LARGE_CENTRIFUGE;
    public static MetaTileEntityLargeChemicalBath LARGE_CHEMICAL_BATH;
    public static MetaTileEntityLargeCircuitAssembler LARGE_CIRCUIT_ASSEMBLER;
    public static MetaTileEntityLargeCompressor LARGE_COMPRESSOR;
    public static MetaTileEntityLargeCutter LARGE_CUTTER;
    public static MetaTileEntityLargeDistillery LARGE_DISTILLERY;
    public static MetaTileEntityLargeElectrolyzer LARGE_ELECTROLYZER;
    public static MetaTileEntityLargeElectromagneticSeparator LARGE_ELECTROMAGNETIC_SEPARATOR;
    public static MetaTileEntityLargeEngraver LARGE_ENGRAVER;
    public static MetaTileEntityLargeExtractor LARGE_EXTRACTOR;
    public static MetaTileEntityLargeExtruder LARGE_EXTRUDER;
    public static MetaTileEntityLargeFermenter LARGE_FERMENTER;
    public static MetaTileEntityLargeFluidHeater LARGE_FLUID_HEATER;
    public static MetaTileEntityLargeForgeHammer LARGE_FORGE_HAMMER;
    public static MetaTileEntityLargeFormingPress LARGE_FORMING_PRESS;
    public static MetaTileEntityLargeMacerator LARGE_MACERATOR;
    public static MetaTileEntityLargeMixer LARGE_MIXER;
    public static MetaTileEntityLargeOreWasher LARGE_ORE_WASHER;
    public static MetaTileEntityLargePackager LARGE_PACKAGER;
    public static MetaTileEntityLargePolarizer LARGE_POLARIZER;
    public static MetaTileEntityLargeSifter LARGE_SIFTER;
    public static MetaTileEntityLargeSolidifier LARGE_SOLIDIFIER;
    public static MetaTileEntityLargeThermalCentrifuge LARGE_THERMAL_CENTRIFUGE;
    public static MetaTileEntityLargeWiremill LARGE_WIREMILL;

    public static MetaTileEntityMegaBlastFurnace MEGA_BLAST_FURNACE;
    public static MetaTileEntityMegaChemicalReactor MEGA_CHEMICAL_REACTOR;
    public static MetaTileEntityMegaFusionReactor MEGA_FUSION_REACTOR;
    public static MetaTileEntityMegaOilCracker MEGA_OIL_CRACKER;
    public static MetaTileEntityMegaPyrolyseOven MEGA_PYROLYSE_OVEN;
    public static MetaTileEntityMegaVacuumFreezer MEGA_VACUUM_FREEZER;

    public static MetaTileEntityAdvancedMemoryProvider ADVANCED_MEMORY_PROVIDER;

    public static MetaTileEntitySteamEngine STEAM_ENGINE;

    public static MetaTileEntityTieredHatch[] TIERED_HATCH = new MetaTileEntityTieredHatch[GTValues.V.length];
    public static MetaTileEntityUpgradeHatch[] UPGRADE_HATCH = new MetaTileEntityUpgradeHatch[4];

    private GCYMMetaTileEntities() {}

    public static void init() {
        // Multiblocks
        ALLOY_BLAST_SMELTER = registerMetaTileEntity(2000,
                new MetaTileEntityAlloyBlastSmelter(gcymId("alloy_blast_smelter")));
        ELECTRIC_IMPLOSION_COMPRESSOR = registerMetaTileEntity(2001,
                new MetaTileEntityElectricImplosionCompressor(gcymId("electric_implosion_compressor")));

        LARGE_ARC_FURNACE = registerMetaTileEntity(2002,
                new MetaTileEntityLargeArcFurnace(gcymId("large_arc_furnace")));
        LARGE_ASSEMBLER = registerMetaTileEntity(2003, new MetaTileEntityLargeAssembler(gcymId("large_assembler")));
        LARGE_AUTOCLAVE = registerMetaTileEntity(2004, new MetaTileEntityLargeAutoclave(gcymId("large_autoclave")));
        LARGE_BENDER = registerMetaTileEntity(2005, new MetaTileEntityLargeBender(gcymId("large_bender")));
        LARGE_BREWERY = registerMetaTileEntity(2006, new MetaTileEntityLargeBrewery(gcymId("large_brewery")));
        LARGE_CANNER = registerMetaTileEntity(2007, new MetaTileEntityLargeCanner(gcymId("large_canner")));
        LARGE_CENTRIFUGE = registerMetaTileEntity(2008, new MetaTileEntityLargeCentrifuge(gcymId("large_centrifuge")));
        LARGE_CHEMICAL_BATH = registerMetaTileEntity(2009,
                new MetaTileEntityLargeChemicalBath(gcymId("large_chemical_bath")));
        LARGE_CIRCUIT_ASSEMBLER = registerMetaTileEntity(2010,
                new MetaTileEntityLargeCircuitAssembler(gcymId("large_circuit_assembler")));
        LARGE_COMPRESSOR = registerMetaTileEntity(2011, new MetaTileEntityLargeCompressor(gcymId("large_compressor")));
        LARGE_CUTTER = registerMetaTileEntity(2012, new MetaTileEntityLargeCutter(gcymId("large_cutter")));
        LARGE_DISTILLERY = registerMetaTileEntity(2013, new MetaTileEntityLargeDistillery(gcymId("large_distillery")));
        LARGE_ELECTROLYZER = registerMetaTileEntity(2014,
                new MetaTileEntityLargeElectrolyzer(gcymId("large_electrolyzer")));
        LARGE_ELECTROMAGNETIC_SEPARATOR = registerMetaTileEntity(2015,
                new MetaTileEntityLargeElectromagneticSeparator(gcymId("large_electromagnetic_separator")));
        LARGE_ENGRAVER = registerMetaTileEntity(2016, new MetaTileEntityLargeEngraver(gcymId("large_engraver")));
        LARGE_EXTRACTOR = registerMetaTileEntity(2017, new MetaTileEntityLargeExtractor(gcymId("large_extractor")));
        LARGE_EXTRUDER = registerMetaTileEntity(2018, new MetaTileEntityLargeExtruder(gcymId("large_extruder")));
        LARGE_FERMENTER = registerMetaTileEntity(2019, new MetaTileEntityLargeFermenter(gcymId("large_fermenter")));
        LARGE_FLUID_HEATER = registerMetaTileEntity(2020,
                new MetaTileEntityLargeFluidHeater(gcymId("large_fluid_heater")));
        LARGE_FORGE_HAMMER = registerMetaTileEntity(2021,
                new MetaTileEntityLargeForgeHammer(gcymId("large_forge_hammer")));
        LARGE_FORMING_PRESS = registerMetaTileEntity(2022,
                new MetaTileEntityLargeFormingPress(gcymId("large_forming_press")));
        LARGE_MACERATOR = registerMetaTileEntity(2023, new MetaTileEntityLargeMacerator(gcymId("large_macerator")));
        LARGE_MIXER = registerMetaTileEntity(2024, new MetaTileEntityLargeMixer(gcymId("large_mixer")));
        LARGE_ORE_WASHER = registerMetaTileEntity(2025, new MetaTileEntityLargeOreWasher(gcymId("large_ore_washer")));
        LARGE_PACKAGER = registerMetaTileEntity(2026, new MetaTileEntityLargePackager(gcymId("large_packager")));
        LARGE_POLARIZER = registerMetaTileEntity(2027, new MetaTileEntityLargePolarizer(gcymId("large_polarizer")));
        LARGE_SIFTER = registerMetaTileEntity(2028, new MetaTileEntityLargeSifter(gcymId("large_sifter")));
        LARGE_SOLIDIFIER = registerMetaTileEntity(2029, new MetaTileEntityLargeSolidifier(gcymId("large_solidifier")));
        LARGE_THERMAL_CENTRIFUGE = registerMetaTileEntity(2030,
                new MetaTileEntityLargeThermalCentrifuge(gcymId("large_thermal_centrifuge")));
        LARGE_WIREMILL = registerMetaTileEntity(2031, new MetaTileEntityLargeWiremill(gcymId("large_wiremill")));

        MEGA_BLAST_FURNACE = registerMetaTileEntity(2032,
                new MetaTileEntityMegaBlastFurnace(gcymId("mega_blast_furnace")));
        MEGA_CHEMICAL_REACTOR = registerMetaTileEntity(2033,
                new MetaTileEntityMegaChemicalReactor(gcymId("mega_chemical_reactor")));
        MEGA_FUSION_REACTOR = registerMetaTileEntity(2034,
                new MetaTileEntityMegaFusionReactor(gcymId("mega_fusion_reactor")));
        MEGA_OIL_CRACKER = registerMetaTileEntity(2035, new MetaTileEntityMegaOilCracker(gcymId("mega_oil_cracker")));
        MEGA_PYROLYSE_OVEN = registerMetaTileEntity(2036,
                new MetaTileEntityMegaPyrolyseOven(gcymId("mega_pyrolyse_oven")));
        MEGA_VACUUM_FREEZER = registerMetaTileEntity(2037,
                new MetaTileEntityMegaVacuumFreezer(gcymId("mega_vacuum_freezer")));

        STEAM_ENGINE = registerMetaTileEntity(2038, new MetaTileEntitySteamEngine(gcymId("steam_engine")));

        ADVANCED_MEMORY_PROVIDER = registerMetaTileEntity(2039,
                new MetaTileEntityAdvancedMemoryProvider(gcymId("advanced_memory_provider")));

        for (int i = 0; i < TIERED_HATCH.length; i++) {
            if (!GregTechAPI.isHighTier() && i > GTValues.UHV)
                break;

            TIERED_HATCH[i] = registerMetaTileEntity(2040 + i,
                    new MetaTileEntityTieredHatch(gcymId(String.format("tiered_hatch.%s", GTValues.VN[i])), i));
        }

        for (int i = 0; i < UPGRADE_HATCH.length; i++) {

            int tier = GTValues.IV + i;

            UPGRADE_HATCH[i] = registerMetaTileEntity(2071 + i,
                    new MetaTileEntityUpgradeHatch(gcymId(String.format("upgrade_hatch.%s", GTValues.VN[tier])), tier));
        }
    }
}
