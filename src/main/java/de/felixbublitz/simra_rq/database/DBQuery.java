package de.felixbublitz.simra_rq.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Represents an SQL Query as DBQuery object
 */

public class DBQuery {
    private ArrayList<String> where;
    private ArrayList<String> cells;
    private ArrayList<String> cellValues;

    private QueryType type;
    private String table;
    private Connection connection;

    enum QueryType{SELECT, UPDATE, DELETE, INSERT};

    /**
     * Creates a new DBQuery object
     * @param connection sql database connection
     */
    public DBQuery(Connection connection){
        this.connection = connection;
        where = new ArrayList<String>();
        cells = new ArrayList<String>();
        cellValues = new ArrayList<String>();
    }

    /**
     * Set Query method to delete
     * @param table table for deletion
     * @return DBQuery object
     */
    public DBQuery delete(String table){
        this.table = table;
        this.type = QueryType.DELETE;
        return this;
    }

    /**
     * Set Query method to insert
     * @param table table for insertion
     * @return DBQuery object
     */
    public DBQuery insert(String table){
        this.table = table;
        this.type = QueryType.INSERT;
        return this;
    }

    /**
     * Set Query method to update
     * @param table table for update
     * @return DBQuery object
     */
    public DBQuery update(String table){
        this.table = table;
        this.type = QueryType.UPDATE;
        return this;
    }

    /**
     * Set Query method to select
     * @param table table for select
     * @return DBQuery object
     */
    public DBQuery select(String table){
        this.table = table;
        this.type = QueryType.SELECT;
        return this;
    }

    /**
     * Set cell which is used in query
     * @param attr db row cell
     * @return DBQuery object
     */
    public DBQuery cell(String attr){
        this.cells.add(attr);
        return this;
    }

    /**
     * Set cells which are used in query
     * @param attr cells
     * @return DBQuery object
     */
    public DBQuery cells(String ... attr){
        for(int i=0; i< attr.length; i++)
            this.cells.add(attr[i]);
        return this;
    }

    /**
     * Set cell and value to set
     * @param attr cell
     * @param val value to set
     * @return DBQuery object
     */

    public DBQuery cell(String attr, Object val){
        this.cells.add(attr);
        this.cellValues.add("'"+val+"'");
        return this;
    }


    /**
     * Set where the query should happen
     * @param attr cell
     * @param val where cell = attr
     * @return DBQuery object
     */
    public DBQuery where(String attr, Object val){
        this.where.add(attr + "='" + val+"'");
        return this;
    }


    /**
     * Runs SQL query and returns last inserted row id
     * @return last row of queried table
     */
    public int query(){
            querySQL();
            return getLastKey();
    }

    /**
     * Runs SQL query and returns result
     * @return list of hashmap with results of query
     */
    public ArrayList<HashMap<String, String>> getResult(){
        return formatResult(querySQL());
    }

    /**
     * Exectue SQL query and returns sql result set
     * @return sql result set
     */
    private ResultSet querySQL(){
        ResultSet rs = null;

        try {
            Statement s = connection.createStatement();
            if(type == QueryType.SELECT) {
                rs = s.executeQuery(getSQL());
            }else{
                s.executeUpdate(getSQL());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;

    }

    /**
     * Takes resultset of query and generates list of result
     * @param rs resultset of query
     * @return list of hashmap of results
     */
    private ArrayList<HashMap<String, String>> formatResult(ResultSet rs) {
        try {
            ArrayList<HashMap<String, String>> out = new ArrayList<HashMap<String, String>>();
            ResultSetMetaData rsm = rs.getMetaData();
            while (rs.next()) {
                HashMap h = new HashMap();
                for (String a : cells) {
                    h.put(a, String.valueOf(rs.getObject(a)));
                }
                out.add(h);
            }
            return out;
        }catch (SQLException e){
            return null;
        }
    }

    /**
     * Get the sql statement string
     * @return sql statement string
     */
    private String getSQL(){
        switch (type){
            case SELECT:
                return getSelectRequest();
            case INSERT:
                return getInsertRequest();
            case UPDATE:
                return getUpdateRequest();
            case DELETE:
                return getDeleteRequest();
            default:
                return null;
        }
    };

    /**
     * Get select sql statement string
     * @return sql statement string
     */
    private String getSelectRequest(){
        return "SELECT "+ String.join(", ", cells) + " FROM "+ table +
                (where.size() == 0 ? "" : " WHERE ") + String.join(" and ", where);
    }

    /**
     * Get insert sql statement string
     * @return sql statement string
     */
    private String getInsertRequest(){
        return "INSERT INTO  "+ table + " (" +  String.join(", ", cells) + " )" + " VALUES (" + String.join(", ", cellValues) +" )";
    }

    /**
     * Get delete sql statement string
     * @return sql statement string
     */
    private String getDeleteRequest(){
        return "DELETE FROM "+ table  +
                (where.size() == 0 ? "" : " WHERE ") + String.join(" and ", where);

    }

    /**
     * Get update sql statement string
     * @return sql statement string
     */
    private String getUpdateRequest(){
        return "UPDATE "+ table + " SET " + getUpdateString()   +
                (where.size() == 0 ? "" : " WHERE ") + String.join(" and ", where);
    }


    /**
     * get sql update set string
     * @return sql update set string
     */
    private String getUpdateString(){
        String out = "";
        for(int i=0; i<cellValues.size();i++){
            if(i != 0)
                out+= ",";
            out += cells.get(i) + "=" + cellValues.get(i);
        }
        return out;
    }

    /**
     * Get id of last inserted row
     * @return last inserted row id
     */
    private int getLastKey(){
        Statement s = null;

        try {
            s = connection.createStatement();
            ResultSet rs = s.executeQuery("Select last_insert_rowid();");
            while(rs.next())
                return rs.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }
}
