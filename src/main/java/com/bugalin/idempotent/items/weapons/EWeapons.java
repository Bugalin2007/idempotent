package com.bugalin.idempotent.items.weapons;

import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

public enum EWeapons {
    OAK_SWORD(Material.WOODEN_SWORD,"Oak Sword",new String[0],3,Rarity.COMMON),
    REFINED_DIAMOND_SWORD(Material.DIAMOND_SWORD,"Refined Diamond Sword",new String[]{"A blade made of diamonds","Refined to enhance its strengths"},10,Rarity.RARE),
    BOBS_MYTHIC_SWORD(Material.GOLDEN_SWORD,"Bob's Mythic Sword",new String[]{"A sword said to be forged by Bob himself","Tainted with corrupted tissues","It is now emitting ominous lights"},50,Rarity.LEGENDARY),
    ;

    private final JavaPlugin plugin = JavaPlugin.getPlugin(JavaPlugin.class);
    private final NamespacedKey damageKey = new NamespacedKey(plugin, "damage");
    private final NamespacedKey rarityKey = new NamespacedKey(plugin, "rarity");

    private EWeapons(Material base, String name, String[] lore, int damage, Rarity rarity) {
        this.base = base;
        this.name = name;
        this.lore = lore;
        this.damage = damage;
        this.rarity = rarity;
    }

    private Material base;
    private String name;
    private String[] lore;
    private int damage;
    private Rarity rarity;
    public Material getBase() {return base;}
    public String getName() { return name; }
    public int getDamage() { return damage; }
    public String[] getLore() {return lore;}
    public Rarity getRarity() {return rarity;}

    public ItemStack getItemStack() {
        ItemLore.Builder loreObj = ItemLore.lore();
        for (String loreLine : lore) {
            loreObj.addLine(MiniMessage.miniMessage().deserialize(loreLine));
        }
        loreObj.addLine(Component.empty());
        loreObj.addLine(MiniMessage.miniMessage().deserialize(rarity.name().toLowerCase()+" item"));

        ItemStack itemStack = ItemStack.of(base);
        itemStack.setData(DataComponentTypes.CUSTOM_NAME, MiniMessage.miniMessage().deserialize(rarity.colorLabel+name));
        itemStack.setData(DataComponentTypes.LORE, loreObj.build());
        itemStack.editPersistentDataContainer(pdc ->{
            pdc.set(damageKey, PersistentDataType.INTEGER, damage);
            pdc.set(rarityKey, PersistentDataType.STRING, rarity.name().toLowerCase());
        });

        return itemStack;
    }

    private enum Rarity {
        COMMON("<white>"),
        UNCOMMON("<yellow>"),
        RARE("<magenta>"),
        LEGENDARY("<light_blue>"),
        MYTHIC("<purple>");

        Rarity(String colorLabel) {
            this.colorLabel = colorLabel;
        }
        private final String colorLabel;

    }
}
