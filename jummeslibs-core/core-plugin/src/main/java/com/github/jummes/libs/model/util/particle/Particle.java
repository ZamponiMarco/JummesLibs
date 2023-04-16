package com.github.jummes.libs.model.util.particle;

import com.github.jummes.libs.annotation.Serializable;
import com.github.jummes.libs.model.Model;
import com.github.jummes.libs.model.util.particle.options.ParticleOptions;
import com.github.jummes.libs.model.wrapper.VectorWrapper;
import com.github.jummes.libs.util.MessageUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.util.Map;

@Getter
@Setter
public class Particle implements Model, Cloneable {

    private static final int COUNT_DEFAULT = 1;
    private static final VectorWrapper OFFSET_DEFAULT = new VectorWrapper(new Vector(0, 0, 0));
    private static final double SPEED_DEFAULT = 0;
    private static final boolean FORCE_DEFAULT = false;

    private static final String TYPE_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWY4NDczNWZjOWM3NjBlOTVlYWYxMGNlYzRmMTBlZGI1ZjM4MjJhNWZmOTU1MWVlYjUwOTUxMzVkMWZmYTMwMiJ9fX0=";
    private static final String COUNT_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjdkYzNlMjlhMDkyM2U1MmVjZWU2YjRjOWQ1MzNhNzllNzRiYjZiZWQ1NDFiNDk1YTEzYWJkMzU5NjI3NjUzIn19fQ==";
    private static final String OFFSET_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDI4OTIyMTdjZThmYTg0MTI4YWJlMjY0YjVlNzFkN2VlN2U2YTlkNTgyMzgyNThlZjdkMmVmZGMzNDcifX19";
    private static final String SPEED_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTNmYzUyMjY0ZDhhZDllNjU0ZjQxNWJlZjAxYTIzOTQ3ZWRiY2NjY2Y2NDkzNzMyODliZWE0ZDE0OTU0MWY3MCJ9fX0=";
    private static final String FORCE_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2VkMWFiYTczZjYzOWY0YmM0MmJkNDgxOTZjNzE1MTk3YmUyNzEyYzNiOTYyYzk3ZWJmOWU5ZWQ4ZWZhMDI1In19fQ==";

    @Serializable(headTexture = TYPE_HEAD, stringValue = true, description = "gui.action.location.particle.type", fromListMapper = "particlesMapper", fromList = "getParticles")
    private org.bukkit.Particle type;

    @Serializable(headTexture = COUNT_HEAD, description = "gui.action.location.particle.count", additionalDescription = {"gui.additional-tooltips.value"})
    @Serializable.Number(minValue = 0, scale = 1)
    @Serializable.Optional(defaultValue = "COUNT_DEFAULT")
    private int count;

    @Serializable(headTexture = OFFSET_HEAD, description = "gui.action.location.particle.offset", additionalDescription = {"gui.additional-tooltips.value"})
    @Serializable.Number(minValue = 0)
    @Serializable.Optional(defaultValue = "OFFSET_DEFAULT")
    private VectorWrapper offset;

    @Serializable(headTexture = SPEED_HEAD, description = "gui.action.location.particle.speed", additionalDescription = {"gui.additional-tooltips.value"})
    @Serializable.Number(minValue = 0)
    @Serializable.Optional(defaultValue = "SPEED_DEFAULT")
    private double speed;

    @Serializable(headTexture = FORCE_HEAD, description = "gui.action.location.particle.force")
    @Serializable.Optional(defaultValue = "FORCE_DEFAULT")
    private boolean force;
    @Serializable(displayItem = "getDataObject", description = "gui.action.location.particle.data")
    private ParticleOptions data;

    public Particle() {
        this(org.bukkit.Particle.FIREWORKS_SPARK, COUNT_DEFAULT, new VectorWrapper(), SPEED_DEFAULT, FORCE_DEFAULT, null);
    }

    public Particle(org.bukkit.Particle type, int count, VectorWrapper offset, double speed, boolean force, ParticleOptions data) {
        this.type = type;
        this.count = count;
        this.offset = offset;
        this.speed = speed;
        this.force = force;
        this.data = data;
    }

    public Particle(Map<String, Object> map) {
        this.type = org.bukkit.Particle.valueOf((String) map.getOrDefault("type", "FIREWORKS_SPARK"));
        this.force = (boolean) map.getOrDefault("force", FORCE_DEFAULT);
        this.count = (int) map.getOrDefault("count", COUNT_DEFAULT);
        this.offset = (VectorWrapper) map.getOrDefault("offset", OFFSET_DEFAULT.clone());
        this.speed = (double) map.getOrDefault("speed", SPEED_DEFAULT);
        this.data = (ParticleOptions) map.get("data");
    }

    public void spawnParticle(Location location) {
        if (location.getWorld() == null) {
            return;
        }

        location.getWorld().spawnParticle(type, location, count, offset.getWrapped().getX(), offset.getWrapped().getY(),
                offset.getWrapped().getZ(), speed, data == null ? null : data.buildData(), force);
    }

    @Override
    public Particle clone() {
        return new Particle(type, count, offset.clone(), speed, force, data == null ? null : data.clone());
    }

    public String getName() {
        return "&6&lParticle: &c" + MessageUtils.capitalize(type.toString());
    }

    public ItemStack getDataObject() {
        if (ParticleOptions.getParticleOptionsMap().containsKey(type.getDataType())) {
            return new ItemStack(Material.DIAMOND);
        }
        return null;
    }

    @Override
    public void onModify(Field field) {
        if (data == null || !data.getClass().equals(ParticleOptions.getParticleOptionsMap().get(type.getDataType()))) {
            data = ParticleOptions.buildOptions(type.getDataType());
        }
    }

}
