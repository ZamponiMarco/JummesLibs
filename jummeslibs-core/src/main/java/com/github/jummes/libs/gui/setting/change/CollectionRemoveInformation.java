package com.github.jummes.libs.gui.setting.change;

import java.lang.reflect.Field;
import java.util.Collection;

import com.github.jummes.libs.model.Model;
import com.github.jummes.libs.model.ModelPath;
import com.github.jummes.libs.model.wrapper.ModelWrapper;

public class CollectionRemoveInformation extends ChangeInformation {

    private Object currentValue;

    public CollectionRemoveInformation(Field field, Object currentValue) {
        super(field);
        this.currentValue = currentValue;
    }

    @Override
    public void setValue(ModelPath<?> path, Object value) {
        Collection<Object> collection;
        try {
            collection = (Collection<Object>) field.get(path.getLast());
            if (path.getRoot() != null) {
                path.getRoot().beforeModify();
            }
            collection.remove(currentValue);
            path.saveModel(field);
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
