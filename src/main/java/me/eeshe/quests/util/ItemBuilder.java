package me.eeshe.quests.util;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
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

  public ItemBuilder() {}

  public ItemBuilder(ItemStack item) {
    if (item == null) {
      return;
    }
    material(item.getType());
    amount(item.getAmount());

    final ItemMeta meta = item.getItemMeta();
    if (meta.hasDisplayName()) {
      displayName(meta.getDisplayName());
    }
    if (meta.hasLore()) {
      lore(meta.getLore());
    }
    if (meta.isUnbreakable()) {
      unbreakable(meta.isUnbreakable());
    }
    if (meta.hasCustomModelData()) {
      customModelData(meta.getCustomModelData());
    }
    if (!meta.getItemFlags().isEmpty()) {
      for (ItemFlag itemFlag : meta.getItemFlags()) {
        addFlag(itemFlag);
      }
    }
    if (!item.getEnchantments().isEmpty()) {
      for (Entry<Enchantment, Integer> entry : item.getEnchantments().entrySet()) {
        addEnchantment(entry.getKey(), entry.getValue());
      }
    }
    if (meta.hasAttributeModifiers()) {
      for (Entry<Attribute, AttributeModifier> entry : meta.getAttributeModifiers().entries()) {
        addAttributeModifier(entry.getKey(), entry.getValue());
      }
    }
    if (meta instanceof SkullMeta skullMeta) {
      skullOwner(skullMeta.getOwner());
    }
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

  public ItemBuilder addLore(List<String> lore) {
    if (this.lore == null) {
      this.lore = new ArrayList<>();
    }
    this.lore.addAll(lore);
    return this;
  }

  public ItemBuilder addLore(String lore) {
    if (this.lore == null) {
      this.lore = new ArrayList<>();
    }
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
    if (flag == ItemFlag.HIDE_ATTRIBUTES) {
      // HIDE_ATTRIBUTES requires the item to have at least one attribute modifier
      addAttributeModifier(
          Attribute.LUCK,
          new AttributeModifier(NamespacedKey.minecraft("luck"), 0.1, Operation.ADD_NUMBER));
    }
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
      meta.setDisplayName(StringUtil.formatColor(displayName));
    }
    if (lore != null) {
      lore.replaceAll(StringUtil::formatColor);
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
