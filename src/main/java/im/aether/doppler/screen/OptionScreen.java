package im.aether.doppler.screen;

//? >= 1.20.1 {
/*import net.minecraft.client.gui.GuiGraphics;
*///?}
import im.aether.doppler.Util;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
//? < 1.19.2 {
import net.minecraft.network.chat.TextComponent;
 //?}

//? <= 1.19.2 {
import com.mojang.blaze3d.vertex.PoseStack;
 //?}

public class OptionScreen extends Screen {

    private final Screen parent;

    public OptionScreen(Screen parent) {
        //? >= 1.19.2 {
        /*super(Component.literal("Doppler Setting"));
        *///?} else {
        super(new TextComponent("Doppler Setting"));
         //?}
        this.parent = parent;
    }

    @Override
    protected void init() {
        Component c;
        if (Util.EFFECT_SCALE < 0.1)
            c = /*? >=1.19.2 {*/ /*Component.literal("Disabled") *//*?} else {*/  new TextComponent("Disabled") /*?}*/;
        else
            c = /*? >=1.19.2 {*/ /*Component.literal(String.format("%.1f%%", Util.EFFECT_SCALE * 100)) *//*?} else {*/  new TextComponent(String.format("%.1f%%", Util.EFFECT_SCALE * 100)) /*?}*/;

        this.addRenderableWidget(new SliderButton(this.width / 2 - (160 / 2), this.height / 2 - 10, 160, 20, c, Util.EFFECT_SCALE));
    }

    //? >= 1.20.1 {
    /*@Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        /^? if forge {^/
        super.renderBackground(guiGraphics);
        /^?}^/
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 10, -1);

        guiGraphics.drawString(this.font, "Effect scale (0 to disable)", this.width / 2 - (160 / 2), this.height / 2 - 22, -1, true);
        guiGraphics.drawString(this.font, "Default is 40%", this.width / 2 - (160 / 2), this.height / 2 + 15, -1, true);
    }
    *///?} else {
    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(poseStack);
        super.render(poseStack, mouseX, mouseY, partialTick);
        drawCenteredString(poseStack, this.font, this.title, this.width / 2, 10, -1);

        drawString(poseStack, this.font, "Effect scale (0 to disable)", this.width / 2 - (160 / 2), this.height / 2 - 22, -1);
        drawString(poseStack, this.font, "Default is 40%", this.width / 2 - (160 / 2), this.height / 2 + 15, -1);
    }
    //?}

    @Override
    public void onClose() {
        if (this.minecraft == null) return; // to make ide shut up

        this.minecraft.setScreen(parent);

        Util.saveConfig();
    }

    static class SliderButton extends AbstractSliderButton {

        public SliderButton(int x, int y, int width, int height, Component message, double value) {
            super(x, y, width, height, message, value);
        }

        @Override
        protected void updateMessage() {
            if (value < 0.1)
                setMessage(/*? >=1.19.2 {*/ /*Component.literal("Disabled") *//*?} else {*/  new TextComponent("Disabled") /*?}*/);
            else
                setMessage(/*? >=1.19.2 {*/ /*Component.literal(String.format("%.1f%%", value * 100)) *//*?} else {*/  new TextComponent(String.format("%.1f%%", value * 100)) /*?}*/);
        }

        @Override
        protected void applyValue() {
            Util.EFFECT_SCALE = this.value;
        }
    }
}