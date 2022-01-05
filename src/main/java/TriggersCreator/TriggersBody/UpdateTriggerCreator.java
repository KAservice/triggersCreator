package TriggersCreator.TriggersBody;

public class UpdateTriggerCreator {
    public String createTrigger(String tableName){
        String result = "CREATE TRIGGER ADD_LOG_" + tableName + "_UPD " +
                "AFTER UPDATE ON " + tableName + "\n" +
                "FOR EACH ROW \n" +
                "EXECUTE PROCEDURE ADD_LOG_" + tableName + "_UPD();";
        return  result;
    }
}
