package TriggersCreator.TriggersBody;

public class PrimaryKeyTriggerCreator {
    public static String createTrigger(String tableName) {
        String result = "CREATE TRIGGER " + tableName + "_BI BEFORE INSERT ON " + tableName + "\n" +
                "FOR EACH ROW \n" +
                "EXECUTE PROCEDURE " + tableName + "_BI();";
        return result;
    }
}