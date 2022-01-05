package TriggersCreator.TriggersBody;

public class InsertTriggerCreator {
    public String createTrigger(String tableName){
        String result = "CREATE TRIGGER ADD_LOG_" + tableName + "_INS AFTER INSERT ON " + tableName + "\n" +
                "FOR EACH ROW \n" +
                "EXECUTE PROCEDURE ADD_LOG_" + tableName + "_INS();";
        return result;
    }
}
