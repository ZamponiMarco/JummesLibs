package com.github.jummes.libs.database;

import com.github.jummes.libs.model.IdentifiableModel;
import com.github.jummes.libs.model.Model;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.apache.commons.lang.ClassUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MySQLDatabase<T extends Model> extends Database<T> {

    private static Connection connection;
    private static Queue<SQLOperation> operations = new LinkedList<>();
    private static int executorThread;

    ExecutorService executor = Executors.newSingleThreadExecutor();


    public MySQLDatabase(@NonNull Class classObject, @NonNull JavaPlugin plugin) throws IllegalArgumentException {
        super(classObject, plugin);
        if (!ClassUtils.isAssignable(classObject, IdentifiableModel.class)) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    protected void openConnection() {
        synchronized (this) {
            try {
                if (connection != null && !connection.isClosed()) {
                    return;
                }
                Class.forName("com.mysql.jdbc.Driver");
                connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "password");
                executorThread = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::startOperationsCycle, 0, 300).getTaskId();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void startOperationsCycle() {
        try {
            System.out.println("Cleaning db actions");
            while (!operations.isEmpty()) {
                operations.poll().prepareStatement().execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                while (!operations.isEmpty()) {
                    operations.poll().prepareStatement().execute();
                }
                connection.close();
                Bukkit.getScheduler().cancelTask(executorThread);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public Future<List<T>> loadObjects() {
        return executor.submit(getCallable());
    }

    private Callable<List<T>> getCallable() {
        return () -> {
            try {
                List<T> list = new ArrayList<>();
                ResultSet result = connection.createStatement().executeQuery("SELECT * FROM " + getTableName());
                while (result.next()) {
                    ByteArrayInputStream b = new ByteArrayInputStream(result.getBytes("OBJECT"));
                    BukkitObjectInputStream stream = new BukkitObjectInputStream(b);
                    T obj = (T) stream.readObject();
                    list.add(obj);
                }
                return list;
            } catch (SQLException | IOException | ClassNotFoundException throwables) {
                throwables.printStackTrace();
            }
            return null;
        };
    }

    @Override
    public void saveObject(T object) {

        try {
            String query = "SELECT count(*) FROM " + getTableName() + " " + conditionQuery(object);
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, ((IdentifiableModel) object).getId().toString());
            ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
                final int count = result.getInt(1);
                if (count != 0) {
                    operations.removeIf(operation -> operation.getOperation().equals(Operation.MODIFY) && operation.getModel().equals(object));
                    operations.add(new SQLOperation(object, Operation.MODIFY));
                } else {
                    operations.removeIf(operation -> operation.getOperation().equals(Operation.INSERT) && operation.getModel().equals(object));
                    operations.add(new SQLOperation(object, Operation.INSERT));
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    @Override
    public void deleteObject(T object) {
        operations.removeIf(operation -> operation.getModel().equals(object));
        operations.add(new SQLOperation(object, Operation.DELETE));
    }

    private String conditionQuery(Model object) {
        return "WHERE id=?;";
    }

    private String getTableName() {
        return classObject.getSimpleName().toLowerCase();
    }

    public enum Operation {
        INSERT, MODIFY, DELETE;
    }

    @Setter
    @Getter
    @AllArgsConstructor
    private static class SQLOperation {

        private Model model;
        private Operation operation;

        public PreparedStatement prepareStatement() {
            String query;
            PreparedStatement toReturn = null;
            try {
                switch (operation) {
                    case DELETE:
                        query = "DELETE FROM " + getTableName() + " " + conditionQuery();
                        toReturn = connection.prepareStatement(query);
                        toReturn.setString(1, ((IdentifiableModel) model).getId().toString());
                        break;
                    case MODIFY:
                        query = "UPDATE " + getTableName() + " SET OBJECT=? " + conditionQuery();
                        toReturn = connection.prepareStatement(query);
                        ByteArrayOutputStream s1 = new ByteArrayOutputStream();
                        new BukkitObjectOutputStream(s1).writeObject(model);
                        toReturn.setBytes(1, s1.toByteArray());
                        toReturn.setString(2, ((IdentifiableModel) model).getId().toString());
                        break;
                    case INSERT:
                        query = "INSERT INTO " + getTableName() + columnsList() + " VALUES " + values() + ";";
                        toReturn = connection.prepareStatement(query);
                        ByteArrayOutputStream s2 = new ByteArrayOutputStream();
                        new BukkitObjectOutputStream(s2).writeObject(model);
                        toReturn.setString(1, ((IdentifiableModel) model).getId().toString());
                        toReturn.setBytes(2, s2.toByteArray());
                        break;
                }
            } catch (SQLException | IOException throwables) {
                throwables.printStackTrace();
            }
            System.out.println(toReturn);
            return toReturn;
        }

        private String getTableName() {
            return model.getClass().getSimpleName().toLowerCase();
        }

        private String values() {
            return "(?,?)";
        }

        private String conditionQuery() {
            return "WHERE id=?;";
        }

        private String columnsList() {
            return " (id,OBJECT)";
        }

    }
}
