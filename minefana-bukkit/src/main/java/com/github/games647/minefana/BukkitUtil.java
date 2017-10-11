package com.github.games647.minefana;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.entity.Player;

public class BukkitUtil {

    private static Method getHandleMethod;
    private static Field pingField;

    public static int getReflectionPing(Player player) {
        try {
            if (getHandleMethod == null) {
                getHandleMethod = player.getClass().getDeclaredMethod("getHandle");
                //disable java security check. This will speed it a little
                getHandleMethod.setAccessible(true);
            }

            Object entityPlayer = getHandleMethod.invoke(player);

            if (pingField == null) {
                pingField = entityPlayer.getClass().getDeclaredField("ping");
                //disable java security check. This will speed it a little
                pingField.setAccessible(true);
            }

            //returns the found int value
            return pingField.getInt(entityPlayer);
        } catch (IllegalAccessException | NoSuchFieldException | NoSuchMethodException | InvocationTargetException ex) {
            //Forward the exception to replaceManager
            throw new RuntimeException(ex);
        }
    }
}
