package com.willfp.ecoenchants.proxy.v1_20_R3.registration

import com.willfp.eco.util.StringUtils
import com.willfp.ecoenchants.display.getFormattedName
import com.willfp.ecoenchants.enchant.EcoEnchant
import io.papermc.paper.enchantments.EnchantmentRarity
import net.kyori.adventure.text.Component
import net.minecraft.world.item.enchantment.Enchantment
import org.bukkit.craftbukkit.v1_20_R3.enchantments.CraftEnchantment
import org.bukkit.enchantments.EnchantmentTarget
import org.bukkit.entity.EntityCategory
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import java.util.Objects

class DelegatedCraftEnchantment(
    private val enchant: EcoEnchant,
    nmsEnchantment: Enchantment
) : CraftEnchantment(enchant.key, nmsEnchantment), EcoEnchant by enchant {
    override fun canEnchantItem(item: ItemStack): Boolean {
        return enchant.canEnchantItem(item)
    }

    override fun conflictsWith(other: org.bukkit.enchantments.Enchantment): Boolean {
        return enchant.conflictsWith(other)
    }

    override fun translationKey(): String {
        return "ecoenchants:enchantment.$id"
    }

    @Deprecated(
        message = "getName is a legacy Spigot API",
        replaceWith = ReplaceWith("this.displayName(level)")
    )
    override fun getName(): String = this.id.uppercase()

    override fun getMaxLevel(): Int {
        return maxLevel
    }

    override fun getStartLevel(): Int {
        return 1
    }

    @Deprecated(
        message = "getItemTargets is an incompatible Spigot API",
        replaceWith = ReplaceWith("this.targets")
    )
    override fun getItemTarget(): EnchantmentTarget = EnchantmentTarget.ALL

    @Deprecated(
        message = "Treasure enchantments do not exist in EcoEnchants",
        replaceWith = ReplaceWith("this.isEnchantable")
    )
    override fun isTreasure(): Boolean = !isEnchantable

    @Deprecated(
        message = "Use EnchantmentType instead",
        replaceWith = ReplaceWith("type.id")
    )
    override fun isCursed(): Boolean {
        return false
    }

    override fun displayName(level: Int): Component {
        return StringUtils.toComponent(enchant.getFormattedName(level))
    }

    override fun isTradeable(): Boolean {
        return isTradeable
    }

    override fun isDiscoverable(): Boolean {
        return isDiscoverable
    }

    override fun getMinModifiedCost(level: Int): Int {
        return Int.MAX_VALUE
    }

    override fun getMaxModifiedCost(level: Int): Int {
        return Int.MAX_VALUE
    }

    @Deprecated(
        message = "EcoEnchants uses a custom system for enchantment rarity",
        replaceWith = ReplaceWith("this.enchantRarity")
    )
    override fun getRarity(): EnchantmentRarity {
        return EnchantmentRarity.RARE
    }

    @Deprecated(
        message = "EcoEnchants do not have damage increase, this method is for sharpness/boa/smite",
        replaceWith = ReplaceWith("0.0f")
    )
    override fun getDamageIncrease(level: Int, entityCategory: EntityCategory): Float = 0.0f

    @Deprecated(
        message = "getActiveSlots is an incompatible Paper API",
        replaceWith = ReplaceWith("this.slots")
    )
    override fun getActiveSlots() = emptySet<EquipmentSlot>()

    override fun equals(other: Any?): Boolean {
        return other is DelegatedCraftEnchantment &&
                other.key == this.key
    }

    override fun hashCode(): Int {
        return Objects.hash(this.key)
    }
}
