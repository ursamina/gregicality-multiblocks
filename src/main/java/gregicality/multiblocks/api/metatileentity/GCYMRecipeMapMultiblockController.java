package gregicality.multiblocks.api.metatileentity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
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
import gregtech.api.gui.Widget;
import gregtech.api.gui.resources.TextureArea;
import gregtech.api.gui.widgets.*;
import gregtech.api.metatileentity.multiblock.MultiMapMultiblockController;
import gregtech.api.metatileentity.multiblock.MultiblockAbility;
import gregtech.api.metatileentity.multiblock.MultiblockDisplayText;
import gregtech.api.metatileentity.multiblock.MultiblockWithDisplayBase;
import gregtech.api.pattern.PatternMatchContext;
import gregtech.api.pattern.TraceabilityPredicate;
import gregtech.api.recipes.RecipeMap;
import gregtech.api.util.GTUtility;
import gregtech.api.util.LocalizationUtils;
import gregtech.api.util.TextComponentUtil;
import gregtech.api.util.TextFormattingUtil;

import gregicality.multiblocks.api.capability.IUpgradeableMultiblock;
import gregicality.multiblocks.api.capability.impl.GCYMMultiblockRecipeLogic;
import gregicality.multiblocks.api.render.GCYMGuiTextures;
import gregicality.multiblocks.api.upgrade.Upgrade;
import gregicality.multiblocks.common.GCYMConfigHolder;

public abstract class GCYMRecipeMapMultiblockController extends MultiMapMultiblockController
                                                        implements IUpgradeableMultiblock {

    public int currentMemory;
    private final Upgrade scalar = new Upgrade(0, 0, 5, 1, 1);
    private final Upgrade processingSpeed = new Upgrade(0, 0, 50, 5, 2);
    private final Upgrade euDiscount = new Upgrade(0, 0, 50, 5, 3);
    private final Upgrade parallelMultiplier = new Upgrade(1, 1, 5, 1, 10);
    private final Upgrade specialUpgrade = new Upgrade(0, 0, 1, 1, 20);
    private final Upgrade laserCompatible = new Upgrade(0, 0, 1, 1, 30);

    private int memoryMultiplier;
    protected int parallelScalar;
    protected int inherentEUtDiscount;
    protected int inherentSpeedBonus;

    public GCYMRecipeMapMultiblockController(ResourceLocation metaTileEntityId, RecipeMap<?> recipeMap) {
        this(metaTileEntityId, new RecipeMap<?>[] { recipeMap });
    }

    public GCYMRecipeMapMultiblockController(ResourceLocation metaTileEntityId, RecipeMap<?>[] recipeMaps) {
        super(metaTileEntityId, recipeMaps);

        this.recipeMapWorkable = new GCYMMultiblockRecipeLogic(this);
        this.memoryMultiplier = 1;
    }

    // todo parallel multipliers arent implemented yet
    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        calculateMemoryUsage();

        if (laserCompatible.currentValue != 1 && !this.getAbilities(MultiblockAbility.INPUT_LASER).isEmpty()) {
            this.invalidateStructure();
        }
    }

    public int getMaxMemory() {
        return this.getAbilities(GCYMMultiblockAbility.UPGRADE_HATCH).isEmpty() ? 0 :
                this.getAbilities(GCYMMultiblockAbility.UPGRADE_HATCH).get(0).getMaxMemory();
    }

    public double getMemoryUsageProgressBar() {
        if (currentMemory >= getMaxMemory()) {
            return 1;
        } else return (double) currentMemory / getMaxMemory();
    }

    public boolean hasMemoryCapacity() {
        return getMaxMemory() >= currentMemory;
    }

    public int calculateMemoryUsage() {
        int flatMemoryUsage = scalar.getMemoryConsumption() + processingSpeed.getMemoryConsumption() +
                euDiscount.getMemoryConsumption() + parallelMultiplier.getMemoryConsumption() +
                specialUpgrade.getMemoryConsumption() + laserCompatible.getMemoryConsumption();

        return flatMemoryUsage * memoryMultiplier;
    }

    public void toggleSpecialUpgrade(boolean specialUpgradeToggled) {
        if (specialUpgradeToggled) {

            switch (specialUpgrade.currentValue) {
                case 0 -> {
                    specialUpgrade.setCurrentValue(1);
                    setMemoryMultiplier();
                }
                case 1 -> {
                    specialUpgrade.setCurrentValue(0);
                    setMemoryMultiplier();
                }
            }
        } else {
            specialUpgrade.setCurrentValue(0);
            setMemoryMultiplier();
        }
        setMemoryUsage();
    }

    public boolean getSpecialUpgrade() {
        return specialUpgrade.isEnabled();

    }

    public void setMemoryMultiplier() {
        int memoryMultiplier = ((specialUpgrade.currentValue + 1)) * (3 * (laserCompatible.currentValue));
        // ((0 + 1) *

        if (memoryMultiplier == 0) this.memoryMultiplier = ((specialUpgrade.currentValue + 1));

        else this.memoryMultiplier = memoryMultiplier;
    }

    public void setLaserCompatible(boolean laserUpgradeToggled) {
        if (laserUpgradeToggled) {

            switch (laserCompatible.currentValue) {
                case 0 -> {
                    laserCompatible.setCurrentValue(1);
                    setMemoryMultiplier();
                }
                case 1 -> {
                    laserCompatible.setCurrentValue(0);
                    setMemoryMultiplier();
                    this.invalidateStructure();
                }
            }
        } else {
            laserCompatible.setCurrentValue(0);
            setMemoryMultiplier();
            this.invalidateStructure();
        }

        setMemoryUsage();
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        if (getParallelScalar() != 0) {
            tooltip.add(
                    I18n.format("gcym.tooltip.max_parallel", TextFormattingUtil.formatNumbers(parallelScalar)));
        }
        if (inherentEUtDiscount != 0) {
            tooltip.add(
                    I18n.format("gcym.tooltip.energy_discount",
                            TextFormattingUtil.formatNumbers(inherentEUtDiscount)));
        }
        if (inherentSpeedBonus != 0) {
            tooltip.add(
                    I18n.format("gcym.tooltip.speed_boost",
                            TextFormattingUtil.formatNumbers(100 / inherentSpeedBonus)));
        }
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

    protected TraceabilityPredicate getHatchPredicates() {
        // preview could be revised
        return abilities(MultiblockAbility.INPUT_ENERGY).setPreviewCount(1)
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


                        if (laserCompatible.currentValue == 1) {
                            ITextComponent bodyText1 = TextComponentUtil.translationWithColor(
                                    TextFormatting.GRAY,
                                    "Laser Hatches: Enabled");
                            ITextComponent hoverText1 = TextComponentUtil.translationWithColor(
                                    TextFormatting.GRAY,
                                    "gcym.multiblock.parallel_hover");
                            tl.add(TextComponentUtil.setHover(bodyText1, hoverText1));

                        }

                        if (specialUpgrade.currentValue == 1) {
                            ITextComponent bodyText2 = TextComponentUtil.translationWithColor(
                                    TextFormatting.GRAY,
                                    "Special Upgrade: Enabled"
                                    );
                            ITextComponent hoverText2 = TextComponentUtil.translationWithColor(
                                    TextFormatting.GRAY,
                                    "gcym.multiblock.parallel_hover");
                            tl.add(TextComponentUtil.setHover(bodyText2, hoverText2));

                        }
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
        builder.widget(getUpgradeButtons(4, "scalar_parallel", GCYMGuiTextures.BUTTON_SCALAR_PARALLEL, "Scalar",
                s -> this.modifyValue(s, scalar, false), s -> this.modifyValue(s, scalar, true),
                scalar));

        builder.widget(getUpgradeButtons(23, "processing_speed", GCYMGuiTextures.BUTTON_PROCESSING_SPEED,
                "Processing Speed", s -> this.modifyValue(s, processingSpeed, false),
                s -> this.modifyValue(s, processingSpeed, true), processingSpeed));

        builder.widget(getUpgradeButtons(42, "eu_discount", GCYMGuiTextures.BUTTON_EU_DISCOUNT, "EU Discount",
                s -> this.modifyValue(s, euDiscount, false), s -> this.modifyValue(s, euDiscount, true), euDiscount));

        builder.widget(getUpgradeButtons(61, "multiplier_parallel", GCYMGuiTextures.BUTTON_MULTIPLIER_PARALLEL,
                "Parallel Multiplier", s -> this.modifyValue(s, parallelMultiplier, false),
                s -> this.modifyValue(s, parallelMultiplier, true), parallelMultiplier));

        builder.widget(new ImageCycleButtonWidget(156, 80, 18, 18, GCYMGuiTextures.BUTTON_SPECIAL_UPGRADE,
                specialUpgrade::isEnabled,
                this::toggleSpecialUpgrade));
        //
        builder.widget(
                new ImageCycleButtonWidget(176, 80, 18, 18, GCYMGuiTextures.BUTTON_LASER_UPGRADE,
                        laserCompatible::isEnabled,
                        this::setLaserCompatible));

        builder.widget(new ClickButtonWidget(156, 99, 38, 11, "", this::resetButton)
                .setButtonTexture(GCYMGuiTextures.BUTTON_RESET_UPGRADES)
                .setTooltipText("gcym.machine.upgrade_hatch.decrement_"));

        // // Text
        builder.label(9, 9, title, 0xFFFFFF);
        builder.widget(new AdvancedTextWidget(9, 20, this::addDisplayText, 0xFFFFFF)
                .setMaxWidthLimit(181)
                .setClickHandler(this::handleDisplayClick));

        // Desperate attempt at a progress bar
        builder.widget(new ProgressWidget(
                this::getMemoryUsageProgressBar,
                4, 113, 189, 7,
                this.getProgressBarTexture(), ProgressWidget.MoveType.HORIZONTAL)
                        .setHoverTextConsumer(this::addBarHoverText));

        return builder;
    }

    private TextureArea getProgressBarTexture() {
        return GuiTextures.PROGRESS_BAR_MULTI_ENERGY_YELLOW;
    }

    private void addBarHoverText(List<ITextComponent> textList) {
        ITextComponent memoryInfo = new TextComponentTranslation("%s / %s GB",
                TextFormattingUtil.formatNumbers(currentMemory),
                TextFormattingUtil.formatNumbers(getMaxMemory()));
        textList.add(TextComponentUtil.translationWithColor(
                TextFormatting.GRAY,
                "Memory Usage: %s",
                TextComponentUtil.setColor(memoryInfo, TextFormatting.GOLD)));
    }

    protected @NotNull Widget getUpgradeButtons(int y, String upgradeType, TextureArea buttonTexture, String tooltip,
                                                Consumer<Widget.ClickData> decrementValue,
                                                Consumer<Widget.ClickData> incrementValue, Upgrade upgrade) {
        WidgetGroup group = new WidgetGroup(156, y, 38, 18);

        group.addWidget(new ClickButtonWidget(0, 0, 9, 18, "", decrementValue)
                .setButtonTexture(GuiTextures.BUTTON_THROTTLE_MINUS)
                .setTooltipText("gcym.machine.upgrade_hatch.decrement_" + upgradeType));

        group.addWidget(new ImageWidget(10, 0, 18, 18)

                .setImage(buttonTexture).setIgnoreColor(true)
                .setTooltip(LocalizationUtils.format("Using %s / %s upgrade points", upgrade.getAllocatedPoints(), upgrade.getMaxPoints()))
                );

        group.addWidget(new ClickButtonWidget(29, 0, 9, 18, "", incrementValue)
                .setButtonTexture(GuiTextures.BUTTON_THROTTLE_PLUS)
                .setTooltipText("gcym.machine.upgrade_hatch.increment_" + upgradeType));

        return group;
    }

    private void resetButton(Widget.ClickData clickData) {
        resetAllValues();
        this.invalidateStructure();
    }

    private void resetAllValues() {
        scalar.setCurrentValue(0);
        processingSpeed.setCurrentValue(0);
        euDiscount.setCurrentValue(0);
        parallelMultiplier.setCurrentValue(1);

        laserCompatible.setCurrentValue(0);
        specialUpgrade.setCurrentValue(0);

        this.memoryMultiplier = 1;

        setMemoryUsage();
    }

    // todo Suppress the abnormality
    private void modifyValue(Widget.ClickData clickData, Upgrade upgrade, boolean isIncrease) {
        if (isIncrease) {
            if (upgrade.upgradeCost * memoryMultiplier < getMaxMemory() - calculateMemoryUsage()) {
                upgrade.setCurrentValue(
                        MathHelper.clamp(upgrade.currentValue + upgrade.incrementValue, upgrade.getMin(),
                                upgrade.getMax()));
            }
        } else {
            if (currentMemory >= upgrade.upgradeCost * memoryMultiplier) {
                upgrade.setCurrentValue(
                        MathHelper.clamp(upgrade.currentValue - upgrade.incrementValue, upgrade.getMin(),
                                upgrade.getMax()));
            }
        }
        setMemoryUsage();
    }

    @Override
    public boolean isUpgradeable() {
        return true;
    }

    @Override
    public int getTotalParallel() {
        long maxVoltage = Math.max(energyContainer.getInputVoltage(), energyContainer.getOutputVoltage());
        return GTUtility.getTierByVoltage(maxVoltage) * this.getParallelScalar() * parallelMultiplier.getCurrentValue();
    }

    public int getParallelScalar() {
        return scalar.currentValue + parallelScalar;
    }

    public void setParallelScalar(int amount) {
        parallelScalar = amount;
    }

    public void setMemoryUsage() {
        currentMemory = calculateMemoryUsage();
    }

    @Override
    public double getTotalEUtDiscount() {
        if (inherentEUtDiscount == 0) {
            return (double) euDiscount.currentValue * 0.01;
        } else {
            return (euDiscount.currentValue +
                    inherentEUtDiscount) * 0.01;
        }
    }

    @Override
    public double getUpgradeSpeedBonus() {
        if (inherentSpeedBonus == 0) {
            return (double) (100 - processingSpeed.currentValue) * 0.01;
        } else {
            return (100 - processingSpeed.currentValue +
                    inherentSpeedBonus) * 0.01;
        }
    }

    public void setInherentEUtDiscount(int amount) {
        inherentEUtDiscount = amount;
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
        data.setInteger("scalar", scalar.getCurrentValue());
        data.setInteger("processingSpeed", processingSpeed.getCurrentValue());
        data.setInteger("euDiscount", euDiscount.getCurrentValue());
        data.setInteger("parallelMultiplier", parallelMultiplier.getCurrentValue());
        data.setInteger("specialUpgrade", specialUpgrade.getCurrentValue());
        data.setInteger("laserCompatible", laserCompatible.getCurrentValue());

        data.setInteger("memoryMultiplier", this.memoryMultiplier);

        data.setInteger("currentMemory", this.currentMemory);
        return super.writeToNBT(data);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        scalar.setCurrentValue(data.getInteger("scalar"));
        processingSpeed.setCurrentValue(data.getInteger("processingSpeed"));
        euDiscount.setCurrentValue(data.getInteger("euDiscount"));
        parallelMultiplier.setCurrentValue(data.getInteger("parallelMultiplier"));
        laserCompatible.setCurrentValue(data.getInteger("specialUpgrade"));
        parallelMultiplier.setCurrentValue(data.getInteger("laserCompatible"));

        this.memoryMultiplier = data.getInteger("memoryMultiplier");
        this.currentMemory = data.getInteger("currentMemory");
    }

    @Override
    public void writeInitialSyncData(PacketBuffer buf) {
        super.writeInitialSyncData(buf);

        buf.writeInt(scalar.getCurrentValue());
        buf.writeInt(processingSpeed.getCurrentValue());
        buf.writeInt(euDiscount.getCurrentValue());
        buf.writeInt(parallelMultiplier.getCurrentValue());
        buf.writeInt(specialUpgrade.getCurrentValue());
        buf.writeInt(laserCompatible.getCurrentValue());

        buf.writeInt(this.memoryMultiplier);
        buf.writeInt(this.currentMemory);
    }

    @Override
    public void receiveInitialSyncData(PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        scalar.setCurrentValue(buf.readInt());
        processingSpeed.setCurrentValue(buf.readInt());
        euDiscount.setCurrentValue(buf.readInt());
        parallelMultiplier.setCurrentValue(buf.readInt());
        specialUpgrade.setCurrentValue(buf.readInt());
        laserCompatible.setCurrentValue(buf.readInt());

        this.memoryMultiplier = buf.readInt();
        this.currentMemory = buf.readInt();
    }
}
