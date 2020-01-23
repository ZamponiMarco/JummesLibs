package com.github.jummes.libs.model.wrapper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.tools.DocumentationTool.Location;

import org.apache.commons.lang.reflect.FieldUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.jummes.libs.annotation.SetterMappable;
import com.github.jummes.libs.util.ReflectUtils;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class ModelWrapper<T> extends Observable {

	protected T wrapped;

	public ModelWrapper(T wrapped) {
		this.wrapped = wrapped;
		addObserver(getObserver());
	}

	@Override
	public void notifyObservers(Object arg) {
		setChanged();
		super.notifyObservers(arg);
	}

	protected Observer getObserver() {
		return (o, arg) -> {
			Field field = (Field) arg;
			try {
				Object value = FieldUtils.readField(field, this, true);
				if (field.isAnnotationPresent(SetterMappable.class)) {
					Method method = wrapped.getClass()
							.getMethod(field.getAnnotation(SetterMappable.class).setterMethod(), field.getType());
					method.setAccessible(true);
					method.invoke(wrapped, value);
				} else {
					Field toWrite = ReflectUtils.getField(wrapped, field.getName());
					FieldUtils.writeField(toWrite, wrapped, value, true);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		};
	}

	public static Map<Class<?>, Class<? extends ModelWrapper<?>>> getWrappers() {
		Map<Class<?>, Class<? extends ModelWrapper<?>>> map = new HashMap<Class<?>, Class<? extends ModelWrapper<?>>>();
		map.put(Location.class, LocationWrapper.class);
		map.put(ItemStack.class, ItemStackWrapper.class);
		map.put(ItemMeta.class, ItemMetaWrapper.class);
		return map;
	}

}
