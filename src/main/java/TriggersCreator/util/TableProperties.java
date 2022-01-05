package TriggersCreator.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.*;

public class TableProperties {

    private static final Logger logger =  LogManager.getLogger();
    //получаем список таблиц
    public List getListOfTables(){
        List<String> listOfTables = new ArrayList<>();

        try(Connection connection = new UtilConnection().getConnection()) {
            Statement statement = connection.createStatement();

            String command = "SELECT table_name FROM information_schema.tables\n" +
                    "WHERE table_schema NOT IN ('information_schema','pg_catalog');";
            ResultSet resultSet = statement.executeQuery(command);

            while (resultSet.next()){
                String tableName = resultSet.getString("table_name");
                if (checkTableForTrigger(tableName)){
                    listOfTables.add(tableName);
                }
            }
        }
        catch (SQLException exception){
            exception.printStackTrace();
        }

        return listOfTables;
    }

    //список всех таблиц без проверки
    public List<String> getFullListOfTables(){
        List<String> listOfTables = new ArrayList<>();

        try(Connection connection = new UtilConnection().getConnection()) {
            Statement statement = connection.createStatement();

            String command = "SELECT table_name FROM information_schema.tables\n" +
                    "WHERE table_schema NOT IN ('information_schema','pg_catalog');";
            ResultSet resultSet = statement.executeQuery(command);

            while (resultSet.next()){
                String tableName = resultSet.getString("table_name");

                listOfTables.add(tableName);
            }
        }
        catch (SQLException exception){
            exception.printStackTrace();
        }
        return listOfTables;
    }

    //получаем список полей и их типов у конкретной таблицы
    public Map getMapOfColumns(String tableName){
        Map<String, String> mapOfColumn = new HashMap<>();
        String columnName;
        String columnType;

        try(Connection connection = new UtilConnection().getConnection()) {
            Statement statement = connection.createStatement();
            String command = "select column_name,domain_name \n" +
                    "from information_schema.columns \n" +
                    "where table_name = " + "'" + tableName + "';";
            ResultSet resultSet = statement.executeQuery(command);
            while (resultSet.next()){
                columnName = resultSet.getString("column_name");
                columnType = resultSet.getString("domain_name");

                mapOfColumn.put(columnName, columnType);
            }
        }
        catch (SQLException ex){
            ex.printStackTrace();
        }

        return mapOfColumn;
    }

    //определяем тип документа
    public int getTypeTable(String tableName){
        int result = 0;
        String typeOfColumn;

        Map<String, String> mapOfColumns = getMapOfColumns(tableName);
        typeOfColumn = mapOfColumns.get("type_tbl_" + tableName);
        if (typeOfColumn == null){
            logger.error("table name = {}", tableName);
            throw new IllegalArgumentException("в таблице нет поля ");
        }
        if (typeOfColumn.equalsIgnoreCase("DOMAIN_TYPE_TBL_SPR")){
            result = 1;
        }
        else if (typeOfColumn.equalsIgnoreCase("DOMAIN_TYPE_TBL_DOC")){
            result = 2;
        }
        else if (typeOfColumn.equalsIgnoreCase("DOMAIN_TYPE_TBL_REG")){
            result = 3;
        }
        return result;
    }

    //проверяем что тип поля длинная строка
    public static boolean checkTypeFieldLongString(String domainName){

        if(domainName == null){
            return false;
        }
        if (domainName.equalsIgnoreCase("DOMAIN_TEXT_1500")){
            return true;
        }
        else if (domainName.equalsIgnoreCase("DOMAIN_TEXT_500")){
            return true;
        }
        else if (domainName.equalsIgnoreCase("DOMAIN_STR_PAR")){
            return true;
        }
        else if (domainName.equalsIgnoreCase("DOMAIN_ZAPROS")){
            return true;
        }
        else
            return false;
    }

    //проверяем что поле blob
    public static boolean checkTypeFieldBlob(String domainName){
        if (domainName == null){
            return false;
        }
        if (domainName.equalsIgnoreCase("DOMAIN_IMAGE")){
            return true;
        }
        else if (domainName.equalsIgnoreCase("DOMAIN_IMAGE_SMALL")){
            return true;
        }
        else if (domainName.equalsIgnoreCase("DOMAIN_EXTMODULE_BLOB")){
            return true;
        }
        else return false;
    }

    //проверяем логгируется ли таблица
    private boolean checkTableForTrigger(String tableName){
        if (tableName.equalsIgnoreCase("XDATA_OUT") || tableName.equalsIgnoreCase("XFIELD_BASE") ||
                tableName.equalsIgnoreCase("XMESSAGE_OBMEN") || tableName.equalsIgnoreCase("XTABLE_BASE") ||
                tableName.equalsIgnoreCase("XTISM") || tableName.equalsIgnoreCase("XTISM_FIELDS") ||
                tableName.equalsIgnoreCase("SYSTEM") || tableName.equalsIgnoreCase("XSETUP_OBMEN") ||
                tableName.equalsIgnoreCase("XBASE_FOR_OBMEN") || tableName.equalsIgnoreCase("XTPRICE_FOR_OBMEN") ||
                tableName.equalsIgnoreCase("VT_OST_NA_SKLADE") || tableName.equalsIgnoreCase("VT_OST_PO_REV") ||
                tableName.equalsIgnoreCase("GURNALPLAT") || tableName.equalsIgnoreCase("LOG") ||
                tableName.equalsIgnoreCase("RGZAK") || tableName.equalsIgnoreCase("RGZAKT") ||
                tableName.equalsIgnoreCase("SETUP")){
            return false;
        }
        else
            return true;
    }

    public String getPrimaryKey(String tableName){
        String result = "";
        Map<String, String> tableMap = getMapOfColumns(tableName);
        for(Map.Entry<String, String> entry : tableMap.entrySet()){
            String fieldName = entry.getKey();
            String fieldType = entry.getValue();
            if (fieldType != null && fieldType.equalsIgnoreCase("domain_idtable")){
                result = fieldName;
            }
        }
        return result;
    }


}
