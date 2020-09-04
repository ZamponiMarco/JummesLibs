package com.github.jummes.libs.gui.setting.change;

import com.github.jummes.libs.model.ModelPath;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;

@Getter
@Setter
public abstract class ChangeInformation {

    protected Field field;

    public ChangeInformation(Field field) {
        this.field = field;
    }

    public abstract void setValue(ModelPath<?> path, Object value);

    public abstract Object getValue(ModelPath<?> path);

    public abstract String getName();

}
