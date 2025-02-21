package io.github.gonalez.znpcs.utility;

import io.github.gonalez.znpcs.ZNPConfigUtils;
import io.github.gonalez.znpcs.cache.CacheRegistry;
import io.github.gonalez.znpcs.configuration.ConfigConfiguration;
import io.github.gonalez.znpcs.user.ZUser;
import me.clip.placeholderapi.PlaceholderAPI;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Utils {
  public static final double BUKKIT_VERSION = extractAndSumNumbers(Bukkit.getBukkitVersion());
  public static final boolean PLACEHOLDER_SUPPORT = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");

  public Utils() {
  }

  public static double extractAndSumNumbers(String str) {
    Pattern pattern = Pattern.compile("(\\d+)");
    Matcher matcher = pattern.matcher(str);
    double[] numbers = new double[3];

    for(int count = 0; matcher.find() && count < 3; numbers[count++] = Double.parseDouble(matcher.group())) {
    }

    return numbers[1] + numbers[2] / Math.pow((double)10.0F, (double)String.valueOf((int)numbers[2]).length());
  }

  public static boolean isVersionNew(double version) {
    return BUKKIT_VERSION >= version;
  }

  public static String getBukkitPackage() {
    String packageName = Bukkit.getServer().getClass().getPackage().getName();
    String[] split = packageName.split("\\.");
    return split.length > 3 ? split[3] : "";
  }

  public static String toColor(String string) {
    return ChatColor.translateAlternateColorCodes('&', string);
  }

  public static String getWithPlaceholders(String string, Player player) {
    return PlaceholderAPI.setPlaceholders(player, string).replace(ConfigurationConstants.SPACE_SYMBOL, " ");
  }

  public static String randomString(int length) {
    StringBuilder stringBuilder = new StringBuilder();

    for(int index = 0; index < length; ++index) {
      stringBuilder.append(ThreadLocalRandom.current().nextInt(0, 9));
    }

    return stringBuilder.toString();
  }

  public static void sendTitle(Player player, String title, String subTitle) {
    player.sendTitle(toColor(title), toColor(subTitle));
  }

  public static void setValue(Object fieldInstance, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
    Field f = fieldInstance.getClass().getDeclaredField(fieldName);
    f.setAccessible(true);
    f.set(fieldInstance, value);
  }

  public static void setValue(Object fieldInstance, Object value, Class<?> expectedType) throws NoSuchFieldException, IllegalAccessException {
    for(Field field : fieldInstance.getClass().getDeclaredFields()) {
      if (field.getType() == expectedType) {
        setValue(fieldInstance, field.getName(), value);
      }
    }

  }

  public static Object getValue(Object instance, String fieldName) throws NoSuchFieldException, IllegalAccessException {
    Field f = instance.getClass().getDeclaredField(fieldName);
    f.setAccessible(true);
    return f.get(instance);
  }

  public static void sendPackets(ZUser user, Object... packets) {
    try {
      for(Object packet : packets) {
        if (packet != null) {
          ((Method)CacheRegistry.SEND_PACKET_METHOD.load()).invoke(user.getPlayerConnection(), packet);
        }
      }
    } catch (InvocationTargetException | IllegalAccessException e) {
      ((ReflectiveOperationException)e).printStackTrace();
    }

  }
}
