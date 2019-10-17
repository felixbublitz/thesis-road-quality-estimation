package de.felixbublitz.simra_rq.database;

import javafx.util.Pair;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

public class DBQuery {
    private ArrayList<String> where;
    private ArrayList<String> cells;
    private ArrayList<String> cellValues;

    private QueryType type;
    private String table;
    private String[] order;
    private String limit;
    private Connection connection;

    enum QueryType{SELECT, UPDATE, DELETE, INSERT};


    public int query(){
            querySQL();
            return getLastKey();
    }

    public ArrayList<HashMap<String, String>> getResult(){
        return formatResult(querySQL());
    }


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

    private ArrayList<HashMap<String, String>> formatResult(ResultSet rs) {
        try {
            ArrayList<HashMap<String, String>> out = new ArrayList<HashMap<String, String>>();
            while (rs.next()) {
                HashMap h = new HashMap();
                for (String a : cells) {
                    h.put(a, rs.getString(a));
                }
                out.add(h);
            }
            return out;
        }catch (SQLException e){
            return null;
        }
    }


    public DBQuery(Connection connection){
        this.connection = connection;
        where = new ArrayList<String>();
        cells = new ArrayList<String>();
        cellValues = new ArrayList<String>();
    }

    public String getSQL(){
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

    private String getSelectRequest(){
        return "SELECT "+ String.join(", ", cells) + " FROM "+ table + " WHERE " + String.join(" and ", where);
    }

    private String getInsertRequest(){
        return "INSERT INTO  "+ table + " (" +  String.join(", ", cells) + " )" + " VALUES (" + String.join(", ", cellValues) +" )";
    }

    private String getDeleteRequest(){
        return "DELETE FROM "+ table + " WHERE " + String.join(" and ", where);

    }


    private String getUpdateRequest(){
        return "UPDATE "+ table + " SET " + getUpdateString()  + " WHERE " + String.join(" and ", where);
    }

    public DBQuery delete(String table){
        this.table = table;
        this.type = QueryType.DELETE;
        return this;
    }

    public DBQuery insert(String table){
        this.table = table;
        this.type = QueryType.INSERT;
        return this;
    }

    public DBQuery update(String table){
        this.table = table;
        this.type = QueryType.UPDATE;
        return this;
    }

    public DBQuery select(String table){
        this.table = table;
        this.type = QueryType.SELECT;
        return this;
    }

    public DBQuery cell(String attr){
        this.cells.add(attr);
        return this;
    }

    public DBQuery cells(String ... attr){
        for(int i=0; i< attr.length; i++)
            this.cells.add(attr[i]);
        return this;
    }

    public DBQuery cell(String attr, Object val){
        this.cells.add(attr);
        this.cellValues.add("'"+val+"'");
        return this;
    }


    public DBQuery where(String attr, Object val){
        this.where.add(attr + "='" + val+"'");
        return this;
    }

    private String getUpdateString(){
        String out = "";
        for(int i=0; i<cellValues.size();i++){
            if(i != 0)
                out+= ",";
            out += cells.get(i) + "=" + cellValues.get(i);
        }
        return out;
    }
}
