package itacademy.domain.dao.impl;

import itacademy.connection.ConnectionManager;
import itacademy.domain.dao.interfaces.TaskDao;
import itacademy.domain.entity.SystemUser;
import itacademy.domain.entity.Listener;
import itacademy.domain.entity.Task;
import itacademy.domain.entity.TypeOfJob;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TaskDaoImpl implements TaskDao {
    private static final Object LOCK = new Object();
    private static TaskDaoImpl INSTANCE = null;

    public static TaskDaoImpl getInstance() {
        if (INSTANCE == null) {
            synchronized (LOCK) {
                if (INSTANCE == null) {
                    INSTANCE = new TaskDaoImpl();
                }
            }
        }
        return INSTANCE;
    }

    private Task createTaskDaoFromResultSet(ResultSet resultSet) throws SQLException {
        return new Task(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getString("text"),
                new TypeOfJob(
                        resultSet.getLong("id"),
                        resultSet.getString("name")),
                new Listener(
                        resultSet.getLong("id"),
                        resultSet.getString("name")),
                new SystemUser(
                        resultSet.getLong("su_id")),
                new SystemUser(
                        resultSet.getLong("op_id")),
                new SystemUser(
                        resultSet.getLong("ex_id")));
    }

    @Override
    public Long save(Task entity) {
        return null;
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM tasks WHERE (id = ?)";
        try (Connection connection = ConnectionManager.getConnection()) {
            try (PreparedStatement preparedStatement = (connection.prepareStatement(sql))) {
                preparedStatement.setLong(1, id);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Task> findAll() {
        List<Task> taskList = new ArrayList<>();
        try (Connection connection = ConnectionManager.getConnection()) {
            String sql = "select\n" +
                    "  ts.id,\n" +
                    "  ts.name,\n" +
                    "  p.id        as p_id,\n" +
                    "  p.name      as p_name,\n" +
                    "  ts.text,\n" +
                    "  su.id       as su_id,\n" +
                    "  su.name     as su_name,\n" +
                    "  su.family   as su_family,\n" +
                    "  su.e_mail   as su_email,\n" +
                    "  su.password as su_pass,\n" +
                    "  su.branch_id,\n" +
                    "  su.subdivision_id,\n" +
                    "  ts.executor_id as ex_id,\n" +
                    "  ts.operator_id as op_id\n" +
                    "from tasks ts, privileges p, system_users su\n" +
                    "where ts.listener_id = p.id\n" +
                    "      and ts.system_user_id = su.id;";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        taskList.add(createTaskDaoFromResultSet(resultSet));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return taskList;
    }

//    @Override
//    public Long save(SystemUser entity) {
//        Long id = 0L;
//        String sql = "INSERT INTO system_users (name, family, e_mail, password, branch_id, subdivision_id)\n" +
//                "VALUES (?, ?, ?, ?, ?, ?);";
//        try (Connection connection = ConnectionManager.getConnection()) {
//            try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
//                preparedStatement.setString(1, entity.getName());
//                preparedStatement.setString(2, entity.getFamaly());
//                preparedStatement.setString(3, entity.getEmail());
//                preparedStatement.setString(4, entity.getPassword());
//                preparedStatement.setLong(5, entity.getBranch().getId());
//                preparedStatement.setLong(6, entity.getSubdivision().getId());
//                preparedStatement.executeUpdate();
//                ResultSet resultSet = preparedStatement.getGeneratedKeys();
//                if (resultSet.next()) {
//                    id = resultSet.getLong("id");
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return id;
//    }

    @Override
    public Optional<Task> findById(Long id) {
        return Optional.empty();
    }
}