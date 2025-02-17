package org.moon.figura.lua.api.entity;

import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.level.GameType;
import org.luaj.vm2.LuaError;
import org.moon.figura.lua.LuaNotNil;
import org.moon.figura.lua.LuaWhitelist;
import org.moon.figura.lua.docs.LuaMethodDoc;
import org.moon.figura.lua.docs.LuaMethodOverload;
import org.moon.figura.lua.docs.LuaTypeDoc;
import org.moon.figura.utils.EntityUtils;

@LuaWhitelist
@LuaTypeDoc(
        name = "PlayerAPI",
        value = "player"
)
public class PlayerAPI extends LivingEntityAPI<Player> {

    private PlayerInfo playerInfo;

    public PlayerAPI(Player entity) {
        super(entity);
    }

    private boolean checkPlayerInfo() {
        if (playerInfo != null)
            return true;

        PlayerInfo info = EntityUtils.getPlayerInfo(entity.getUUID());
        if (info == null)
            return false;

        playerInfo = info;
        return true;
    }

    @LuaWhitelist
    @LuaMethodDoc("player.get_food")
    public int getFood() {
        checkEntity();
        return entity.getFoodData().getFoodLevel();
    }

    @LuaWhitelist
    @LuaMethodDoc("player.get_saturation")
    public float getSaturation() {
        checkEntity();
        return entity.getFoodData().getSaturationLevel();
    }

    @LuaWhitelist
    @LuaMethodDoc("player.get_experience_progress")
    public float getExperienceProgress() {
        checkEntity();
        return entity.experienceProgress;
    }

    @LuaWhitelist
    @LuaMethodDoc("player.get_experience_level")
    public float getExperienceLevel() {
        checkEntity();
        return entity.experienceLevel;
    }

    @LuaWhitelist
    @LuaMethodDoc("player.is_flying")
    public boolean isFlying() {
        checkEntity();
        return entity.getAbilities().flying;
    }

    @LuaWhitelist
    @LuaMethodDoc("player.get_model_type")
    public String getModelType() {
        checkEntity();
        return checkPlayerInfo() ? playerInfo.getModelName().toUpperCase() : DefaultPlayerSkin.getSkinModelName(entity.getUUID());
    }

    @LuaWhitelist
    @LuaMethodDoc("player.get_gamemode")
    public String getGamemode() {
        checkEntity();
        if (!checkPlayerInfo())
            return null;

        GameType gamemode = playerInfo.getGameMode();
        return gamemode == null ? null : gamemode.getName().toUpperCase();
    }

    @LuaWhitelist
    @LuaMethodDoc("player.has_cape")
    public boolean hasCape() {
        checkEntity();
        return checkPlayerInfo() && playerInfo.isCapeLoaded();
    }

    @LuaWhitelist
    @LuaMethodDoc("player.has_skin")
    public boolean hasSkin() {
        checkEntity();
        return checkPlayerInfo() && playerInfo.isSkinLoaded();
    }

    @LuaWhitelist
    @LuaMethodDoc(
            overloads = @LuaMethodOverload(
                    argumentTypes = String.class,
                    argumentNames = "part"
            ),
            value = "player.is_skin_layer_visible"
    )
    public boolean isSkinLayerVisible(@LuaNotNil String part) {
        checkEntity();
        try {
            if (part.equalsIgnoreCase("left_pants") || part.equalsIgnoreCase("right_pants"))
                part += "_leg";
            return entity.isModelPartShown(PlayerModelPart.valueOf(part.toUpperCase()));
        } catch (Exception ignored) {
            throw new LuaError("Invalid player model part: " + part);
        }
    }

    @LuaWhitelist
    @LuaMethodDoc("player.is_fishing")
    public boolean isFishing() {
        checkEntity();
        return entity.fishing != null;
    }

    @LuaWhitelist
    @LuaMethodDoc("player.get_charged_attack_delay")
    public float getChargedAttackDelay() {
        checkEntity();
        return entity.getCurrentItemAttackStrengthDelay();
    }

    @Override
    public String toString() {
        checkEntity();
        return entity.getName().getString() + " (Player)";
    }
}
