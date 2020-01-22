package com.github.jummes.libs.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.ToString;

/**
 * Utility class to describe the path of models that contains other models in
 * it, the method update model will call the updateModel method of the root
 * modelManager
 * 
 * @author Marco
 *
 * @param <T>
 */
@ToString
public class ModelPath<T extends Model> {

	@Getter
	private ModelManager<T> modelManager;
	@Getter
	private T root;

	private List<Model> modelPath;

	public ModelPath(ModelManager<T> modelManager, T root) {
		this.modelManager = modelManager;
		this.root = root;
		modelPath = new ArrayList<>();
	}

	public boolean addModel(Model model) {
		if (root == null) {
			root = (T) model;
			return true;
		}
		return modelPath.add(model);

	}

	public boolean popModel() {
		if (root != null) {
			if (modelPath.isEmpty()) {
				root = null;
			}
			modelPath.remove(modelPath.size() - 1);
			return true;
		}
		return false;
	}

	public void saveModel() {
		modelManager.saveModel(root);
	}

	public void deleteModel() {
		if (modelPath.isEmpty()) {
			modelManager.deleteModel(root);
		} else {
			saveModel();
		}
	}

	public Model getLast() {
		return modelPath.isEmpty() ? root : modelPath.get(modelPath.size() - 1);
	}
}
