package com.patina.codebloom.common.db.helper;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class wraps around a {@link PreparedStatement} to allow named parameters
 * instead of indexed parameters. Example:
 *
 * this:
 *
 * <pre>
 * // Using PreparedStatement
 * Connection con = getConnection();
 * String query = "select * from my_table where name=? or address=?";
 * PreparedStatement p = con.prepareStatement(query);
 * p.setString(1, "bob");
 * p.setString(2, "123 terrace ct");
 * ResultSet rs = p.executeQuery();
 * </pre>
 *
 * can be replaced with
 *
 * <pre>
 * // Using NamedPreparedStatement
 * Connection con = getConnection();
 * String query = "select * from my_table where name=:name or address=:address";
 * NamedPreparedStatement p = new NamedPreparedStatement(con, query);
 * p.setString("name", "bob");
 * p.setString("address", "123 terrace ct");
 * ResultSet rs = p.executeQuery();
 * </pre>
 */
public class NamedPreparedStatement implements AutoCloseable {

    /** The statement this object is wrapping. */
    private final PreparedStatement statement;

    /** Maps parameter names to arrays of ints which are the parameter indices. */
    private Map<String, int[]> indexMap;

    /**
     * Creates a NamedPreparedStatement. Wraps a call to
     * c.{@link Connection#prepareStatement(java.lang.String) prepareStatement}.
     *
     * @param conn the database connection
     * @param sql the parameterized query
     * @throws SQLException if the statement could not be created
     */
    public NamedPreparedStatement(final Connection conn, final String sql)
        throws SQLException {
        String parsedQuery = parse(sql);
        statement = conn.prepareStatement(parsedQuery);
    }

    /**
     * Alternative way to create a NamedPreparedStatement.
     *
     * Creates a NamedPreparedStatement. Wraps a call to
     * c.{@link Connection#prepareStatement(java.lang.String) prepareStatement}.
     *
     * @param conn the database connection
     * @param sql the parameterized query
     * @throws SQLException if the statement could not be created
     */
    public static NamedPreparedStatement create(
        final Connection conn,
        final String sql
    ) throws SQLException {
        return new NamedPreparedStatement(conn, sql);
    }

    /**
     * Parses a query with named parameters. The parameter-index mappings are put
     * into the map, and the parsed query is returned. DO NOT CALL FROM CLIENT CODE.
     * This method is non-private so JUnit code can test it.
     *
     * @param query query to parse
     * @param paramMap map to hold parameter-index mappings
     * @return the parsed query
     */
    final String parse(final String query) {
        int length = query.length();
        StringBuilder parsedQuery = new StringBuilder(length);
        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;
        int index = 1;
        HashMap<String, List<Integer>> indexes = new HashMap<
            String,
            List<Integer>
        >();

        for (int i = 0; i < length; i++) {
            char c = query.charAt(i);
            if (inSingleQuote) {
                if (c == '\'') {
                    inSingleQuote = false;
                }
            } else if (inDoubleQuote) {
                if (c == '"') {
                    inDoubleQuote = false;
                }
            } else {
                if (c == '\'') {
                    inSingleQuote = true;
                } else if (c == '"') {
                    inDoubleQuote = true;
                } else if (
                    c == ':' &&
                    i + 1 < length &&
                    Character.isJavaIdentifierStart(query.charAt(i + 1))
                ) {
                    int j = i + 2;
                    while (
                        j < length &&
                        Character.isJavaIdentifierPart(query.charAt(j))
                    ) {
                        j++;
                    }
                    String name = query.substring(i + 1, j);
                    c = '?'; // replace the parameter with a question mark
                    i += name.length(); // skip past the end if the parameter

                    List<Integer> indexList = indexes.get(name);
                    if (indexList == null) {
                        indexList = new ArrayList<Integer>();
                        indexes.put(name, indexList);
                    }
                    indexList.add(Integer.valueOf(index));

                    index++;
                }
            }
            parsedQuery.append(c);
        }

        indexMap = new HashMap<String, int[]>(indexes.size());
        // replace the lists of Integer objects with arrays of ints
        for (Map.Entry<String, List<Integer>> entry : indexes.entrySet()) {
            List<Integer> list = entry.getValue();
            int[] intIndexes = new int[list.size()];
            int i = 0;
            for (Integer x : list) {
                intIndexes[i++] = x.intValue();
            }
            indexMap.put(entry.getKey(), intIndexes);
        }

        return parsedQuery.toString();
    }

    /**
     * Returns the indexes for a parameter.
     *
     * @param name parameter name
     * @return parameter indexes
     * @throws IllegalArgumentException if the parameter does not exist
     */
    private int[] getIndexes(final String name) {
        int[] indexes = indexMap.get(name);
        if (indexes == null) {
            throw new IllegalArgumentException("Parameter not found: " + name);
        }
        return indexes;
    }

    /**
     * Sets a parameter.
     *
     * @param name parameter name
     * @param value parameter value
     * @throws SQLException if an error occurred
     * @throws IllegalArgumentException if the parameter does not exist
     * @see PreparedStatement#setObject(int, java.lang.Object)
     */
    public void setObject(final String name, final Object value)
        throws SQLException {
        int[] indexes = getIndexes(name);
        for (int i = 0; i < indexes.length; i++) {
            statement.setObject(indexes[i], value);
        }
    }

    /**
     * Sets a parameter.
     *
     * @param name parameter name
     * @param value parameter value
     * @param targetSqlType the SQL type to be sent to the database
     * @throws SQLException if an error occurred
     * @throws IllegalArgumentException if the parameter does not exist
     * @see PreparedStatement#setObject(int, java.lang.Object)
     */
    public void setObject(
        final String name,
        final Object value,
        final SQLType targetSqlType
    ) throws SQLException {
        int[] indexes = getIndexes(name);
        for (int i = 0; i < indexes.length; i++) {
            statement.setObject(indexes[i], value, targetSqlType);
        }
    }

    /**
     * Sets a parameter.
     *
     * @param name parameter name
     * @param value parameter value
     * @param targetSqlType the SQL type (as defined in java.sql.Types) to be sent
     * to the database
     * @throws SQLException if an error occurred
     * @throws IllegalArgumentException if the parameter does not exist
     * @see PreparedStatement#setObject(int, java.lang.Object)
     */
    public void setObject(
        final String name,
        final Object value,
        final int targetSqlType
    ) throws SQLException {
        int[] indexes = getIndexes(name);
        for (int i = 0; i < indexes.length; i++) {
            statement.setObject(indexes[i], value, targetSqlType);
        }
    }

    /**
     * Sets a parameter.
     *
     * @param name parameter name
     * @param value parameter value
     * @throws SQLException if an error occurred
     * @throws IllegalArgumentException if the parameter does not exist
     * @see PreparedStatement#setString(int, java.lang.String)
     */
    public void setString(final String name, final String value)
        throws SQLException {
        int[] indexes = getIndexes(name);
        for (int i = 0; i < indexes.length; i++) {
            statement.setString(indexes[i], value);
        }
    }

    /**
     * Sets a parameter.
     *
     * @param name parameter name
     * @param value parameter value
     * @throws SQLException if an error occurred
     * @throws IllegalArgumentException if the parameter does not exist
     * @see PreparedStatement#setBoolean(int, java.lang.Boolean)
     */
    public void setBoolean(final String name, final Boolean value)
        throws SQLException {
        int[] indexes = getIndexes(name);
        for (int i = 0; i < indexes.length; i++) {
            statement.setBoolean(indexes[i], value);
        }
    }

    /**
     * Sets a parameter.
     *
     * @param name parameter name
     * @param value parameter value
     * @throws SQLException if an error occurred
     * @throws IllegalArgumentException if the parameter does not exist
     * @see PreparedStatement#setArray(int, java.sql.Array)
     */
    public void setArray(final String name, final Array value)
        throws SQLException {
        int[] indexes = getIndexes(name);
        for (int i = 0; i < indexes.length; i++) {
            statement.setArray(indexes[i], value);
        }
    }

    /**
     * Sets a parameter.
     *
     * @param name parameter name
     * @param value parameter value
     * @throws SQLException if an error occurred
     * @throws IllegalArgumentException if the parameter does not exist
     * @see PreparedStatement#setInt(int, int)
     */
    public void setInt(final String name, final int value) throws SQLException {
        int[] indexes = getIndexes(name);
        for (int i = 0; i < indexes.length; i++) {
            statement.setInt(indexes[i], value);
        }
    }

    /**
     * Sets a parameter.
     *
     * @param name parameter name
     * @param value parameter value
     * @throws SQLException if an error occurred
     * @throws IllegalArgumentException if the parameter does not exist
     * @see PreparedStatement#setInt(int, int)
     */
    public void setLong(final String name, final long value)
        throws SQLException {
        int[] indexes = getIndexes(name);
        for (int i = 0; i < indexes.length; i++) {
            statement.setLong(indexes[i], value);
        }
    }

    /**
     * Sets a parameter.
     *
     * @param name parameter name
     * @param value parameter value
     * @throws SQLException if an error occurred
     * @throws IllegalArgumentException if the parameter does not exist
     * @see PreparedStatement#setTimestamp(int, java.sql.Timestamp)
     */
    public void setTimestamp(final String name, final Timestamp value)
        throws SQLException {
        int[] indexes = getIndexes(name);
        for (int i = 0; i < indexes.length; i++) {
            statement.setTimestamp(indexes[i], value);
        }
    }

    /**
     * Sets a parameter.
     *
     * @param name parameter name
     * @param targetSqlType the SQL type (as defined in java.sql.Types) to be sent
     * to the database
     * @throws SQLException if an error occurred
     * @throws IllegalArgumentException if the parameter does not exist
     * @see PreparedStatement#setNull(int, int)
     */
    public void setNull(final String name, final int targetSqlType)
        throws SQLException {
        int[] indexes = getIndexes(name);
        for (int i = 0; i < indexes.length; i++) {
            statement.setNull(indexes[i], targetSqlType);
        }
    }

    /**
     * Returns the underlying statement.
     *
     * @return the statement
     */
    public PreparedStatement getStatement() {
        return statement;
    }

    /**
     * Executes the statement.
     *
     * @return true if the first result is a {@link ResultSet}
     * @throws SQLException if an error occurred
     * @see PreparedStatement#execute()
     */
    public boolean execute() throws SQLException {
        return statement.execute();
    }

    /**
     * Executes the statement, which must be a query.
     *
     * @return the query results
     * @throws SQLException if an error occurred
     * @see PreparedStatement#executeQuery()
     */
    public ResultSet executeQuery() throws SQLException {
        return statement.executeQuery();
    }

    /**
     * Executes the statement, which must be an SQL INSERT, UPDATE or DELETE
     * statement; or an SQL statement that returns nothing, such as a DDL statement.
     *
     * @return number of rows affected
     * @throws SQLException if an error occurred
     * @see PreparedStatement#executeUpdate()
     */
    public int executeUpdate() throws SQLException {
        return statement.executeUpdate();
    }

    /**
     * Closes the statement.
     *
     * @throws SQLException if an error occurred
     * @see Statement#close()
     */
    public void close() throws SQLException {
        statement.close();
    }

    /**
     * Adds the current set of parameters as a batch entry.
     *
     * @throws SQLException if something went wrong
     */
    public void addBatch() throws SQLException {
        statement.addBatch();
    }

    /**
     * Executes all of the batched statements.
     *
     * See {@link Statement#executeBatch()} for details.
     *
     * @return update counts for each statement
     * @throws SQLException if something went wrong
     */
    public int[] executeBatch() throws SQLException {
        return statement.executeBatch();
    }
}
