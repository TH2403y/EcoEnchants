package com.willfp.ecoenchants.enchantments.meta;

import com.google.common.collect.ImmutableList;
import com.willfp.eco.util.config.Configs;
import com.willfp.eco.util.config.annotations.ConfigUpdater;
import com.willfp.ecoenchants.enchantments.EcoEnchant;
import com.willfp.ecoenchants.enchantments.itemtypes.Artifact;
import com.willfp.ecoenchants.enchantments.itemtypes.Spell;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class EnchantmentType {
    /**
     * All registered types.
     */
    private static final List<EnchantmentType> REGISTERED = new ArrayList<>();

    /**
     * Most enchantments are like this.
     * <p>
     * eg: Arachnid, Telekinesis, Sharpness.
     */
    public static final EnchantmentType NORMAL = new EnchantmentType(
            "normal",
            false,
            () -> Configs.LANG.getString("not-curse-color")
    );

    /**
     * Negative enchantments.
     * <p>
     * eg: Curse of Decay, Curse of Vanishing.
     */
    public static final EnchantmentType CURSE = new EnchantmentType(
            "curse",
            false,
            () -> Configs.LANG.getString("curse-color")
    );

    /**
     * Extremely powerful enchantments.
     * <p>
     * eg: Razor, Force.
     */
    public static final EnchantmentType SPECIAL = new EnchantmentType(
            "special",
            () -> !Configs.CONFIG.getBool("types.special.allow-multiple"),
            () -> Configs.LANG.getString("special-color")
    );

    /**
     * Cosmetic enchantments.
     * <p>
     * eg: Ash Artifact, Totem Artifact.
     */
    public static final EnchantmentType ARTIFACT = new EnchantmentType(
            "artifact",
            () -> !Configs.CONFIG.getBool("types.artifact.allow-multiple"),
            () -> Configs.LANG.getString("artifact-color"),
            Artifact.class
    );

    /**
     * Ability enchantments.
     * <p>
     * eg: Missile, Quake.
     */
    public static final EnchantmentType SPELL = new EnchantmentType(
            "spell",
            true,
            () -> Configs.LANG.getString("spell-color"),
            Spell.class
    );

    /**
     * Lambda to fetch the color of the type.
     */
    private final Supplier<String> colorSupplier;

    /**
     * Lambda to fetch the singularity of the type.
     */
    private final Supplier<Boolean> singularSupplier;

    /**
     * If only one enchantment of this type is allowed on an item.
     */
    @Getter
    private boolean singular;

    /**
     * The color of enchantments of this type to have in lore.
     */
    @Getter
    private String color;

    /**
     * The name of the type.
     */
    @Getter
    private final String name;

    /**
     * The class that all enchantments of this type must extend.
     * <p>
     * Null if not required.
     */
    @Getter
    @Nullable
    private final Class<? extends EcoEnchant> requiredToExtend;

    /**
     * Create simple EnchantmentType.
     * <p>
     * Singularity and Color will not be updated using this constructor.
     *
     * @param name     The name of the type.
     * @param singular Whether an item can have several enchantments of this type.
     * @param color    The color for enchantments with this type in lore to have.
     */
    public EnchantmentType(@NotNull final String name,
                           final boolean singular,
                           @NotNull final String color) {
        this(name, () -> singular, () -> color);
    }

    /**
     * Create EnchantmentType with updatable color.
     * <p>
     * Singularity will not be updated using this constructor.
     *
     * @param name          The name of the type.
     * @param singular      Whether an item can have several enchantments of this type.
     * @param colorSupplier Lambda to fetch the color of enchantments with this type to have. Updates on /ecoreload.
     */
    public EnchantmentType(@NotNull final String name,
                           final boolean singular,
                           @NotNull final Supplier<String> colorSupplier) {
        this(name, () -> singular, colorSupplier);
    }

    /**
     * Create EnchantmentType with updatable color that <b>must</b> extend a specified class.
     * <p>
     * Singularity will not be updated using this constructor.
     *
     * @param name             The name of the type.
     * @param singular         Whether an item can have several enchantments of this type.
     * @param colorSupplier    Lambda to fetch the color of enchantments with this type to have. Updates on /ecoreload.
     * @param requiredToExtend Class that all enchantments of this type must extend - or null if not required.
     */
    public EnchantmentType(@NotNull final String name,
                           final boolean singular,
                           @NotNull final Supplier<String> colorSupplier,
                           @Nullable final Class<? extends EcoEnchant> requiredToExtend) {
        this(name, () -> singular, colorSupplier, requiredToExtend);
    }

    /**
     * Create EnchantmentType with updatable color and singularity.
     *
     * @param name             The name of the type.
     * @param singularSupplier Lambda to fetch whether an item can have several enchantments of this type. Updates on /ecoreload.
     * @param colorSupplier    Lambda to fetch the color of enchantments with this type to have. Updates on /ecoreload.
     */
    public EnchantmentType(@NotNull final String name,
                           @NotNull final Supplier<Boolean> singularSupplier,
                           @NotNull final Supplier<String> colorSupplier) {
        this(name, singularSupplier, colorSupplier, null);
    }

    /**
     * Create EnchantmentType with updatable color and singularity that <b>must</b> extend a specified class.
     *
     * @param name             The name of the type.
     * @param singularSupplier Lambda to fetch whether an item can have several enchantments of this type. Updates on /ecoreload.
     * @param colorSupplier    Lambda to fetch the color of enchantments with this type to have. Updates on /ecoreload.
     * @param requiredToExtend Class that all enchantments of this type must extend - or null if not required.
     */
    public EnchantmentType(@NotNull final String name,
                           @NotNull final Supplier<Boolean> singularSupplier,
                           @NotNull final Supplier<String> colorSupplier,
                           @Nullable final Class<? extends EcoEnchant> requiredToExtend) {
        this.name = name;
        this.singularSupplier = singularSupplier;
        this.colorSupplier = colorSupplier;
        this.requiredToExtend = requiredToExtend;
        color = colorSupplier.get();
        singular = singularSupplier.get();
        REGISTERED.add(this);
    }

    private void refresh() {
        this.color = colorSupplier.get();
        this.singular = singularSupplier.get();
    }

    /**
     * Update suppliers of all types.
     */
    @ConfigUpdater
    public static void update() {
        REGISTERED.forEach(EnchantmentType::refresh);
    }

    /**
     * All registered enchantment types.
     *
     * @return All registered types.
     */
    public static List<EnchantmentType> values() {
        return ImmutableList.copyOf(REGISTERED);
    }
}
