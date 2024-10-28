package io.github.willmerkz.team2PVP.utils;

import io.github.willmerkz.team2PVP.Team2PVP;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class ChatUtil {

    public static Component color(String old) {
        return MiniMessage.miniMessage().deserialize(Team2PVP.instance.getConfig().getString("prefix") + old);
    }

}
