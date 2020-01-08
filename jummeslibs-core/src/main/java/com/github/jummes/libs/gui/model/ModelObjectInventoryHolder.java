package com.github.jummes.libs.gui.model;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.jummes.libs.annotation.GUISerializable;
import com.github.jummes.libs.gui.PluginInventoryHolder;
import com.github.jummes.libs.gui.setting.factory.FieldInventoryHolderFactory;
import com.github.jummes.libs.model.Model;
import com.github.jummes.libs.model.ModelPath;
import com.github.jummes.libs.util.ItemUtils;
import com.google.common.collect.Lists;

/**
 * An InventoryHolder that contains in memory them modelPath used to arrive to
 * him, it usually allows the modification of parameters of models and its
 * subclasses
 * 
 * @author Marco
 *
 */
public class ModelObjectInventoryHolder<T extends Model> extends PluginInventoryHolder {

	protected ModelPath<? extends Model> path;

	public ModelObjectInventoryHolder(JavaPlugin plugin, PluginInventoryHolder parent,
			ModelPath<? extends Model> path) {
		super(plugin, parent);
		this.path = path;
	}

	@Override
	protected void initializeInventory() {
		this.inventory = Bukkit.createInventory(this, 27, path.getLast().getClass().getSimpleName());

		List<Field> fields = new ArrayList<>();
		Class<?> clazz = path.getLast().getClass();
		while (clazz != Object.class) {
			fields.addAll(0, Lists.newArrayList(clazz.getDeclaredFields()));
			clazz = clazz.getSuperclass();
		}
		Field[] toPrint = fields.stream().filter(field -> field.isAnnotationPresent(GUISerializable.class))
				.toArray(size -> new Field[size]);
		int[] itemPositions = getItemPositions(toPrint.length);

		IntStream.range(0, toPrint.length).forEach(i -> {
			registerClickConsumer(itemPositions[i],
					ItemUtils.getNamedItem(
							wrapper.skullFromValue(toPrint[i].getAnnotation(GUISerializable.class).headTexture()),
							toPrint[i].getName(), new ArrayList<String>()),
					e -> {
						try {
							e.getWhoClicked()
									.openInventory(FieldInventoryHolderFactory
											.createFieldInventoryHolder(plugin, this, path, toPrint[i], e.getClick())
											.getInventory());
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					});
		});
		registerClickConsumer(26, getBackItem(), getBackConsumer());
		fillInventoryWith(Material.GRAY_STAINED_GLASS_PANE);
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
	public int[] getItemPositions(int items) {

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

		Function<Integer, int[]> horizontal_dots = (distFromCenter) -> new int[] {
				calculatePosition.apply(-distFromCenter, 0), calculatePosition.apply(distFromCenter, 0) };

		Function<Integer, int[]> corners = (distFromCenter) -> new int[] { calculatePosition.apply(-distFromCenter, -1),
				calculatePosition.apply(distFromCenter, -1), calculatePosition.apply(-distFromCenter, +1),
				calculatePosition.apply(distFromCenter, +1) };

		Function<Integer, int[]> vertical_bars = (distFromCenter) -> new int[] {
				calculatePosition.apply(-distFromCenter, -1), calculatePosition.apply(-distFromCenter, +1),
				calculatePosition.apply(-distFromCenter, 0), calculatePosition.apply(distFromCenter, -1),
				calculatePosition.apply(distFromCenter, +1), calculatePosition.apply(distFromCenter, 0) };

		Function<Integer, int[]> vertical_dots = (distFromCenter) -> new int[] {
				calculatePosition.apply(0, -distFromCenter), calculatePosition.apply(0, distFromCenter) };

		Supplier<int[]> square = () -> IntStream
				.concat(Arrays.stream(vertical_bars.apply(1)), Arrays.stream(vertical_dots.apply(1))).toArray();

		Supplier<int[]> cross = () -> IntStream
				.concat(Arrays.stream(horizontal_dots.apply(1)), Arrays.stream(vertical_dots.apply(1))).toArray();

		if ((items % 2) != 0)
			addToPositions.accept(new int[] { center });

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
				if (this instanceof ModelObjectInventoryHolder<?>) {
					path.removeModel();
				}
				e.getWhoClicked().openInventory(parent.getInventory());
			} else {
				e.getWhoClicked().closeInventory();
			}
		};
	}

}
