package TriggersCreator.TriggersBody;

import TriggersCreator.util.TableProperties;

import java.util.Map;

public class UpdateTriggerFunctionCreator {
    public String createFunction(String tableName, String nameFieldPk){
        TableProperties tableProperties = new TableProperties();
        Map<String, String> fieldsMap = tableProperties.getMapOfColumns(tableName);
        int docType = tableProperties.getTypeTable(tableName);
        String nameFieldIdExtBase = "IDEXT_BASE_" + tableName;
        String nameFieldIdExtDataout = "IDEXT_DOUT_" + tableName;
        String nameFieldIdBase = "IDBASE_" + tableName;

        String result = "CREATE or REPLACE FUNCTION ADD_LOG_" + tableName + "_UPD ()\n" +
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
        if (tableName.equalsIgnoreCase("GALLDOC") || tableName.equalsIgnoreCase("REM_GALLDOC") ||
                tableName.equalsIgnoreCase("HOT_GALLDOC") || tableName.equalsIgnoreCase("HOT_GALLDOC")){
            result = result + "if (current_user='DVREGDOC_USER') then  \n";
            result = result + "      /*не надо логгировать*/\n";
            result = result + "       return null;  /*не надо логгировать*/ \n";
            result = result + "     end if;\n";
        }

        result = result + "   idext_base=0; \n";
        result = result + "   idext_data_out=0; \n";
        result = result + "if (current_user='EXTUSER') then  \n";
        result = result + "      /*внешнее изменение*/\n";
        result = result + "          idext_base=new." + nameFieldIdExtBase + "; \n";
        result = result + "          idext_data_out=new." + nameFieldIdExtDataout + "; \n";
        result = result + "     end if;\n";

        result = result + "  \n" ;
        result = result + "  idXtism=nextval('GEN_XTISM_ID'); \n";
        result = result + "  \n" ;


        result = result+"           insert into xtism (id_xtism,  \n";
        result = result+"                       operation_xtism ,   /*операция 1 вставка 2 изменение 3 удаление 4 отмена 5 проведение*/    \n";
        result = result+"                       type_object_xtism , /*тип объекта 1 справочник 2 документ 3 регистр*/  \n ";
        result = result+"                       name_table_xtism ,  /*имя таблицы*/             \n ";
        result = result+"                       name_field_id_xtism , /*имя поля первичного ключа*/  \n ";
        result = result+"                       value_field_id_xtism ,/* значение первичного ключа*/  \n";
        result = result+"                       idbase_xtism,         /* id базы владельца, для фильтрации */   \n";
        result = result+"                       idext_base_xtism,         /* id внешней базы для квитанции */   \n";
        result = result+"                       idext_dataout_xtism)         /* id_data_out для квитанции при внешнем обновлении */   \n";

        result = result+"              values (idXtism,  \n";
        result = result+"                     2, \n ";
        result = result+"                    " + docType + ", \n";
        result = result+"                     '" + tableName + "', \n";
        result = result+"                     '" + nameFieldPk + "', \n";
        result = result+"                     new." + nameFieldPk + ", \n";
        result = result+"                     new." + nameFieldIdBase + ", \n";
        result = result+"                     idext_base, \n";
        result = result+"                     idext_data_out); \n";

        result = result+"	   \n ";

        for (Map.Entry<String, String> entry : fieldsMap.entrySet()) {
            String fieldName = entry.getKey();
            String fieldType = entry.getValue();

            if(fieldName.equalsIgnoreCase("IDEXT_BASE_" + tableName)
                    || fieldName.equalsIgnoreCase("IDEXT_DOUT_" + tableName)){
                continue;
            }

            result=result+"if ((old."+fieldName+" is null and new."+fieldName+" is not null) or  \n";
            result=result+"  (new."+fieldName+" is null and old."+fieldName+" is not null) or  \n";
            result=result+"  (new."+fieldName+" is not null and old."+fieldName+" is not null and  \n";
            result=result+"   new."+fieldName+" <> old."+fieldName+" )) then  \n";

            result=result+"   insert into xtism_fields (idxtism_xtism_fields,  \n";
            result=result+"                             field_name_xtism_fields, \n";
            result=result+"                             old_value_xtism_fields, \n";
            result=result+"                             new_value_xtism_fields, \n";
            result=result+"                             type_xtism_fields) \n";
            result=result+"                     values (idXtism,  \n";
            result=result+"                             '"+fieldName+"',\n";

            //старое значение
            if (TableProperties.checkTypeFieldLongString(fieldType)){
                //длинная строка
                result=result+"                             null, \n";
            }
            else if (TableProperties.checkTypeFieldBlob(fieldType)){
                //BLOB поле
                result=result+"                             null, \n";
            }
            else
            { //обычное поле
                result=result+"                             old."+fieldName+", \n";
            }

            //новое поле
            if (TableProperties.checkTypeFieldLongString(fieldType)){
                //длинная строка
                result=result+"                             null, \n";
                result=result+"                             2); \n";
                result=result + "end if;";
            }
            else if (TableProperties.checkTypeFieldBlob(fieldType)){
                //BLOB поле
                result=result+"                             null, \n";
                result=result+"                             3); \n";
                result=result + "end if;";
            }
            else
            { //обычное поле
                result=result+"                             new."+fieldName+", \n";
                result=result+"                             1); \n";
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
