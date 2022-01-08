package TriggersCreator.util;

import java.sql.*;

public class UtilConnection {
    public Connection getConnection(){
        Connection connection = null;
        try {
            Class.forName("org.postgresql.Driver");

            connection = DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/testObmen1","andrew", "123456");
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return connection;
    }
}
