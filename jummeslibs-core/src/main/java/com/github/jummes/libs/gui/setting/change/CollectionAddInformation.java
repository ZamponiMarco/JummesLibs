package com.github.jummes.libs.gui.setting.change;

import java.lang.reflect.Field;
import java.util.Collection;

import org.apache.commons.lang.reflect.FieldUtils;

import com.github.jummes.libs.model.Model;
import com.github.jummes.libs.model.ModelPath;
import com.github.jummes.libs.model.wrapper.ModelWrapper;

public class CollectionAddInformation implements ChangeInformation {

	private Field field;

	public CollectionAddInformation(Field field) {
		this.field = field;
	}

	@Override
	public void setValue(ModelPath<?> path, Object value) {
		Collection<Object> collection;
		try {
			collection = (Collection<Object>) FieldUtils.readField(field,
					path.getLast() == null ? path.getModelManager() : path.getLast(), true);
			collection.add(value);
			if (path.getLast() instanceof ModelWrapper<?>) {
				((ModelWrapper<?>) path.getLast()).notifyObservers(field);
			}
			if (value instanceof Model)
				path.addModel((Model) value);
			path.saveModel();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Object getValue(ModelPath<?> path) {
		return null;
	}

	@Override
	public String getName() {
		return null;
	}

}
