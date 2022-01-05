package TriggersCreator.TriggersBody;

public class DeleteTriggerCreator {
    public String createTrigger(String tableName) {
        String result = "CREATE TRIGGER ADD_LOG_" + tableName + "_DEL " +
                "AFTER DELETE ON " + tableName + "\n" +
                "FOR EACH ROW \n" +
                "EXECUTE PROCEDURE ADD_LOG_" + tableName + "_DEL();";
        return result;
    }
}
