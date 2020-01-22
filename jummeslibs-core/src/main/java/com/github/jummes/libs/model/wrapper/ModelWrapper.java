package com.github.jummes.libs.model.wrapper;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.tools.DocumentationTool.Location;

import org.apache.commons.lang.math.IntRange;
import org.apache.commons.lang.reflect.FieldUtils;

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

	public void changed() {
		setChanged();
	}

	protected Observer getObserver() {
		return (o, arg) -> {
			Field field = (Field) arg;
			try {
				FieldUtils.writeDeclaredField(wrapped, field.getName(), field.get(this), true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
	}

	public static Map<Class<?>, Class<? extends ModelWrapper<?>>> getWrappers() {
		Map<Class<?>, Class<? extends ModelWrapper<?>>> map = new HashMap<Class<?>, Class<? extends ModelWrapper<?>>>();
		map.put(Location.class, LocationWrapper.class);
		map.put(IntRange.class, IntRangeWrapper.class);
		return map;
	}

}
