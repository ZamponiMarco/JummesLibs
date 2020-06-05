package com.github.jummes.libs.gui.setting.change;

import java.lang.reflect.Field;

import org.apache.commons.lang.reflect.FieldUtils;

import com.github.jummes.libs.model.ModelPath;
import com.github.jummes.libs.model.wrapper.ModelWrapper;

public class EnumChangeInformation implements ChangeInformation {

    private Field field;

    public EnumChangeInformation(Field field) {
        this.field = field;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void setValue(ModelPath<?> path, Object value) {
        Object enumerableValue = Enum.valueOf((Class<? extends Enum>) field.getType(), value.toString());
        try {
            FieldUtils.writeField(field, path.getLast(), enumerableValue, true);
            if (path.getLast() instanceof ModelWrapper<?>) {
                ((ModelWrapper<?>) path.getLast()).notifyObservers(field);
            }
            path.saveModel();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object getValue(ModelPath<?> path) {
        Object value = null;
        try {
            value = FieldUtils.readField(field, path.getLast(), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    @Override
    public String getName() {
        return field.getName();
    }

}
