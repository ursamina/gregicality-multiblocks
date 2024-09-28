package gregicality.multiblocks.api.metatileentity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import gregtech.api.capability.GregtechTileCapabilities;
import gregtech.api.capability.IControllable;
import gregtech.api.capability.IEnergyContainer;
import gregtech.api.capability.impl.EnergyContainerList;
import gregtech.api.gui.GuiTextures;
import gregtech.api.gui.ModularUI;
import gregtech.api.gui.widgets.AdvancedTextWidget;
import gregtech.api.gui.widgets.ImageCycleButtonWidget;
import gregtech.api.gui.widgets.ImageWidget;
import gregtech.api.gui.widgets.IndicatorImageWidget;
import gregtech.api.metatileentity.multiblock.MultiMapMultiblockController;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.MultiblockDisplayText;
import gregtech.api.metatileentity.multiblock.MultiblockWithDisplayBase;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.pattern.TraceabilityPredicate;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.util.GTUtility;
import gregtech.api.util.TextComponentUtil;
import gregtech.api.util.TextFormattingUtil;

import gregicality.multiblocks.api.capability.IUpgradeableMultiblock;
import gregicality.multiblocks.api.capability.impl.GCYMMultiblockRecipeLogic;
import gregicality.multiblocks.common.GCYMConfigHolder;

public abstract class GCYMRecipeMapMultiblockController extends MultiMapMultiblockController
                                                        implements IUpgradeableMultiblock {

    public GCYMRecipeMapMultiblockController(ResourceLocation metaTileEntityId, RecipeMap<?> recipeMap) {
        this(metaTileEntityId, new RecipeMap<?>[] { recipeMap });
    }

    public GCYMRecipeMapMultiblockController(ResourceLocation metaTileEntityId, RecipeMap<?>[] recipeMaps) {
        super(metaTileEntityId, recipeMaps);

        this.recipeMapWorkable = new GCYMMultiblockRecipeLogic(this);
    }

    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
    }


    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);

        if (GCYMConfigHolder.globalMultiblocks.enableTieredCasings && isTiered())
            tooltip.add(I18n.format("gcym.tooltip.tiered_hatch_enabled"));
    }

    @Override
    protected void initializeAbilities() {
        super.initializeAbilities();
        List<IEnergyContainer> inputEnergy = new ArrayList<>(getAbilities(MultiblockAbility.INPUT_ENERGY));
        inputEnergy.addAll(getAbilities(MultiblockAbility.INPUT_LASER));
        this.energyContainer = new EnergyContainerList(inputEnergy);
    }

    protected TraceabilityPredicate getHatchPredicates(boolean isSingleHatch) {
        // preview could be revised
        return abilities(MultiblockAbility.INPUT_ENERGY).setPreviewCount(1).setMaxGlobalLimited(isSingleHatch ? 1 : 2)
                .or(abilities(MultiblockAbility.INPUT_LASER).setPreviewCount(1));
    }

    @Override
    protected void addDisplayText(List<ITextComponent> textList) {
        MultiblockDisplayText.builder(textList, isStructureFormed())
                .setWorkingStatus(recipeMapWorkable.isWorkingEnabled(), recipeMapWorkable.isActive())
                .addEnergyUsageLine(getEnergyContainer())
                .addEnergyTierLine(GTUtility.getTierByVoltage(recipeMapWorkable.getMaxVoltage()))
                .addCustom(tl -> {
                    if (isStructureFormed()) {
                        // Energy Discount
                        if (getTotalEUtDiscount() != 0) {

                            ITextComponent energyDiscount = TextComponentUtil.stringWithColor(
                                    TextFormatting.AQUA,
                                    TextFormattingUtil.formatNumbers(getTotalEUtDiscount() * 100));

                            ITextComponent base = TextComponentUtil.translationWithColor(
                                    TextFormatting.GRAY,
                                    "EU/t Discount: %s%%",
                                    energyDiscount);

                            ITextComponent hoverText = TextComponentUtil.translationWithColor(
                                    TextFormatting.GRAY,
                                    "gcym.multiblock.energy_discount_hover");

                            TextComponentUtil.setHover(base, hoverText);

                            tl.add(base);
                        }
                        // Processing Speed
                        if (getUpgradeSpeedBonus() != 1) {
                            ITextComponent speedBoost = TextComponentUtil.stringWithColor(
                                    TextFormatting.AQUA,
                                    TextFormattingUtil.formatNumbers(100.0 * getUpgradeSpeedBonus()) + "%");
                            ITextComponent base = TextComponentUtil.translationWithColor(
                                    TextFormatting.GRAY,
                                    "Recipe Duration: %s",
                                    speedBoost);
                            ITextComponent hoverText = TextComponentUtil.translationWithColor(
                                    TextFormatting.GRAY,
                                    "gcym.multiblock.speed_hover");
                            TextComponentUtil.setHover(base, hoverText);
                            tl.add(base);
                        }
                        ITextComponent parallels = TextComponentUtil.stringWithColor(
                                TextFormatting.DARK_PURPLE,
                                TextFormattingUtil.formatNumbers(
                                        getTotalParallel()));
                        ITextComponent bodyText = TextComponentUtil.translationWithColor(
                                TextFormatting.GRAY,
                                "gcym.multiblock.parallel",
                                parallels);
                        ITextComponent hoverText = TextComponentUtil.translationWithColor(
                                TextFormatting.GRAY,
                                "gcym.multiblock.parallel_hover");
                        tl.add(TextComponentUtil.setHover(bodyText, hoverText));

                    }
                })
                .addWorkingStatusLine()
                .addProgressLine(recipeMapWorkable.getProgressPercent());
    }

    @Override
    protected ModularUI createUI(@NotNull EntityPlayer entityPlayer) {
        return createUpgradeHatchUI(getMetaFullName(), entityPlayer).build(getHolder(), entityPlayer);
    }

    public ModularUI.Builder createUpgradeHatchUI(String title, EntityPlayer entityPlayer) {
        ModularUI.Builder builder = ModularUI.builder(GuiTextures.BACKGROUND, 198, 206);

        // Background shit
        builder.image(4, 4, 151, 106, GuiTextures.DISPLAY);

        builder.widget(new IndicatorImageWidget(135, 90, 17, 17, getLogo())
                .setWarningStatus(getWarningLogo(), this::addWarningText)
                .setErrorStatus(getErrorLogo(), this::addErrorText));

        // Inventory
        builder.bindPlayerInventory(entityPlayer.inventory, 123);

        builder.widget(new ImageCycleButtonWidget(173, 143, 18, 18, GuiTextures.BUTTON_DISTINCT_BUSES,
                this::isDistinct, this::setDistinct)
                        .setTooltipHoverString(i -> "gregtech.multiblock.universal.distinct_" +
                                (i == 0 ? "disabled" : "enabled")));

        if (shouldShowVoidingModeButton()) {
            builder.widget(new ImageCycleButtonWidget(173, 161, 18, 18, GuiTextures.BUTTON_VOID_MULTIBLOCK,
                    4, this::getVoidingMode, this::setVoidingMode)
                            .setTooltipHoverString(MultiblockWithDisplayBase::getVoidingModeTooltip));
        } else {
            builder.widget(new ImageWidget(173, 161, 18, 18, GuiTextures.BUTTON_VOID_NONE)
                    .setTooltip("gregtech.gui.multiblock_voiding_not_supported"));
        }

        builder.widget(getFlexButton(173, 125, 18, 18));

        // Power Button
        IControllable controllable = getCapability(GregtechTileCapabilities.CAPABILITY_CONTROLLABLE, null);
        if (controllable != null) {
            builder.widget(new ImageCycleButtonWidget(173, 183, 18, 18, GuiTextures.BUTTON_POWER,
                    controllable::isWorkingEnabled, controllable::setWorkingEnabled));
            builder.widget(new ImageWidget(173, 201, 18, 6, GuiTextures.BUTTON_POWER_DETAIL));
        }

        // Widgets 'n' Buttons

        // // Text
        builder.label(9, 9, title, 0xFFFFFF);
        builder.widget(new AdvancedTextWidget(9, 20, this::addDisplayText, 0xFFFFFF)
                .setMaxWidthLimit(181)
                .setClickHandler(this::handleDisplayClick));

        return builder;
    }

    @Override
    public boolean isUpgradeable() {
        return true;
    }

    public boolean isTiered() {
        return GCYMConfigHolder.globalMultiblocks.enableTieredCasings;
    }

    @Override
    public TraceabilityPredicate autoAbilities(boolean checkEnergyIn, boolean checkMaintenance, boolean checkItemIn,
                                               boolean checkItemOut, boolean checkFluidIn, boolean checkFluidOut,
                                               boolean checkMuffler) {
        TraceabilityPredicate predicate = super.autoAbilities(checkEnergyIn, checkMaintenance, checkItemIn,
                checkItemOut, checkFluidIn, checkFluidOut, checkMuffler);
        if (isUpgradeable())
            predicate = predicate
                    .or(abilities(GCYMMultiblockAbility.UPGRADE_HATCH).setMaxGlobalLimited(1).setPreviewCount(1));

        return predicate;
    }

    public static @NotNull TraceabilityPredicate tieredCasing() {
        return new TraceabilityPredicate(abilities(GCYMMultiblockAbility.TIERED_HATCH)
                .setMinGlobalLimited(GCYMConfigHolder.globalMultiblocks.enableTieredCasings ? 1 : 0)
                .setMaxGlobalLimited(1));
    }

    @Override
    public NBTTagCompound writeToNBT(@NotNull NBTTagCompound data) {
        return super.writeToNBT(data);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
    }

    @Override
    public void writeInitialSyncData(PacketBuffer buf) {
        super.writeInitialSyncData(buf);
    }

    @Override
    public void receiveInitialSyncData(PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
    }
}
