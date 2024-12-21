package gregicality.multiblocks.api.metatileentity;

import java.util.List;

import gregtech.api.GTValues;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.multiblock.MultiMapMultiblockController;
import gregtech.api.metatileentity.multiblock.MultiblockDisplayText;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.util.GTUtility;
import gregtech.api.util.TextComponentUtil;
import gregtech.api.util.TextFormattingUtil;
import gregtech.client.utils.TooltipHelper;

import gregicality.multiblocks.api.capability.impl.GCYMMultiblockRecipeLogic;
import gregicality.multiblocks.common.block.blocks.BlockSuperconductorCoil;
import gregicality.multiblocks.common.block.blocks.BlockTieredComponent;

public abstract class GCYMMultiblockController extends MultiMapMultiblockController {

    protected int superconductorTier;
    protected int componentTier;

    public GCYMMultiblockController(ResourceLocation metaTileEntityId, RecipeMap<?> recipeMap) {
        this(metaTileEntityId, new RecipeMap<?>[] { recipeMap });
    }

    public GCYMMultiblockController(ResourceLocation metaTileEntityId, RecipeMap<?>[] recipeMaps) {
        super(metaTileEntityId, (recipeMaps));
        this.recipeMapWorkable = new GCYMMultiblockRecipeLogic(this);
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);

        Object type = context.get("SuperconductorTier");
        if (type instanceof BlockSuperconductorCoil.CasingType tier)
            this.superconductorTier = tier.ordinal() + 1;
        else this.superconductorTier = -1;

        Object type2 = context.get("ComponentTier");
        if (type2 instanceof BlockTieredComponent.CasingType tier)
            this.componentTier = tier.ordinal() + 1;
        else this.componentTier = -1;

    }

    @Override
    public void invalidateStructure() {
        super.invalidateStructure();
        this.superconductorTier = -1;
        this.componentTier = -1;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, boolean advanced) {
        MetaTileEntity metaTileEntity = GTUtility.getMetaTileEntity(stack);

        super.addInformation(stack, player, tooltip, advanced);

        if (TooltipHelper.isCtrlDown()) {
            tooltip.add(I18n.format("[§4Tier 1§7] %s", I18n.format(metaTileEntity.getMetaName() + ".tier.1")));
            tooltip.add(I18n.format("[§cTier 2§7] %s", I18n.format(metaTileEntity.getMetaName() + ".tier.2")));
            tooltip.add(I18n.format("[§6Tier 3§7] %s", I18n.format(metaTileEntity.getMetaName() + ".tier.3")));
            tooltip.add(I18n.format("[§eTier 4§7] %s", I18n.format(metaTileEntity.getMetaName() + ".tier.4")));
            tooltip.add(I18n.format("[§aTier 5§7] %s", I18n.format(metaTileEntity.getMetaName() + ".tier.5")));
        } else {
            tooltip.add(I18n.format("Hold CTRL to show Tier Upgrades"));
        }
    }

    @Override
    protected void addDisplayText(List<ITextComponent> textList) {
        MultiblockDisplayText.builder(textList, isStructureFormed())
                .setWorkingStatus(recipeMapWorkable.isWorkingEnabled(), recipeMapWorkable.isActive())
                .addEnergyUsageLine(getEnergyContainer())
                .addEnergyTierLine(GTUtility.getTierByVoltage(recipeMapWorkable.getMaxVoltage()))
                .addCustom(tl -> {
                    if (isStructureFormed()) {
                        ITextComponent componentString = TextComponentUtil.stringWithColor(
                                TextFormatting.RED,
                                TextFormattingUtil.formatNumbers(componentTier));
                        tl.add(TextComponentUtil.translationWithColor(
                                TextFormatting.GRAY,
                                "Component Tier: %s",
                                componentString));
                    }
                })
                .addCustom(tl -> {
                    if (isStructureFormed()) {
                        ITextComponent voltageName = new TextComponentString(GTValues.VNF[superconductorTier]);

                        ITextComponent superconductorString = TextComponentUtil.stringWithColor(
                                TextFormatting.GOLD,"T" +
                                        TextFormattingUtil.formatNumbers(superconductorTier));

                        tl.add(TextComponentUtil.translationWithColor(
                                TextFormatting.GRAY,
                                "Superconductor Tier: %s (%s)",
                                 voltageName, superconductorString));
                    }
                })
                .addParallelsLine(recipeMapWorkable.getParallelLimit())
                .addWorkingStatusLine()
                .addProgressLine(recipeMapWorkable.getProgressPercent());
    }

    public int getComponentTier() {
        return this.componentTier;
    }
}
