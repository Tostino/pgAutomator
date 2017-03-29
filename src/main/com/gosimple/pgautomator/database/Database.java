/*
 * Copyright (c) 2017, Adam Brusselback
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.gosimple.pgautomator.database;


import com.gosimple.pgautomator.Config;
import org.postgresql.ds.PGSimpleDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public enum Database
{
    INSTANCE;

    private final PGSimpleDataSource data_source;
    private Connection main_connection;

    Database()
    {
        data_source = new PGSimpleDataSource();
        data_source.setServerName(Config.INSTANCE.db_host);
        data_source.setPortNumber(Config.INSTANCE.db_port);
        data_source.setDatabaseName(Config.INSTANCE.db_database);
        data_source.setUser(Config.INSTANCE.db_user);
        data_source.setPassword(Config.INSTANCE.db_password);
        data_source.setApplicationName("pgAutomator: " + Config.INSTANCE.hostname);
    }

    /**
     * Returns the main connection used for all pgAutomator upkeep.
     *
     * @return
     */
    public Connection getMainConnection() throws SQLException
    {
        if (main_connection == null)
        {
            resetMainConnection();
        }
        return main_connection;
    }

    /**
     * Closes existing connection if necessary, and creates a new connection.
     */
    public void resetMainConnection()
    {
        try
        {
            if (main_connection != null)
            {
                main_connection.close();
            }
            main_connection = Database.INSTANCE.getConnection(Config.INSTANCE.db_host, Config.INSTANCE.db_database);
        }
        catch (final SQLException e)
        {
            Config.INSTANCE.logger.error(e.getMessage());
            main_connection = null;
        }
    }

    /**
     * Returns a connection to the specified database with autocommit on.
     *
     * @param host_name
     * @param database
     * @return
     * @throws SQLException
     */
    public synchronized Connection getConnection(final String host_name, final String database) throws SQLException
    {
        data_source.setDatabaseName(database);
        data_source.setServerName(host_name);

        return data_source.getConnection();
    }

    /**
     * Returns a connection to the specified database with user and pass with autocommit on.
     *
     * @param host_name
     * @param database
     * @param user
     * @param password
     * @return
     * @throws SQLException
     */
    public synchronized Connection getConnection(final String host_name, final String database, final String user, final String password) throws SQLException
    {
        data_source.setDatabaseName(database);
        data_source.setServerName(host_name);

        return data_source.getConnection(user, password);
    }
}
