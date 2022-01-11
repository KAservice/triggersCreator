package TriggersCreator.TriggersBody;

import TriggersCreator.util.TableProperties;

import java.util.Map;

public class DeleteTriggerFunctionCreator {
    public String createFunction(String tableName, String nameFieldPk) {
        TableProperties tableProperties = new TableProperties();
        Map<String, String> fieldsMap = tableProperties.getMapOfColumns(tableName);
        int docType = tableProperties.getTypeTable(tableName);
        String nameFieldIdExtBase = "IDEXT_BASE_" + tableName;
        String nameFieldIdExtDataout = "IDEXT_DOUT_" + tableName;
        String nameFieldIdBase = "IDBASE_" + tableName;

        String result = "CREATE or REPLACE FUNCTION ADD_LOG_" + tableName + "_DEL ()\n" +
                "RETURNS trigger \n" +

                "LANGUAGE 'plpgsql' \n" +
                "    COST 100 \n" +
                "    VOLATILE PARALLEL UNSAFE \n" +
                "AS $BODY$\n" +
                "DECLARE  idXtism BIGINT ;\n" +
                "DECLARE  add_in_tism char ;\n" +
                "DECLARE  idext_base BIGINT ;\n" +
                "DECLARE  idext_data_out BIGINT ;\n";

        if (docType == 3){
            result += "DECLARE  add_in_tism_reg char ; \n";
        }

        result += "begin\n";
        result += "select VALUE_SETUP from SETUP where ID_SETUP=4 into add_in_tism; \n" +
                "if (add_in_tism='1') then   \n" +
                "else\n" +
                "   return null; /*не включено логгирование*/\n" +
                "end if; \n";
        if (docType == 3){
            result += "  select VALUE_SETUP from SETUP where ID_SETUP=6 into add_in_tism_reg; \n" +
                    "if (add_in_tism_reg='1') then   \n" +
                    "else\n" +
                    " return null;  /*не включено логгирование регистров*/ \n" +
                    "end if;\n";
        }

        result = result + "  \n" ;
        result = result + "  idXtism=nextval('GEN_XTISM_ID'); \n";
        result = result + "  \n" ;

        result = result + "           insert into xtism (id_xtism,  \n";
        result = result + "                       operation_xtism ,   /*операция 1 вставка 2 изменение 3 удаление 4 отмена 5 проведение*/    \n";
        result = result + "                       type_object_xtism , /*тип объекта 1 справочник 2 документ 3 регистр*/  \n ";
        result = result + "                       name_table_xtism ,  /*имя таблицы*/             \n ";
        result = result + "                       name_field_id_xtism , /*имя поля первичного ключа*/  \n ";
        result = result + "                       value_field_id_xtism ,/* значение первичного ключа*/  \n";
        result = result + "                       idbase_xtism)         /* id базы владельца, для фильтрации */   \n";

        result = result + "              values (idXtism,  \n";
        result = result + "                     3, \n ";
        result = result + "                    " + docType + ", \n";
        result = result + "                     '" + tableName + "', \n";
        result = result + "                     '" + nameFieldPk + "', \n";
        result = result + "                     old." + nameFieldPk + ", \n";
        result = result + "                     old." + nameFieldIdBase + "); \n";


        result = result + "	   \n ";

        for (Map.Entry<String, String> entry : fieldsMap.entrySet()) {
            String fieldName = entry.getKey();
            String fieldType = entry.getValue();
            if (fieldName.equalsIgnoreCase("IDEXT_BASE_" + tableName)
                    || fieldName.equalsIgnoreCase("IDEXT_DOUT_" + tableName)) {
                continue;
            }

            result = result + "if (not(old." + fieldName + " is null)) then  \n";
            result = result + "   insert into xtism_fields (idxtism_xtism_fields,  \n";
            result = result + "                             field_name_xtism_fields, \n";
            result = result + "                             old_value_xtism_fields, \n";
            result = result + "                             new_value_xtism_fields, \n";
            result = result + "                             type_xtism_fields) \n";

            result = result + "                     values (idXtism,  \n";
            result = result + "                             '" + fieldName + "',\n";

            if (TableProperties.checkTypeFieldLongString(fieldType)){
                //длинная строка
                result=result + "                             null, \n";
                result=result + "                             null, \n";
                result=result + "                             2); \n";      //тип поля
                result=result + "end if;";
            }
            else if (TableProperties.checkTypeFieldBlob(fieldType)){
                //BLOB поле
                result=result + "                             null, \n";
                result=result + "                             null, \n";
                result=result + "                             3); \n";      //тип поля
                result=result + "end if;";
            }
            else
            { //обычное поле
                result=result + "                             old." + fieldName + ", \n";
                result=result + "                             null, \n";
                result=result + "                             1); \n";    //тип поля
                result=result + "end if;";
            }

            result=result+"	   \n ";
        }
        result=result+"return new; \n";
        result=result+"end \n" +
                "$BODY$;" ;

        return result;
    }
}
