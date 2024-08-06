package gregicality.multiblocks.api.render;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.relauncher.Side;

import gregtech.api.gui.resources.TextureArea;

import gregicality.multiblocks.GregicalityMultiblocks;

@Mod.EventBusSubscriber(modid = GregicalityMultiblocks.MODID, value = Side.CLIENT)

public class GCYMGuiTextures {

    public static final TextureArea BUTTON_SCALAR_PARALLEL = TextureArea
            .fullImage("textures/gui/widget/button_scalar_parallel.png");
    public static final TextureArea BUTTON_PROCESSING_SPEED = TextureArea
            .fullImage("textures/gui/widget/button_processing_speed.png");
    public static final TextureArea BUTTON_EU_DISCOUNT = TextureArea
            .fullImage("textures/gui/widget/button_eu_discount.png");
    public static final TextureArea BUTTON_MULTIPLIER_PARALLEL = TextureArea
            .fullImage("textures/gui/widget/button_multiplier_parallel.png");

    public static final TextureArea BUTTON_SPECIAL_UPGRADE = TextureArea
            .fullImage("textures/gui/widget/button_special_upgrade.png");
    public static final TextureArea BUTTON_LASER_UPGRADE = TextureArea
            .fullImage("textures/gui/widget/button_laser_upgrade.png");
    public static final TextureArea BUTTON_RESET_UPGRADES = TextureArea
            .fullImage("textures/gui/widget/button_reset_upgrades.png");
}
