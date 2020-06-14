package com.github.jummes.libs.model;

import com.github.jummes.libs.annotation.Serializable;
import lombok.Getter;

import java.util.UUID;

@Getter
public class IdentifiableModel implements Model {

    @Serializable(stringValue = true, primaryKey = true)
    protected final UUID id;

    public IdentifiableModel(){
        id = UUID.randomUUID();
    }

    public IdentifiableModel(UUID id){
        this.id = id;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IdentifiableModel that = (IdentifiableModel) o;

        return id.equals(that.id);
    }

    @Override
    public final int hashCode() {
        return id.hashCode();
    }
}
