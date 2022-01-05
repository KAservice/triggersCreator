package TriggersCreator.util;

import TriggersCreator.TriggersBody.*;
import com.sun.source.doctree.SeeTree;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class TriggersQueryExecutor {
    private static final Logger logger =  LogManager.getLogger();

    private TableProperties tableProperties;
    private DeleteTriggerFunctionCreator deleteTriggerFunctionCreator = new DeleteTriggerFunctionCreator();
    private InsertTriggerFunctionCreator insertTriggerFunctionCreator = new InsertTriggerFunctionCreator();
    private UpdateTriggerFunctionCreator updateTriggerFunctionCreator = new UpdateTriggerFunctionCreator();
    private InsertTriggerCreator insertTriggerCreator = new InsertTriggerCreator();
    private UpdateTriggerCreator updateTriggerCreator = new UpdateTriggerCreator();
    private DeleteTriggerCreator deleteTriggerCreator = new DeleteTriggerCreator();
    private DropAllTriggersCreator dropAllTriggersCreator = new DropAllTriggersCreator();



    //создание триггерных функций
    public void allTriggerFunctionsQuery(){
        tableProperties = new TableProperties();
        List<String> tableList = tableProperties.getListOfTables();
        int temp = 1;
        int size = tableList.size();

        try(Connection connection = new UtilConnection().getConnection()) {
            for (String tableName : tableList) {
                try {

                    String primaryKey = tableProperties.getPrimaryKey(tableName);


                    String insertTrigger = insertTriggerFunctionCreator.createFunction(tableName, primaryKey);
                    String updateTrigger = updateTriggerFunctionCreator.createFunction(tableName, primaryKey);
                    String deleteTrigger = deleteTriggerFunctionCreator.createFunction(tableName, primaryKey);
                    Statement statement = connection.createStatement();
                    statement.execute(insertTrigger);
                    statement.execute(updateTrigger);

                    statement.execute(deleteTrigger);
                    float percent = (float) temp++ / size * 100;
                    System.out.println(percent);
                }
                catch (SQLException ex){
                    logger.info("table name = {}", tableName, ex);
                    break;
                }
            }
        }
        catch (Exception ex){
            logger.error("error", ex);
        }

    }

    //создание триггеров
    public void allTriggersQuery(){
        List<String> tableList = new TableProperties().getListOfTables();
        int temp = 1;
        int size = tableList.size();

        try(Connection connection = new UtilConnection().getConnection()) {
            for (String tableName : tableList){
                String insertTriggerCommand = insertTriggerCreator.createTrigger(tableName);
                String updateTriggerCommand = updateTriggerCreator.createTrigger(tableName);
                String deleteTriggerCommand = deleteTriggerCreator.createTrigger(tableName);
                Statement statement = connection.createStatement();
                statement.execute(insertTriggerCommand);
                statement.execute(updateTriggerCommand);
                statement.execute(deleteTriggerCommand);


                float percent = (float) temp++ / size * 100;
                System.out.println(percent);
            }
        }
        catch (SQLException ex){
            logger.info("error", ex);
        }
    }

    //создание триггеров первичных ключей
    public void createPrimaryTriggers(){
        tableProperties = new TableProperties();
        List<String> tableList = new TableProperties().getFullListOfTables();


        try(Connection connection = new UtilConnection().getConnection();
            Statement statement = connection.createStatement()) {
            int temp = 1;
            int l = tableList.size();
            for (String tableName : tableList) {
                String primaryKey = tableProperties.getPrimaryKey(tableName);
                String triggerFunction = PrimatyKeyTriggerFunctionCreator.createFunction(tableName, primaryKey);
                String trigger = PrimaryKeyTriggerCreator.createTrigger(tableName);
                String dropTrigger = dropAllTriggersCreator.dropPrimaryTrigger(tableName);


                statement.execute(dropTrigger);
                statement.execute(triggerFunction);
                statement.execute(trigger);

                logger.info("процент создания триггеров первичных ключей = {}", (temp++ * 100)/l);
            }
        }
        catch (SQLException ex){
            logger.error("ошибка при создании триггеров первичного ключа", ex);
        }
    }

    //удаление триггеров
    public void dropAllTriggers(){
        List<String> tableList = new TableProperties().getListOfTables();
        try(Connection connection = new UtilConnection().getConnection()) {
            for (String tableName : tableList) {
                String dropDeleteTrigger = dropAllTriggersCreator.dropDeleteTrigger(tableName);
                String dropInsertTrigger = dropAllTriggersCreator.dropInsertTrigger(tableName);
                String dropUpdateTrigger = dropAllTriggersCreator.dropUpdateTrigger(tableName);

                Statement statement = connection.createStatement();
                statement.execute(dropDeleteTrigger);
                statement.execute(dropInsertTrigger);
                statement.execute(dropUpdateTrigger);
            }
        }
        catch (SQLException ex){
            logger.info("error", ex);
        }
    }


}
