package com.github.jummes.libs.gui.setting.change;

import com.github.jummes.libs.model.ModelPath;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

public class CollectionChangeInformation extends ChangeInformation {

    private Object currentValue;

    public CollectionChangeInformation(Field field, Object currentValue) {
        super(field);
        this.currentValue = currentValue;
    }

    @Override
    public void setValue(ModelPath<?> path, Object value) {
        Collection<Object> collection;
        try {
            collection = (Collection<Object>) field.get(path.getLast());
            Object finalValue = callBeforeModify(path, field, value);
            if (collection instanceof List<Object> list) {
                int i = list.indexOf(currentValue);
                list.set(i, finalValue);
            } else {
                collection.remove(currentValue);
                collection.add(finalValue);
            }
            path.saveModel(field);
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
        return field.getName();
    }

}
