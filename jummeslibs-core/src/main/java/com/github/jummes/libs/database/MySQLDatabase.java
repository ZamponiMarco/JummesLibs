package com.github.jummes.libs.database;

import com.github.jummes.libs.core.Libs;
import com.github.jummes.libs.model.IdentifiableModel;
import com.github.jummes.libs.model.Model;
import lombok.NonNull;
import org.apache.commons.lang.ClassUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class MySQLDatabase<T extends Model> extends Database<T> {

    private static final Object lock = new Object();

    private static volatile Connection connection;
    private static final Queue<Runnable> operations = new LinkedList<>();
    private static volatile int executorThread;

    public MySQLDatabase(@NonNull Class<T> classObject, @NonNull JavaPlugin plugin) {
        super(classObject, plugin);
        if (!ClassUtils.isAssignable(classObject, IdentifiableModel.class)) {
            throw new IllegalArgumentException();
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, this::openConnection);
    }

    @Override
    protected void openConnection() {
        synchronized (lock) {
            try {
                if (connection == null || connection.isClosed()) {
                    Class.forName("com.mysql.jdbc.Driver");
                    connection = DriverManager.getConnection("jdbc:mysql://localhost:" +
                                    plugin.getConfig().getString("sql.port") + "/" +
                                    plugin.getConfig().getString("sql.database"),
                            plugin.getConfig().getString("sql.user"),
                            plugin.getConfig().getString("sql.password"));
                }
                DatabaseMetaData dbm = connection.getMetaData();
                ResultSet tables = dbm.getTables(null, null, getTableName(), null);
                if (!tables.next()) {
                    String query = "CREATE TABLE " + getTableName() + " (id VARCHAR(36) PRIMARY KEY, OBJECT MEDIUMBLOB);";
                    connection.createStatement().execute(query);
                }
                if (executorThread == 0) {
                    executorThread = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::clearOperationQueue, 0, 600).getTaskId();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void clearOperationQueue() {
        while (!operations.isEmpty()) {
            operations.poll().run();
        }
    }

    @Override
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                clearOperationQueue();
                connection.close();
                Bukkit.getScheduler().cancelTask(executorThread);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void loadObjects(List<T> list, Runnable r) {
        operations.add(() -> loadEverything(list, r));
    }

    @Override
    public void saveObject(T object) {
        try {
            String query = "SELECT count(*) FROM " + getTableName() + " " + conditionQuery();
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, ((IdentifiableModel) object).getId().toString());
            ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
                final int count = result.getInt(1);
                if (count != 0) {
                    operations.add(() -> modifyObject(object));
                } else {
                    operations.add(() -> insertObject(object));
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    @Override
    public void deleteObject(T object) {
        operations.add(() -> deleteModel(object));
    }

    private String conditionQuery() {
        return "WHERE id=?;";
    }

    private String values() {
        return "(?,?)";
    }

    private String columnsList() {
        return " (id,OBJECT)";
    }

    private String getTableName() {
        return classObject.getSimpleName().toLowerCase();
    }

    private void loadEverything(List<T> list, Runnable r) {
        try {
            ResultSet result = connection.createStatement().executeQuery("SELECT * FROM " + getTableName());
            Bukkit.getScheduler().runTask(Libs.getPlugin(), () -> {
                try {
                    while (result.next()) {
                        ByteArrayInputStream b = new ByteArrayInputStream(Base64.getDecoder().decode(result.getString("OBJECT")));
                        BukkitObjectInputStream stream = new BukkitObjectInputStream(b);
                        T obj = (T) stream.readObject();
                        list.add(obj);
                    }
                    r.run();
                } catch (Exception throwables) {
                    throwables.printStackTrace();
                }
            });
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void deleteModel(Model object) {
        try {
            String query = "DELETE FROM " + getTableName() + " " + conditionQuery();
            PreparedStatement toReturn = connection.prepareStatement(query);
            toReturn.setString(1, ((IdentifiableModel) object).getId().toString());
            toReturn.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void modifyObject(Model object) {
        try {
            String query = "UPDATE " + getTableName() + " SET OBJECT=? " + conditionQuery();
            PreparedStatement toReturn = connection.prepareStatement(query);
            ByteArrayOutputStream s1 = new ByteArrayOutputStream();
            new BukkitObjectOutputStream(s1).writeObject(object);
            toReturn.setString(1, new String(Base64.getEncoder().encode(s1.toByteArray())));
            toReturn.setString(2, ((IdentifiableModel) object).getId().toString());
            toReturn.execute();
        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
        }
    }

    private void insertObject(Model object) {
        try {
            String query = "INSERT INTO " + getTableName() + columnsList() + " VALUES " + values() + ";";
            PreparedStatement toReturn = connection.prepareStatement(query);
            ByteArrayOutputStream s2 = new ByteArrayOutputStream();
            new BukkitObjectOutputStream(s2).writeObject(object);
            toReturn.setString(1, ((IdentifiableModel) object).getId().toString());
            toReturn.setString(2, new String(Base64.getEncoder().encode(s2.toByteArray())));
            toReturn.execute();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }
}
