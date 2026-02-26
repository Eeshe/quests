package me.eeshe.quests.util;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public final class ItemBuilder {
  private Material material = Material.AIR;
  private int amount = 1;
  private String displayName;
  private List<String> lore;
  private boolean unbreakable;
  private Integer customModelData;
  private final List<ItemFlag> flags = new ArrayList<>();
  private final Map<Enchantment, Integer> enchantments = new HashMap<>();
  private final Multimap<Attribute, AttributeModifier> attributeModifiers =
      ArrayListMultimap.create();
  private String skullOwner;

  public static ItemBuilder builder() {
    return new ItemBuilder();
  }

  public ItemBuilder material(Material material) {
    this.material = material;
    return this;
  }

  public ItemBuilder amount(int amount) {
    this.amount = amount;
    return this;
  }

  public ItemBuilder displayName(String displayName) {
    this.displayName = displayName;
    return this;
  }

  public ItemBuilder lore(List<String> lore) {
    this.lore = lore;
    return this;
  }

  public ItemBuilder unbreakable(boolean unbreakable) {
    this.unbreakable = unbreakable;
    return this;
  }

  public ItemBuilder customModelData(int customModelData) {
    this.customModelData = customModelData;
    return this;
  }

  public ItemBuilder addFlag(ItemFlag flag) {
    this.flags.add(flag);
    return this;
  }

  public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
    this.enchantments.put(enchantment, level);
    return this;
  }

  public ItemBuilder addAttributeModifier(Attribute attribute, AttributeModifier modifier) {
    this.attributeModifiers.put(attribute, modifier);
    return this;
  }

  public ItemBuilder skullOwner(String skullOwner) {
    this.skullOwner = skullOwner;
    return this;
  }

  public ItemStack build() {
    ItemStack item = new ItemStack(material, amount);
    ItemMeta meta = item.getItemMeta();
    if (meta == null) {
      return item;
    }
    if (displayName != null) {
      meta.setDisplayName(displayName);
    }
    if (lore != null) {
      meta.setLore(lore);
    }
    if (unbreakable) {
      meta.setUnbreakable(true);
    }
    if (customModelData != null) {
      meta.setCustomModelData(customModelData);
    }
    if (!flags.isEmpty()) {
      meta.addItemFlags(flags.toArray(new ItemFlag[0]));
    }
    if (!attributeModifiers.isEmpty()) {
      meta.setAttributeModifiers(attributeModifiers);
    }
    if (skullOwner != null && meta instanceof SkullMeta) {
      SkullMeta skullMeta = (SkullMeta) meta;
      skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(skullOwner));
    }
    item.setItemMeta(meta);
    if (!enchantments.isEmpty()) {
      item.addEnchantments(enchantments);
    }
    return item;
  }
}
