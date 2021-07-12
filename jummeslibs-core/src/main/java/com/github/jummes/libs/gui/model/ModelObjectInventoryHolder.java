package com.github.jummes.libs.gui.model;

import com.github.jummes.libs.annotation.GUINameable;
import com.github.jummes.libs.annotation.Serializable;
import com.github.jummes.libs.core.Libs;
import com.github.jummes.libs.gui.FieldInventoryHolderFactory;
import com.github.jummes.libs.gui.PluginInventoryHolder;
import com.github.jummes.libs.model.Model;
import com.github.jummes.libs.model.ModelPath;
import com.github.jummes.libs.util.ItemUtils;
import com.github.jummes.libs.util.MessageUtils;
import com.github.jummes.libs.util.ReflectUtils;
import lombok.SneakyThrows;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.reflect.FieldUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.*;
import java.util.stream.IntStream;

/**
 * An InventoryHolder that contains in memory them modelPath used to arrive to
 * him, it usually allows the modification of parameters of models and its
 * subclasses
 *
 * @author Marco
 */
public class ModelObjectInventoryHolder extends PluginInventoryHolder {

    protected ModelPath<? extends Model> path;
    private Class<?> clazz;

    public ModelObjectInventoryHolder(JavaPlugin plugin, PluginInventoryHolder parent,
                                      ModelPath<? extends Model> path) {
        super(plugin, parent);
        this.path = path;
        this.clazz = path.getLast() != null ? path.getLast().getClass() : null;
    }

    @Override
    protected void initializeInventory() {
        // If path changed, get back to original path
        if (this.clazz != null) {
            while (!path.getLast().getClass().equals(this.clazz)) {
                path.popModel();
            }
        }

        // Get title and create inventory
        String title = getTitleString(path.getLast());
        this.inventory = Bukkit.createInventory(this, 27, title);

        // List displayable fields and print them in GUI
        List<Field> fields = ReflectUtils.getFieldsList(path.getLast());
        Field[] toPrint = fields.stream().filter(getItemFilter(clazz)).toArray(Field[]::new);
        int[] itemPositions = getItemPositions(toPrint.length);

        IntStream.range(0, toPrint.length).forEach(i -> {
            try {
                ItemStack displayItem = getFieldDisplayItem(toPrint, i);
                if (displayItem != null) {
                    registerClickConsumer(itemPositions[i],
                            displayItem,
                            e -> {
                                if (toPrint[i].isAnnotationPresent(Serializable.CustomClickable.class)) {
                                    try {
                                        path.getLast().getClass().getMethod(toPrint[i].getAnnotation(Serializable.CustomClickable.class).
                                                        customClickConsumer(), JavaPlugin.class, PluginInventoryHolder.class,
                                                ModelPath.class, Field.class, InventoryClickEvent.class).
                                                invoke(path.getLast(), plugin, this, path, toPrint[i], e);
                                    } catch (Exception exception) {
                                        exception.printStackTrace();
                                    }
                                } else {
                                    e.getWhoClicked().openInventory(FieldInventoryHolderFactory
                                            .createFieldInventoryHolder(plugin, this, path, toPrint[i], e).
                                                    getInventory());
                                }
                            });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Back button and fill the rest
        registerClickConsumer(26, getBackItem(), getBackConsumer());
        fillInventoryWith(Material.GRAY_STAINED_GLASS_PANE);
    }

    private ItemStack getFieldDisplayItem(Field[] toPrint, int i) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        ItemStack item = !toPrint[i].getAnnotation(Serializable.class).headTexture().equals("") ?
                wrapper.skullFromValue(toPrint[i].getAnnotation(Serializable.class).headTexture()) :
                (ItemStack) clazz.getMethod(toPrint[i].getAnnotation(Serializable.class).displayItem()).invoke(path.getLast());
        if (item != null) {
            String itemName = MessageUtils.color("&6&l" + toPrint[i].getName() + " » &c&l" + getValueString(toPrint[i]));
            List<String> lore = Libs.getLocale().getList(toPrint[i].getAnnotation(Serializable.class).description());
            Arrays.stream(toPrint[i].getAnnotation(Serializable.class).additionalDescription()).forEach(description -> {
                lore.addAll(Libs.getLocale().getList(description));
            });
            return ItemUtils.getNamedItem(item, itemName, lore);
        }
        return null;
    }

    private Predicate<Field> getItemFilter(Class<?> clazz) {
        return field -> {
            try {
                return field.isAnnotationPresent(Serializable.class)
                        && (!field.getAnnotation(Serializable.class).headTexture().equals("") || (!field.getAnnotation(Serializable.class).displayItem().equals("") &&
                        clazz.getMethod(field.getAnnotation(Serializable.class).displayItem()).invoke(path.getLast()) != null));
            } catch (Exception e) {
                return false;
            }
        };
    }

    @SneakyThrows
    private String getTitleString(Model object) {
        String name;
        Class<?> clazz = object.getClass();
        if (clazz.isAnnotationPresent(GUINameable.class)) {
            name = (String) clazz.getMethod(clazz.getAnnotation(GUINameable.class).GUIName()).invoke(object);
        } else {
            name = clazz.getSimpleName();
        }
        return MessageUtils.color("&c&l" + name);
    }

    private String getValueString(Field field) {
        String valueToPrint;
        try {
            if (ClassUtils.isAssignable(field.getType(), Collection.class)) {
                valueToPrint = "Collection";
            } else if (ClassUtils.isAssignable(field.getType(), Model.class)
                    && !(field.getType().isAnnotationPresent(GUINameable.class)
                    && field.getType().getAnnotation(GUINameable.class).stringValue())) {
                valueToPrint = field.getType().isAnnotationPresent(GUINameable.class)
                        && !field.getType().getAnnotation(GUINameable.class).GUIName().equalsIgnoreCase("")
                        ? (String) field.getType().getMethod(field.getType().getAnnotation(GUINameable.class).
                        GUIName()).invoke(FieldUtils.readField(field, path.getLast(), true))
                        : FieldUtils.readField(field, path.getLast(), true).getClass().getSimpleName();
            } else if (field.getType().isEnum()) {
                Enum<?> obj = (Enum<?>) FieldUtils.readField(field, path.getLast(), true);
                valueToPrint = obj.name();
            } else {
                Object obj = FieldUtils.readField(field, path.getLast(), true);
                valueToPrint = obj == null ? "null" : obj.toString();
            }
            if (valueToPrint.length() > 60) {
                valueToPrint = valueToPrint.substring(0, 58).concat("...");
            }
            return valueToPrint;
        } catch (Exception e) {
            return "null";
        }
    }

    /*
     * A method which returns coordinates of center inventory positions according to
     * number of items
     *
     * @param items int which represent the number of items to dispose into
     * inventory
     *
     * @return int[] of inventory coordinates
     *
     * @author dmitry-mingazov
     */
    protected int[] getItemPositions(int items) {

        if (items > 17 || items < 0)
            throw new IllegalArgumentException("Number of items not valid");

        final int LENGTH = 9;
        int center_X = LENGTH / 2;
        int center_Y = 1;
        int offset = 2;
        int[] positions = new int[items];
        AtomicInteger atomicItemsPushed = new AtomicInteger(0);
        AtomicInteger atomicItemsToPush = new AtomicInteger(items);

        final BiFunction<Integer, Integer, Integer> calculatePosition = (x, y) -> (center_Y + y) * LENGTH
                + (center_X + x);

        int center = calculatePosition.apply(0, 0);

        Consumer<int[]> addToPositions = (pattern) -> {
            for (int position : pattern) {
                positions[atomicItemsPushed.getAndIncrement()] = position;
                atomicItemsToPush.decrementAndGet();
            }
        };

        Function<Integer, int[]> horizontal_dots = (distFromCenter) -> new int[]{
                calculatePosition.apply(-distFromCenter, 0), calculatePosition.apply(distFromCenter, 0)};

        Function<Integer, int[]> corners = (distFromCenter) -> new int[]{calculatePosition.apply(-distFromCenter, -1),
                calculatePosition.apply(distFromCenter, -1), calculatePosition.apply(-distFromCenter, +1),
                calculatePosition.apply(distFromCenter, +1)};

        Function<Integer, int[]> vertical_bars = (distFromCenter) -> new int[]{
                calculatePosition.apply(-distFromCenter, -1), calculatePosition.apply(-distFromCenter, +1),
                calculatePosition.apply(-distFromCenter, 0), calculatePosition.apply(distFromCenter, -1),
                calculatePosition.apply(distFromCenter, +1), calculatePosition.apply(distFromCenter, 0)};

        Function<Integer, int[]> vertical_dots = (distFromCenter) -> new int[]{
                calculatePosition.apply(0, -distFromCenter), calculatePosition.apply(0, distFromCenter)};

        Supplier<int[]> square = () -> IntStream
                .concat(Arrays.stream(vertical_bars.apply(1)), Arrays.stream(vertical_dots.apply(1))).toArray();

        Supplier<int[]> cross = () -> IntStream
                .concat(Arrays.stream(horizontal_dots.apply(1)), Arrays.stream(vertical_dots.apply(1))).toArray();

        if ((items % 2) != 0)
            addToPositions.accept(new int[]{center});

        if (atomicItemsToPush.get() < 9) {
            switch (atomicItemsToPush.get()) {
                case 8:
                    addToPositions.accept(square.get());
                    break;
                case 6:
                    addToPositions.accept(cross.get());
                    addToPositions.accept(horizontal_dots.apply(2));
                    break;
                case 4:
                    addToPositions.accept(cross.get());
                    break;
                case 2:
                    addToPositions.accept(horizontal_dots.apply(1));
                    break;
                case 0:

                    break;
            }
        } else {
            addToPositions.accept(square.get());
            while (atomicItemsToPush.get() > 0) {
                while (atomicItemsToPush.get() >= 6) {
                    addToPositions.accept(vertical_bars.apply(offset++));
                }
                switch (atomicItemsToPush.get()) {
                    case 0:
                        break;
                    case 2:
                        addToPositions.accept(horizontal_dots.apply(offset++));
                        break;
                    case 4:

                        addToPositions.accept(corners.apply(offset++));
                        break;
                    default:
                        throw new IllegalStateException("Number not accepted: " + atomicItemsToPush.get());
                }
            }
        }

        return positions;
    }

    protected Consumer<InventoryClickEvent> getBackConsumer() {
        return e -> {
            if (parent != null) {
                path.popModel();
                e.getWhoClicked().openInventory(parent.getInventory());
            } else {
                e.getWhoClicked().closeInventory();
            }
        };
    }

}
