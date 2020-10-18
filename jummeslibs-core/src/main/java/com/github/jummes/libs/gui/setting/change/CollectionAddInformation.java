package com.github.jummes.libs.gui.setting.change;

import com.github.jummes.libs.model.Model;
import com.github.jummes.libs.model.ModelPath;
import com.github.jummes.libs.model.wrapper.ModelWrapper;
import org.apache.commons.lang.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.Collection;

public class CollectionAddInformation extends ChangeInformation {

    public CollectionAddInformation(Field field) {
        super(field);
    }

    @Override
    public void setValue(ModelPath<?> path, Object value) {
        Collection<Object> collection;
        try {
            if (path.getRoot() != null) {
                path.getRoot().beforeModify();
            }
            collection = (Collection<Object>) FieldUtils.readField(field,
                    path.getLast() == null ? path.getModelManager() : path.getLast(), true);
            collection.add(value);
            if (value instanceof Model)
                path.addModel((Model) value);
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
