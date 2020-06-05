package com.github.jummes.libs.gui.setting.change;

import java.lang.reflect.Field;
import java.util.Collection;

import com.github.jummes.libs.model.ModelPath;
import com.github.jummes.libs.model.wrapper.ModelWrapper;

public class CollectionRemoveInformation implements ChangeInformation {

    private Field field;
    private Object currentValue;

    public CollectionRemoveInformation(Field field, Object currentValue) {
        this.field = field;
        this.currentValue = currentValue;
    }

    @Override
    public void setValue(ModelPath<?> path, Object value) {
        Collection<Object> collection;
        try {
            collection = (Collection<Object>) field.get(path.getLast());
            collection.remove(currentValue);
            if (path.getLast() instanceof ModelWrapper<?>) {
                ((ModelWrapper<?>) path.getLast()).notifyObservers(field);
            }
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
