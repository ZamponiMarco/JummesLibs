package com.github.jummes.libs.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to describe the path of models that contains other models in
 * it, the method update model will call the updateModel method of the root
 * modelManager
 * 
 * @author Marco
 *
 * @param <T>
 */
public class ModelPath<T extends Model> {

	private ModelManager<T> modelManager;
	private T root;
	private List<Model> modelPath;

	public ModelPath(ModelManager<T> modelManager, T root) {
		this.modelManager = modelManager;
		this.root = root;
		modelPath = new ArrayList<>();
	}

	public List<Model> getModelPath() {
		return modelPath;
	}

	public boolean addModel(Model model) {
		return modelPath.add(model);
	}

	public void updateModel() {
		modelManager.updateModel(root);
	}

	public T getRoot() {
		return root;
	}

	public Model getLast() {
		return modelPath.isEmpty() ? root : modelPath.get(modelPath.size() - 1);
	}
}
