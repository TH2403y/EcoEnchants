package com.willfp.ecoenchants.proxy.v1_20_R3

import com.willfp.ecoenchants.enchant.EcoEnchant
import com.willfp.ecoenchants.enchant.EcoEnchants
import com.willfp.ecoenchants.enchant.registration.modern.ModernEnchantmentRegistererProxy
import com.willfp.ecoenchants.proxy.v1_20_R3.registration.DelegatedCraftEnchantment
import com.willfp.ecoenchants.proxy.v1_20_R3.registration.ModifiedVanillaCraftEnchantment
import com.willfp.ecoenchants.proxy.v1_20_R3.registration.VanillaEcoEnchantsEnchantment
import net.minecraft.core.MappedRegistry
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_20_R3.CraftRegistry
import org.bukkit.craftbukkit.v1_20_R3.CraftServer
import org.bukkit.craftbukkit.v1_20_R3.enchantments.CraftEnchantment
import org.bukkit.craftbukkit.v1_20_R3.util.CraftNamespacedKey
import org.bukkit.enchantments.Enchantment

class ModernEnchantmentRegisterer : ModernEnchantmentRegistererProxy {
    private val frozenField = MappedRegistry::class.java
        .declaredFields
        .filter { it.type.isPrimitive }[0]
        .apply { isAccessible = true }

    @Suppress("UNCHECKED_CAST")
    private val registries = CraftServer::class.java
        .getDeclaredField("registries")
        .apply { isAccessible = true }
        .get(Bukkit.getServer())
            as HashMap<Class<*>, org.bukkit.Registry<*>>

    override fun replaceRegistry() {
        val server = Bukkit.getServer() as CraftServer

        registries[Enchantment::class.java] = CraftRegistry(
            Enchantment::class.java,
            server.handle.server.registryAccess().registryOrThrow(Registries.ENCHANTMENT)
        ) { key, registry ->
            val enchant = EcoEnchants.getByID(key.key)

            if (enchant == null) {
                ModifiedVanillaCraftEnchantment(key, registry)
            } else {
                DelegatedCraftEnchantment(enchant, registry)
            }
        }
    }

    override fun register(enchant: EcoEnchant) {
        if (BuiltInRegistries.ENCHANTMENT.containsKey(CraftNamespacedKey.toMinecraft(enchant.key))) {
            return
        }

        // Unfreeze registry
        frozenField.set(BuiltInRegistries.ENCHANTMENT, false)

        Registry.register(
            BuiltInRegistries.ENCHANTMENT, enchant.id, VanillaEcoEnchantsEnchantment(
                enchant.id
            )
        )
    }

    override fun unregister(enchant: EcoEnchant) {
        /*

        You can't unregister from a minecraft registry, so we simply leave the stale reference there.
        This shouldn't cause many issues in production because unregistered enchantments can't be accessed.

         */
    }
}
