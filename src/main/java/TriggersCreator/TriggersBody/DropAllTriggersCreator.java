package TriggersCreator.TriggersBody;

public class DropAllTriggersCreator {

    public String dropDeleteTrigger(String tableName){
        String command = "DROP TRIGGER IF EXISTS " + "ADD_LOG_" + tableName + "_DEL "
                + "ON " + tableName + ";";
        return command;
    }

    public String dropInsertTrigger(String tableName){
        String command = "DROP TRIGGER IF EXISTS " + "ADD_LOG_" + tableName + "_INS "
                + "ON " + tableName + ";";
        return command;
    }

    public String dropUpdateTrigger(String tableName){
        String command = "DROP TRIGGER IF EXISTS " + "ADD_LOG_" + tableName + "_UPD "
                + "ON " + tableName + ";";
        return command;
    }

    public String dropPrimaryTrigger(String tableName){
        String command = "DROP TRIGGER IF EXISTS " + tableName + "_BI "
                + "ON " + tableName + ";";
        return command;
    }
}
