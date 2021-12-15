package com.github.jummes.libs.gui.setting.change;

import com.github.jummes.libs.model.ModelPath;
import org.apache.commons.lang.reflect.FieldUtils;

import java.lang.reflect.Field;

public class FieldChangeInformation extends ChangeInformation {

    public FieldChangeInformation(Field field) {
        super(field);
    }

    @Override
    public void setValue(ModelPath<?> path, Object value) {
        try {
            Object finalValue = callBeforeModify(path, field, value);
            FieldUtils.writeField(field, path.getLast(), finalValue, true);
            path.saveModel(field);
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
