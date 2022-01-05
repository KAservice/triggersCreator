package TriggersCreator.TriggersBody;

public class PrimatyKeyTriggerFunctionCreator {
    public static String createFunction(String tableName, String nameFieldPk){
        String query = "CREATE or REPLACE FUNCTION " + tableName + "_BI ()\n" +
                "RETURNS trigger \n" +

                "LANGUAGE 'plpgsql' \n" +
                "    COST 100 \n" +
                "    VOLATILE PARALLEL UNSAFE \n" +
                "AS $BODY$\n" +
                "begin \n" +
                "IF (NEW." + nameFieldPk + " IS NULL) THEN\n" +
                "    NEW." + nameFieldPk + " = nextval('GEN_" + tableName + "_ID'); \n" +
                "end if; \n" +
                "return new; \n" +
                "end \n" +
                "$BODY$;";

        return query;
    }

    public static void main(String[] args) {
        System.out.println(new PrimaryKeyTriggerCreator().createTrigger("xtism"));
        System.out.println("--------------------");
        System.out.println(new PrimatyKeyTriggerFunctionCreator().createFunction("xtism", "id_xtism"));
    }
}
