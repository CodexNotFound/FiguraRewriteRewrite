package org.moon.figura.model.rendertasks;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import org.moon.figura.avatar.Badges;
import org.moon.figura.lua.LuaWhitelist;
import org.moon.figura.lua.docs.LuaMethodDoc;
import org.moon.figura.lua.docs.LuaMethodOverload;
import org.moon.figura.lua.docs.LuaMethodShadow;
import org.moon.figura.lua.docs.LuaTypeDoc;
import org.moon.figura.math.vector.FiguraVec3;
import org.moon.figura.model.PartCustomization;
import org.moon.figura.utils.ColorUtils;
import org.moon.figura.utils.LuaUtils;
import org.moon.figura.utils.TextUtils;
import org.moon.figura.utils.ui.UIHelper;

import java.util.List;

@LuaWhitelist
@LuaTypeDoc(
        name = "TextTask",
        value = "text_task"
)
public class TextTask extends RenderTask {

    private List<Component> text;
    private boolean centered = false;
    private boolean rtl = false;
    private boolean shadow = false;
    private boolean outline = false;
    private FiguraVec3 outlineColor;

    private int cachedComplexity;

    public TextTask(String name) {
        super(name);
    }

    @Override
    public boolean render(PartCustomization.Stack stack, MultiBufferSource buffer, int light, int overlay) {
        if (!enabled || text == null || text.size() == 0)
            return false;

        this.pushOntoStack(stack);
        PoseStack poseStack = stack.peek().copyIntoGlobalPoseStack();
        poseStack.scale(-1, -1, 1);

        Font font = Minecraft.getInstance().font;

        for (int i = 0; i < text.size(); i++) {
            Component text = this.text.get(i);
            int x = centered ? -font.width(text) / 2 : rtl ? -font.width(text) : 0;
            int y = i * font.lineHeight;

            if (outline) {
                UIHelper.renderOutlineText(poseStack, font, text, x, y, 0xFFFFFF, outlineColor == null ? 0 : ColorUtils.rgbToInt(outlineColor));
            } else {
                font.drawInBatch(text, x, y, 0xFFFFFF, shadow, poseStack.last().pose(), buffer, false, 0, this.light != null ? this.light : light);
            }
        }

        stack.pop();
        return true;
    }

    @Override
    public int getComplexity() {
        return cachedComplexity;
    }

    @LuaWhitelist
    @LuaMethodDoc(
            overloads = {
                    @LuaMethodOverload(
                            argumentTypes = String.class,
                            argumentNames = "text"
                    )
            },
            value = "text_task.set_text"
    )
    public void setText(String text) {
        this.text = text == null ? null : TextUtils.splitText(Badges.noBadges4U(TextUtils.tryParseJson(text)), "\n");
        if (text != null)
            this.cachedComplexity = text.length() + 1;
    }

    @LuaWhitelist
    @LuaMethodShadow("setText")
    public RenderTask text(String text) {
        setText(text);
        return this;
    }

    @LuaWhitelist
    @LuaMethodDoc("text_task.is_centered")
    public boolean isCentered() {
        return this.centered;
    }

    @LuaWhitelist
    @LuaMethodDoc(
            overloads = {
                    @LuaMethodOverload(
                            argumentTypes = Boolean.class,
                            argumentNames = "centered"
                    )
            },
            value = "text_task.set_centered"
    )
    public void setCentered(boolean centered) {
        this.centered = centered;
    }

    @LuaWhitelist
    @LuaMethodShadow("setCentered")
    public RenderTask centered(boolean centered) {
        setCentered(centered);
        return this;
    }

    @LuaWhitelist
    @LuaMethodDoc("text_task.is_right")
    public boolean isRight() {
        return this.rtl;
    }

    @LuaWhitelist
    @LuaMethodDoc(
            overloads = {
                    @LuaMethodOverload(
                            argumentTypes = Boolean.class,
                            argumentNames = "right"
                    )
            },
            value = "text_task.set_right"
    )
    public void setRight(boolean right) {
        this.rtl = right;
    }

    @LuaWhitelist
    @LuaMethodShadow("setRight")
    public RenderTask right(boolean right) {
        setRight(right);
        return this;
    }

    @LuaWhitelist
    @LuaMethodDoc("text_task.has_shadow")
    public boolean hasShadow() {
        return this.shadow;
    }

    @LuaWhitelist
    @LuaMethodDoc(
            overloads = {
                    @LuaMethodOverload(
                            argumentTypes = Boolean.class,
                            argumentNames = "shadow"
                    )
            },
            value = "text_task.set_shadow"
    )
    public void setShadow(boolean shadow) {
        this.shadow = shadow;
    }

    @LuaWhitelist
    @LuaMethodShadow("setShadow")
    public RenderTask shadow(boolean shadow) {
        setShadow(shadow);
        return this;
    }

    @LuaWhitelist
    @LuaMethodDoc("text_task.has_outline")
    public boolean hasOutline() {
        return this.outline;
    }

    @LuaWhitelist
    @LuaMethodDoc(
            overloads = {
                    @LuaMethodOverload(
                            argumentTypes = Boolean.class,
                            argumentNames = "outline"
                    )
            },
            value = "text_task.set_outline"
    )
    public void setOutline(boolean outline) {
        this.outline = outline;
    }

    @LuaWhitelist
    @LuaMethodShadow("setOutline")
    public RenderTask outline(boolean outline) {
        setOutline(outline);
        return this;
    }

    @LuaWhitelist
    @LuaMethodDoc("text_task.get_outline_color")
    public FiguraVec3 getOutlineColor() {
        return this.outlineColor;
    }

    @LuaWhitelist
    @LuaMethodDoc(
            overloads = {
                    @LuaMethodOverload(
                            argumentTypes = FiguraVec3.class,
                            argumentNames = "color"
                    ),
                    @LuaMethodOverload(
                            argumentTypes = {Double.class, Double.class, Double.class},
                            argumentNames = {"r", "g", "b"}
                    )
            },
            value = "text_task.set_outline_color"
    )
    public void setOutlineColor(Object x, Double y, Double z) {
        this.outlineColor = LuaUtils.parseVec3("setOutlineColor", x, y, z);
    }

    @LuaWhitelist
    @LuaMethodShadow("setOutlineColor")
    public TextTask outlineColor(Object x, Double y, Double z) {
        setOutlineColor(x, y, z);
        return this;
    }

    @Override
    public String toString() {
        return name + " (Text Render Task)";
    }
}
