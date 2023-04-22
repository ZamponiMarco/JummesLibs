package com.github.jummes.libs.model;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.ToString;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to describe the path of models that contains other models in
 * it, the method update model will call the updateModel method of the root
 * modelManager
 *
 * @param <T>
 * @author Marco
 */
@ToString
@Getter
public class ModelPath<T extends NamedModel> implements Cloneable {

    @Getter
    private ModelManager<T> modelManager;
    @Getter
    private T root;

    private final List<Model> modelPath;

    protected ModelPath(ModelManager<T> modelManager, T root, List<Model> modelPath) {
        this.modelManager = modelManager;
        this.root = root;
        this.modelPath = modelPath;
    }

    public ModelPath(ModelManager<T> modelManager, T root) {
        this.modelManager = modelManager;
        this.root = root;
        this.modelPath = new ArrayList<>();
    }

    public boolean addModel(Model model) {
        if (root == null) {
            root = (T) model;
            return true;
        }
        modelPath.add(model);
        return true;
    }

    public boolean popModel() {
        if (root != null) {
            if (modelPath.isEmpty()) {
                root = null;
            } else {
                modelPath.remove(modelPath.size() - 1);
            }
            return true;
        }
        return false;
    }

    public void saveModel(Field field) {
        Lists.reverse(modelPath).forEach(model -> model.onModify(field));
        root.onModify(field);
        modelManager.saveModel(root);
    }

    public void saveModel() {
        saveModel(null);
    }

    public void deleteRoot(Model model) {
        addModel(model);
        deleteLastPathModel();
        popModel();
    }

    public void deleteLastPathModel() {
        if (modelPath.isEmpty()) {
            modelManager.deleteModel(root);
            root.onRemoval();
        } else {
            saveModel();
        }
    }

    public Model getLast() {
        return modelPath.isEmpty() ? root : modelPath.get(modelPath.size() - 1);
    }

    @Override
    public ModelPath<T> clone() {
        return new ModelPath<>(modelManager, root, new ArrayList<>(modelPath));
    }
}
