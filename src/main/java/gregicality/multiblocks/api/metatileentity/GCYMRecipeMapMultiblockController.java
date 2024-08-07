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
import gregtech.api.util.TextComponentUtil;
import gregtech.api.util.TextFormattingUtil;

import gregicality.multiblocks.api.capability.IUpgradeableMultiblock;
import gregicality.multiblocks.api.capability.impl.GCYMMultiblockRecipeLogic;
import gregicality.multiblocks.api.render.GCYMGuiTextures;
import gregicality.multiblocks.common.GCYMConfigHolder;

public abstract class GCYMRecipeMapMultiblockController extends MultiMapMultiblockController
                                                        implements IUpgradeableMultiblock {

    // TODO NBT. Do the NBT these settings wont persist between logins.

    public int currentMemory;

    private int scalar;
    private final int maxScalar;
    private int processingSpeed;
    private int euDiscount;
    private int parallelMultiplier;
    private boolean specialUpgrade;
    private boolean laserCompatible;
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

        this.scalar = 0;
        this.maxScalar = 5;
        this.processingSpeed = 100;
        this.euDiscount = 100;
        this.parallelMultiplier = 1;
        this.specialUpgrade = false;
        this.laserCompatible = false;

        this.memoryMultiplier = 1;
    }


    // todo parallel multipliers arent implemented yet
    @Override
    protected void formStructure(PatternMatchContext context) {
        super.formStructure(context);
        resetAllValues();
    }

    @Override
    public void invalidateStructure() {
        super.invalidateStructure();
        resetAllValues();
    }

    public int getMaxMemory() {
        return this.getAbilities(GCYMMultiblockAbility.UPGRADE_HATCH).isEmpty() ? 0 :
                this.getAbilities(GCYMMultiblockAbility.UPGRADE_HATCH).get(0).getMaxMemory();
    }

    public int getCurrentMemory() {
        return currentMemory;
    }

    public double getMemoryUsage() {
        return (double) getCurrentMemory() / getMaxMemory();
    }

    public int getCurrentScalar() {
        return scalar;
    }

    public int getCurrentSpeed() {
        return processingSpeed;
    }

    public int getCurrentDiscount() {
        return euDiscount;
    }

    public int getCurrentMultiplier() {
        return parallelMultiplier;
    }

    public boolean isSpecialUpgrade() {
        return specialUpgrade;
    }

    public boolean isLaserCompatible() {
        return laserCompatible;
    }

    public boolean hasMemoryCapacity() {
        return getMaxMemory() >= currentMemory;
    }

    public void setSpecialUpgrade(boolean specialUpgradeToggled) {
        if (!isSpecialUpgrade()) {
            specialUpgrade = specialUpgradeToggled;
            currentMemory = (currentMemory + 10 * getMemoryMultiplier()) * 2;
            memoryMultiplier = memoryMultiplier * 2;
        } else {
            specialUpgrade = false;
            memoryMultiplier = memoryMultiplier / 2;
            currentMemory = (currentMemory / 2) - 10 * getMemoryMultiplier();
        }
    }

    public int getMemoryMultiplier() {
        return memoryMultiplier;
    }

    public void setLaserCompatible(boolean laserUpgradeToggled) {
        if (laserUpgradeToggled) {
            laserCompatible = true;
            currentMemory = (currentMemory + 25 * getMemoryMultiplier()) * 3;
            memoryMultiplier = memoryMultiplier * 3;
        } else {
            laserCompatible = false;
            memoryMultiplier = memoryMultiplier / 3;
            currentMemory = (currentMemory / 3) - 25 * getMemoryMultiplier();
        }
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
                        if (getTotalEUtDiscount() != 1) {

                            ITextComponent energyDiscount = TextComponentUtil.stringWithColor(
                                    TextFormatting.AQUA,
                                    TextFormattingUtil.formatNumbers(getTotalEUtDiscount() * 100));

                            ITextComponent base = TextComponentUtil.translationWithColor(
                                    TextFormatting.GRAY,
                                    "Energy discount: %s%%",
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
                                    "gcym.multiblock.speed",
                                    speedBoost);
                            ITextComponent hoverText = TextComponentUtil.translationWithColor(
                                    TextFormatting.GRAY,
                                    "gcym.multiblock.speed_hover");
                            TextComponentUtil.setHover(base, hoverText);
                            tl.add(base);
                        }
                        if (getTotalParallel() > 1) {
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
                        if (isLaserCompatible()) {
                            ITextComponent parallels = TextComponentUtil.stringWithColor(
                                    TextFormatting.DARK_PURPLE,
                                    Boolean.toString(isLaserCompatible()));
                            ITextComponent bodyText = TextComponentUtil.translationWithColor(
                                    TextFormatting.GRAY,
                                    "Hi %s",
                                    parallels);
                            ITextComponent hoverText = TextComponentUtil.translationWithColor(
                                    TextFormatting.GRAY,
                                    "gcym.multiblock.parallel_hover");
                            tl.add(TextComponentUtil.setHover(bodyText, hoverText));

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
        // todo in the future, refactor so that this class is instanceof IControllable.
        IControllable controllable = getCapability(GregtechTileCapabilities.CAPABILITY_CONTROLLABLE, null);
        if (controllable != null) {
            builder.widget(new ImageCycleButtonWidget(173, 183, 18, 18, GuiTextures.BUTTON_POWER,
                    controllable::isWorkingEnabled, controllable::setWorkingEnabled));
            builder.widget(new ImageWidget(173, 201, 18, 6, GuiTextures.BUTTON_POWER_DETAIL));
        }

        // Widgets 'n' Buttons
        builder.widget(getUpgradeButtons(4, "scalar_parallel", GCYMGuiTextures.BUTTON_SCALAR_PARALLEL, "Scalar",
                this::decrementScalar, this::incrementScalar));

        builder.widget(getUpgradeButtons(23, "processing_speed", GCYMGuiTextures.BUTTON_PROCESSING_SPEED,
                "Processing Speed", this::decrementProcessingSpeed, this::incrementProcessingSpeed));

        builder.widget(getUpgradeButtons(42, "eu_discount", GCYMGuiTextures.BUTTON_EU_DISCOUNT, "EU Discount",
                this::decrementEuDiscount, this::incrementEuDiscount));

        builder.widget(getUpgradeButtons(61, "multiplier_parallel", GCYMGuiTextures.BUTTON_MULTIPLIER_PARALLEL,
                "Parallel Multiplier", this::decrementParallelMultiplier, this::incrementParallelMultiplier));

        builder.widget(new ImageCycleButtonWidget(156, 80, 18, 18, GCYMGuiTextures.BUTTON_SPECIAL_UPGRADE,
                () -> specialUpgrade,
                this::setSpecialUpgrade));
        //
        builder.widget(
                new ImageCycleButtonWidget(176, 80, 18, 18, GCYMGuiTextures.BUTTON_LASER_UPGRADE, () -> laserCompatible,
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
                this::getMemoryUsage,
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
                TextFormattingUtil.formatNumbers(getCurrentMemory()),
                TextFormattingUtil.formatNumbers(getMaxMemory()));
        textList.add(TextComponentUtil.translationWithColor(
                TextFormatting.GRAY,
                "Memory Usage: %s",
                TextComponentUtil.setColor(memoryInfo, TextFormatting.GOLD)));
    }

    protected @NotNull Widget getUpgradeButtons(int y, String upgradeType, TextureArea buttonTexture, String tooltip,
                                                Consumer<Widget.ClickData> decrementValue,
                                                Consumer<Widget.ClickData> incrementValue) {
        WidgetGroup group = new WidgetGroup(156, y, 38, 18);

        group.addWidget(new ClickButtonWidget(0, 0, 9, 18, "", decrementValue)
                .setButtonTexture(GuiTextures.BUTTON_THROTTLE_MINUS)
                .setTooltipText("gcym.machine.upgrade_hatch.decrement_" + upgradeType));

        group.addWidget(new ImageWidget(10, 0, 18, 18)
                .setImage(buttonTexture).setIgnoreColor(true)
                .setTooltip(tooltip));

        group.addWidget(new ClickButtonWidget(29, 0, 9, 18, "", incrementValue)
                .setButtonTexture(GuiTextures.BUTTON_THROTTLE_PLUS)
                .setTooltipText("gcym.machine.upgrade_hatch.increment_" + upgradeType));

        return group;
    }

    private void resetButton(Widget.ClickData clickData) {
        this.resetAllValues();
    }

    private void resetAllValues() {
        this.scalar = 0;
        this.processingSpeed = 100;
        this.euDiscount = 100;
        this.parallelMultiplier = 1;

        this.specialUpgrade = false;
        this.laserCompatible = false;

        this.currentMemory = 0;
        this.memoryMultiplier = 1;
    }

    private void decrementScalar(Widget.ClickData clickData) {
        if (getCurrentMemory() >= getMemoryMultiplier() && scalar > 0) {
            scalar = MathHelper.clamp(scalar - 1, 0, maxScalar);
            currentMemory = currentMemory - getMemoryMultiplier();
        }
    }

    private void incrementScalar(Widget.ClickData clickData) {
        if (getCurrentMemory() <= getMaxMemory() - getMemoryMultiplier() && getCurrentScalar() < maxScalar) {
            scalar = MathHelper.clamp(scalar + 1, 0, maxScalar);
            currentMemory = currentMemory + getMemoryMultiplier();
        }
    }

    private void decrementProcessingSpeed(Widget.ClickData clickData) {
        if (getCurrentMemory() >= 2 * getMemoryMultiplier() && (getCurrentSpeed()) > 100) {
            processingSpeed = MathHelper.clamp(processingSpeed - 10, 100, 200);
            currentMemory = currentMemory - 2 * getMemoryMultiplier();

        }
    }

    private void incrementProcessingSpeed(Widget.ClickData clickData) {
        if (getCurrentMemory() <= getMaxMemory() - 2 * getMemoryMultiplier() &&
                getCurrentSpeed() < 200) {
            processingSpeed = MathHelper.clamp(processingSpeed + 10, 100, 200);
            currentMemory = currentMemory + 2 * getMemoryMultiplier();
        }
    }

    private void decrementEuDiscount(Widget.ClickData clickData) {
        if (getCurrentMemory() >= 3 * getMemoryMultiplier() && getCurrentDiscount() < 100) {
            euDiscount = MathHelper.clamp(euDiscount + 5, 75, 100);
            currentMemory = currentMemory - 3 * getMemoryMultiplier();
        }
    }

    private void incrementEuDiscount(Widget.ClickData clickData) {
        if (getCurrentMemory() <= getMaxMemory() - 3 * getMemoryMultiplier() &&
                getCurrentDiscount() > 75) {
            euDiscount = MathHelper.clamp(euDiscount - 5, 75, 100);
            currentMemory = currentMemory + 3 * getMemoryMultiplier();
        }
    }

    private void decrementParallelMultiplier(Widget.ClickData clickData) {
        if (getCurrentMemory() >= 10 * getMemoryMultiplier() && parallelMultiplier > 1) {
            parallelMultiplier = MathHelper.clamp(parallelMultiplier - 1, 1, 3);
            currentMemory = currentMemory - 10 * getMemoryMultiplier();
        }
    }

    private void incrementParallelMultiplier(Widget.ClickData clickData) {
        if (getCurrentMemory() <= getMaxMemory() - 10 * getMemoryMultiplier() && getCurrentMultiplier() < 3) {
            parallelMultiplier = MathHelper.clamp(parallelMultiplier + 1, 1, 3);
            currentMemory = currentMemory + 10 * getMemoryMultiplier();
        }
    }

    @Override
    public boolean isUpgradeable() {
        return true;
    }

    @Override
    public boolean isLaserHatchUpgrade() {
        return this.isLaserCompatible();
    }

    @Override
    public int getTotalParallel() {
        long maxVoltage = Math.max(energyContainer.getInputVoltage(), energyContainer.getOutputVoltage());
        return GTUtility.getTierByVoltage(maxVoltage) * this.getParallelScalar();
    }

    public int getParallelScalar() {
        return this.getCurrentScalar() + parallelScalar;
    }

    public void setParallelScalar(int amount) {
        parallelScalar = amount;
    }

    @Override
    public double getTotalEUtDiscount() {
        if (inherentEUtDiscount == 0) {
            return (double) this.getCurrentDiscount() * 0.01;
        } else {
            return (this.getCurrentDiscount() +
                    inherentEUtDiscount) * 0.01;
        }
    }

    @Override
    public double getUpgradeSpeedBonus() {
        if (inherentSpeedBonus == 0) {
            return (double) this.getCurrentSpeed() * 0.01;
        } else {
            return (this.getCurrentSpeed() +
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

        predicate = predicate
                .or((isLaserHatchUpgrade() ? abilities(MultiblockAbility.INPUT_ENERGY, MultiblockAbility.INPUT_LASER) :
                        abilities(MultiblockAbility.INPUT_ENERGY))
                                .setMinGlobalLimited(1)
                                .setMaxGlobalLimited(1)
                                .setPreviewCount(1));

        return predicate;
    }

    public static @NotNull TraceabilityPredicate tieredCasing() {
        return new TraceabilityPredicate(abilities(GCYMMultiblockAbility.TIERED_HATCH)
                .setMinGlobalLimited(GCYMConfigHolder.globalMultiblocks.enableTieredCasings ? 1 : 0)
                .setMaxGlobalLimited(1));
    }



    @Override
    public NBTTagCompound writeToNBT(@NotNull NBTTagCompound data) {
        data.setInteger("scalar", this.scalar);
        data.setInteger("processingSpeed", this.processingSpeed);
        data.setInteger("euDiscount", this.euDiscount);
        data.setInteger("parallelMultiplier", this.parallelMultiplier);
        data.setBoolean("specialUpgrade", this.specialUpgrade);
        data.setBoolean("laserCompatible", this.laserCompatible);
        data.setInteger("memoryMultiplier", this.memoryMultiplier);
        return super.writeToNBT(data);
    }

    @Override
    public void readFromNBT(NBTTagCompound data) {
        super.readFromNBT(data);
        this.scalar = data.getInteger("scalar");
        this.processingSpeed = data.getInteger("processingSpeed");
        this.euDiscount = data.getInteger("euDiscount");
        this.parallelMultiplier = data.getInteger("parallelMultiplier");
        this.specialUpgrade = data.getBoolean("specialUpgrade");
        this.laserCompatible = data.getBoolean("laserCompatible");
        this.memoryMultiplier = data.getInteger("memoryMultiplier");
    }

    @Override
    public void writeInitialSyncData(PacketBuffer buf) {
        super.writeInitialSyncData(buf);


        buf.writeInt(this.scalar);
        buf.writeInt(this.processingSpeed);
        buf.writeInt(this.euDiscount);
        buf.writeInt(this.parallelMultiplier);
        buf.writeBoolean(this.specialUpgrade);
        buf.writeBoolean(this.laserCompatible);
        buf.writeInt(this.memoryMultiplier);
    }

    @Override
    public void receiveInitialSyncData(PacketBuffer buf) {
        super.receiveInitialSyncData(buf);
        this.scalar = buf.readInt();
        this.processingSpeed = buf.readInt();
        this.euDiscount = buf.readInt();
        this.parallelMultiplier = buf.readInt();
        this.specialUpgrade = buf.readBoolean();
        this.laserCompatible = buf.readBoolean();
        this.memoryMultiplier = buf.readInt();
    }
}
