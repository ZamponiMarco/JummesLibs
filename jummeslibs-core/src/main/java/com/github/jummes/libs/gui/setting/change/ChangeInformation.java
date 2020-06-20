package com.github.jummes.libs.gui.setting.change;

import com.github.jummes.libs.model.ModelPath;

public interface ChangeInformation {

    void setValue(ModelPath<?> path, Object value);

    Object getValue(ModelPath<?> path);

    String getName();

}
