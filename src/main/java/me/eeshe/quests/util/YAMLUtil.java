package me.eeshe.quests.util;

import com.google.common.collect.Multimap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class YAMLUtil {

  public static void writeItemStack(
      final FileConfiguration configuration, final String key, final ItemStack item) {
    if (configuration.contains(key)) {
      return;
    }

    configuration.set(key + ".amount", item.getAmount());
    configuration.set(key + ".material", item.getType().name());

    final ItemMeta meta = item.getItemMeta();
    if (meta == null) {
      return;
    }

    if (meta.hasDisplayName()) {
      configuration.set(key + ".display-name", meta.getDisplayName());
    }
    if (meta.hasLore()) {
      configuration.set(key + ".lore", meta.getLore());
    }
    if (meta.isUnbreakable()) {
      configuration.set(key + ".unbreakable", true);
    }
    if (meta.hasCustomModelData()) {
      configuration.set(key + ".custom-model-data", meta.getCustomModelData());
    }
    final Set<ItemFlag> flags = meta.getItemFlags();
    System.out.println("FLAGS: " + flags.size());
    if (!flags.isEmpty()) {
      final List<String> flagNames = flags.stream().map(ItemFlag::name).toList();
      configuration.set(key + ".flags", flagNames);
    }
    final Map<Enchantment, Integer> enchantments = meta.getEnchants();
    if (!enchantments.isEmpty()) {
      final Map<String, Integer> yamlEnchantments = new HashMap<>();
      for (final Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
        final String enchantmentKey = entry.getKey().getKey().toString();
        final Integer enchantmentLevel = entry.getValue();

        yamlEnchantments.put(enchantmentKey, enchantmentLevel);
      }
      configuration.set(key + ".enchantments", yamlEnchantments);
    }
    final Multimap<Attribute, AttributeModifier> attributeModifiers = meta.getAttributeModifiers();
    if (attributeModifiers != null) {
      final Map<String, Map<String, Object>> attributeMap = new HashMap<>();
      for (final Map.Entry<Attribute, AttributeModifier> attributeEntry :
          attributeModifiers.entries()) {
        final Attribute attribute = attributeEntry.getKey();
        final AttributeModifier modifier = attributeEntry.getValue();
        final Map<String, Object> modifierMap = new HashMap<>();

        modifierMap.put("key", modifier.getKey().toString());
        modifierMap.put("name", modifier.getName());
        modifierMap.put("amount", modifier.getAmount());
        modifierMap.put("operation", modifier.getOperation().name());
        modifierMap.put("slot", modifier.getSlotGroup().toString());

        attributeMap.put(attribute.getKey().toString(), modifierMap);
      }
      configuration.set(key + ".attributes", attributeMap);
    }
    if (meta instanceof SkullMeta) {
      final SkullMeta skullMeta = (SkullMeta) meta;
      if (skullMeta.hasOwner()) {
        configuration.set(key + ".skull-owner", skullMeta.getOwningPlayer().getName());
      }
    }
  }

  public static ItemStack readItemStack(final FileConfiguration config, final String key) {
    if (!config.contains(key)) {
      return null;
    }
    final int amount = config.getInt(key + ".amount", 1);
    final Material material = Material.matchMaterial(config.getString(key + ".material", "AIR"));
    final ItemBuilder builder = ItemBuilder.builder().material(material).amount(amount);
    if (config.contains(key + ".display-name")) {
      builder.displayName(config.getString(key + ".display-name"));
    }
    if (config.contains(key + ".lore")) {
      builder.lore(config.getStringList(key + ".lore"));
    }
    if (config.getBoolean(key + ".unbreakable", false)) {
      builder.unbreakable(true);
    }
    if (config.contains(key + ".custom-model-data")) {
      builder.customModelData(config.getInt(key + ".custom-model-data"));
    }
    if (config.contains(key + ".flags")) {
      for (final String flagName : config.getStringList(key + ".flags")) {
        final ItemFlag flag = ItemFlag.valueOf(flagName);
        builder.addFlag(flag);
      }
    }
    if (config.contains(key + ".enchantments")) {
      final Map<String, Object> enchantmentsMap =
          config.getConfigurationSection(key + ".enchantments").getValues(false);
      for (final Entry<String, Object> entry : enchantmentsMap.entrySet()) {
        final Enchantment enchantment =
            Registry.ENCHANTMENT.get(NamespacedKey.fromString(entry.getKey()));
        if (enchantment == null) {
          LogUtil.warning(
              "Unknown enchantment '" + entry.getKey() + "' configured in '" + key + "'.");
          continue;
        }
        try {
          final int level = Integer.parseInt(entry.getValue().toString());

          builder.addEnchantment(enchantment, level);
        } catch (final Exception e) {
          LogUtil.warning(
              "Invalid enchantment level '" + entry.getValue() + "' configured in '" + key + "'.");
        }
      }
    }
    final ConfigurationSection attributeSection =
        config.getConfigurationSection(key + ".attributes");
    if (attributeSection != null) {
      for (final String attributeName : attributeSection.getKeys(false)) {
        final NamespacedKey attributeKey = NamespacedKey.fromString(attributeName);
        final Attribute attribute = Registry.ATTRIBUTE.get(attributeKey);
        if (attribute == null) {
          LogUtil.warning("Unknown attribute '" + attributeName + "' configured in '" + key + "'.");
          continue;
        }
        final ConfigurationSection modifierSection =
            attributeSection.getConfigurationSection(attributeName);
        if (modifierSection == null) {
          LogUtil.warning(
              "Invalid config for attribute '" + attributeName + "' configured in '" + key + "'.");
          continue;
        }
        final NamespacedKey modifierKey =
            NamespacedKey.fromString(modifierSection.getString("key"));
        final double modifierAmount = modifierSection.getDouble("amount");

        final String operationString = modifierSection.getString("operation");
        final Operation operation;
        try {
          operation = Operation.valueOf(operationString);
        } catch (final Exception e) {
          LogUtil.warning(
              "Invalid operation '" + operationString + "' configured in '" + key + "'.");
          continue;
        }
        final String slotString = modifierSection.getString("slot");
        final EquipmentSlotGroup slot;
        try {
          slot = EquipmentSlotGroup.getByName(slotString);
        } catch (final Exception e) {
          LogUtil.warning("Invalid slot '" + slotString + "' configured in '" + key + "'.");
          continue;
        }
        final AttributeModifier attributeModifier =
            new AttributeModifier(modifierKey, modifierAmount, operation, slot);

        builder.addAttributeModifier(attribute, attributeModifier);
      }
    }
    if (config.contains(key + ".skull-owner")) {
      builder.skullOwner(config.getString(key + ".skull-owner"));
    }
    return builder.build();
  }
}
