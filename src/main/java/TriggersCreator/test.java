package TriggersCreator;


import TriggersCreator.util.TriggersQueryExecutor;



public class test {
    public static void main(String[] args) {
        TriggersQueryExecutor triggersQueryExecutor = new TriggersQueryExecutor();
        triggersQueryExecutor.dropAllTriggers();
        triggersQueryExecutor.allTriggerFunctionsQuery();
        triggersQueryExecutor.allTriggersQuery();


    }
}