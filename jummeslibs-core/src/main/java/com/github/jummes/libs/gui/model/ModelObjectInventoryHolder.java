package com.github.jummes.libs.gui.model;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.IntStream;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.jummes.libs.annotation.GUISerializable;
import com.github.jummes.libs.gui.PluginInventoryHolder;
import com.github.jummes.libs.gui.setting.factory.FieldInventoryHolderFactory;
import com.github.jummes.libs.model.Model;
import com.github.jummes.libs.model.ModelPath;
import com.github.jummes.libs.util.ItemUtils;

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

		Field[] fields = path.getLast().getClass().getDeclaredFields();
		Field[] toPrint = Arrays.stream(fields).filter(field -> field.isAnnotationPresent(GUISerializable.class))
				.toArray(size -> new Field[size]);

		IntStream.range(0, toPrint.length).forEach(i -> {
			registerClickConsumer(i,
					ItemUtils.getNamedItem(
							wrapper.skullFromValue(toPrint[i].getAnnotation(GUISerializable.class).headTexture()),
							toPrint[i].getName(), new ArrayList<String>()),
					e -> {
						try {
							e.getWhoClicked().openInventory(FieldInventoryHolderFactory
									.createFieldInventoryHolder(plugin, this, path, toPrint[i]).getInventory());
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					});
		});

		fillInventoryWith(Material.GRAY_STAINED_GLASS_PANE);
	}
	/*
	* A method which returns coordinates of center inventory positions
	* according to number of items
	* @param items int which represent the number of items to dispose into inventory
	* @return int[] of inventory coordinates
	* 
	* @author dmitry-mingazov
	*/
	public int[] getItemPositions(int items){

  if(items > 17 || items < 0)
    throw new IllegalArgumentException("Number of items not valid");

		final int LENGTH = 9;
		int center_X = LENGTH/2;
		int center_Y = 1;
		int offset = 2;
		int[] positions = new int[items];
		AtomicInteger atomicItemsPushed = new AtomicInteger(0);
		AtomicInteger atomicItemsToPush = new AtomicInteger(items);

		final BiFunction<Integer, Integer, Integer> calculatePosition = (x, y) -> (center_X + x) * LENGTH + (center_Y + y);

		int center = calculatePosition.apply(0, 0);

		Consumer<int[]> addToPositions = (pattern) -> {
			for(int position : pattern){
				positions[atomicItemsPushed.getAndIncrement()] = position;
				atomicItemsToPush.decrementAndGet();
			}
		};

		Function<Integer, int[]> horizontal_dots = (distFromCenter) -> new int[] {calculatePosition.apply(-distFromCenter, 0),
					calculatePosition.apply(distFromCenter, 0)};

		Function<Integer, int[]> corners = (distFromCenter) ->
			new int[] {calculatePosition.apply(-distFromCenter, -1),
					calculatePosition.apply(distFromCenter, -1),
					calculatePosition.apply(-distFromCenter, +1),
					calculatePosition.apply(distFromCenter, +1)};

		Function<Integer, int[]> vertical_bars = (distFromCenter) ->
			new int[] {calculatePosition.apply(-distFromCenter, -1),
					calculatePosition.apply(-distFromCenter, +1),
					calculatePosition.apply(-distFromCenter, 0),
					calculatePosition.apply(distFromCenter, -1),
					calculatePosition.apply(distFromCenter, +1),
					calculatePosition.apply(distFromCenter, 0)};

		Function<Integer, int[]>vertical_dots = (distFromCenter) -> 
			new int[] {calculatePosition.apply(0, -distFromCenter),
					calculatePosition.apply(0, distFromCenter)};

		Supplier<int[]> square = () ->
			IntStream.concat(Arrays.stream(vertical_bars.apply(1)), Arrays.stream(vertical_dots.apply(1))).toArray();

		Supplier<int[]> cross = () ->
			IntStream.concat(Arrays.stream(horizontal_dots.apply(1)), Arrays.stream(vertical_dots.apply(1))).toArray();

		if((items % 2) != 0)
		    addToPositions.accept(new int[]{center});

		if(atomicItemsToPush.get() < 9){
			switch(atomicItemsToPush.get()){
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
		}else{
			addToPositions.accept(square.get());
			while(atomicItemsToPush.get() > 0){
				while(atomicItemsToPush.get() >= 6){
					addToPositions.accept(vertical_bars.apply(offset++));
				}
				switch(atomicItemsToPush.get()){
          case 0:
            break;
					case 2:
						addToPositions.accept(horizontal_dots.apply(offset++));
						break;
					case 4:
            
						addToPositions.accept(corners.apply(offset++));
						break;
          default:
            throw new IllegalStateException("Number not accepted: "+atomicItemsToPush.get());
				}
			}
		}


		return positions;
	}

}
