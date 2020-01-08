package com.github.jummes.libs.model;

import lombok.NonNull;

/**
 * It's a class that manages data in memory about a certain Model object, it
 * only provides a method to update the model in the database, it probably has
 * to be built better
 * 
 * @author Marco
 *
 * @param <T> the model the manager will handle
 */
public interface ModelManager<T extends Model> {

	public void saveModel(@NonNull T object);
	
	public void deleteModel(@NonNull T object);

}
