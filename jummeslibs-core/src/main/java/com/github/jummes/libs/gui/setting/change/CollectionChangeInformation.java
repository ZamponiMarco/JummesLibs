package com.github.jummes.libs.gui.setting.change;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

import com.github.jummes.libs.model.ModelPath;
import com.github.jummes.libs.model.wrapper.ModelWrapper;

public class CollectionChangeInformation implements ChangeInformation {

    private Field field;
    private Object currentValue;

    public CollectionChangeInformation(Field field, Object currentValue) {
        this.field = field;
        this.currentValue = currentValue;
    }

    @Override
    public void setValue(ModelPath<?> path, Object value) {
        Collection<Object> collection;
        try {
            collection = (Collection<Object>) field.get(path.getLast());
            if (collection instanceof List) {
                List<Object> list = (List<Object>) collection;
                int i = list.indexOf(currentValue);
                list.set(i, value);
            } else {
                collection.remove(currentValue);
                collection.add(value);
            }
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
        return currentValue;
    }

    @Override
    public String getName() {
        return "sas";
    }

}
