package com.github.jummes.libs.gui.setting.change;

import com.github.jummes.libs.model.ModelPath;
import com.github.jummes.libs.util.ReflectUtils;

import java.lang.reflect.Field;

public class EnumChangeInformation extends ChangeInformation {

    public EnumChangeInformation(Field field) {
        super(field);
    }

    @Override
    public void setValue(ModelPath<?> path, Object value) {
        Object enumerableValue = Enum.valueOf((Class<? extends Enum>) field.getType(), value.toString());
        try {
            Object finalValue = callBeforeModify(path, field, enumerableValue);
            ReflectUtils.writeField(field, path.getLast(), finalValue);
            path.saveModel(field);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object getValue(ModelPath<?> path) {
        Object value = null;
        try {
            value = ReflectUtils.readField(field, path.getLast());
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
