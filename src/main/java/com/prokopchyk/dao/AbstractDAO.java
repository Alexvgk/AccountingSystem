package com.prokopchyk.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public abstract class AbstractDAO<T> implements AutoCloseable {
    protected Connection connection;

    public AbstractDAO(Connection connection){
        this.connection = connection;
    }

    public void setConnection(Connection connection){
        this.connection = connection;
    }
    public abstract List<T> findAll() throws DAOException, SQLException;

    public abstract Optional<T> findById (int id) throws DAOException;

    public abstract boolean delete(int id) throws DAOException;

    public abstract boolean create(T t) throws DAOException;

    public abstract T update(T t) throws DAOException;

    @Override
    public void close() throws Exception {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            }
            catch (SQLException e) {
                throw new DAOException("Failed to close the connection", e);
            }
            finally {
                connection = null;
            }
        }
    }
}
