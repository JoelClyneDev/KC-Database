package LocalDatabaseOperations;

import java.sql.*;

public class CardDatabaseManager {

    private static final String CONNECTION_URL = "Jdbc:sqlite:" + System.getProperty("user.dir") + "\\";


    public static Connection createNewDatabase(String fileName) {

        String url = CONNECTION_URL + fileName;

        try {
            Connection conn = DriverManager.getConnection(url);
            if (conn != null){
                System.out.println(url);
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
                Statement stmt = conn.createStatement();
                setupTables(stmt);
            }
            return conn;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setupTables(Statement stmt) throws SQLException {
        String monsterTableSetupCommands = "CREATE TABLE IF NOT EXISTS Monster_Table (\n" +
                "   id integer PRIMARY KEY, \n" +
                "   Name VARCHAR(255),\n" +
                "   Effect VARCHAR(255),\n" +
                "   Level TINYINT(255),\n" +
                "   Attack SMALLINT(255),\n" +
                "   Defense SMALLINT(255),\n" +
                "   Attribute VARCHAR(255),\n" +
                "   Monster_Type VARCHAR(255),\n" +
                "   Effect_Status BOOLEAN, \n" +
                "   Summoning_Type VARCHAR(255),\n" +
                "   Pendulum BOOLEAN, \n" +
                "   Pendulum_Scale TINYINT(255),\n" +
                "   Pendulum_Effect VARCHAR(255)"+
                "   );";
        stmt.execute(monsterTableSetupCommands);
    }



    public static DatabaseMetaData createCardDatabase(){
        return null;
    }

    public static void main(){
        createNewDatabase("test.db");

    }
}
