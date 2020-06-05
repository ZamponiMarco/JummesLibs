package com.github.jummes.libs.gui.setting.change;

import com.github.jummes.libs.model.ModelPath;

public interface ChangeInformation {

    public void setValue(ModelPath<?> path, Object value);

    public Object getValue(ModelPath<?> path);

    public String getName();

}
