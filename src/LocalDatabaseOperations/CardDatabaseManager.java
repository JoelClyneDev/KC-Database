package LocalDatabaseOperations;

import CardTypes.YuGiOhCard;
import CardTypes.YuGiOhMonster;
import CardTypes.YuGiOhSpellTrap;

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
                "   id VARCHAR(255) PRIMARY KEY, \n" +
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
                "   Pendulum_Effect VARCHAR(255), \n" +
                "   Pack VARCHAR(255), \n" +
                "   Card_Image_Path VARCHAR(255)"+
                "   );";
        String spellTrapTableSetupCommands = "CREATE TABLE IF NOT EXISTS Spells_and_Traps (\n" +
                "   id VARCHAR(255) PRIMARY KEY, \n" +
                "   Name VARCHAR(255),\n" +
                "   Effect VARCHAR(255),\n" +
                "   Pack VARCHAR(255), \n" +
                "   Card_Image_Path VARCHAR(255), \n"+
                "   Main_Card_Type VARCHAR(255), \n" +
                "   Deep_Type VARCHAR (255)" +
                "   );";
        stmt.execute(monsterTableSetupCommands);
        stmt.execute(spellTrapTableSetupCommands);
    }

    public static void insertMonster(YuGiOhMonster card, Connection conn) throws SQLException {
        String id = card.getId();
        String name = card.getName();
        String effect = card.getEffect();
        String pack = card.getPack();
        String cardImagePath = card.getCardImagePath();
        int level = card.getLevel();
        int attack = card.getAttack();
        int defense = card.getDefense();
        String attribute = DatabaseConverters.AttributeToString(card.getAttribute());
        String monsterType = DatabaseConverters.MonsterTypeToString(card.getMonsterType());
        boolean effectStatus = card.isEffectStatus();
        String summonType = DatabaseConverters.MonsterSummoningTypeToString(card.getMonsterSummoningType());
        boolean isPendulum = card.isPendulum();
        int pendulumScale = card.getPendulumScale();
        String pendulumEffect = card.getPendulumEffect();
        PreparedStatement monsterInsertCommand = generateMonsterCommand(id, name, effect, pack, cardImagePath, level, attack, defense, attribute, monsterType, effectStatus, summonType, isPendulum, pendulumScale, pendulumEffect, conn);
        monsterInsertCommand.executeUpdate();
        //stmt.execute(monsterInsertCommand);
    }

    public static void insertSpellTrap(YuGiOhSpellTrap card, Connection conn) throws SQLException {
        String id = card.getId();
        String name = card.getName();
        String effect = card.getEffect();
        String pack = card.getPack();
        String cardImagePath = card.getCardImagePath();
        String mainCardType = DatabaseConverters.MainCardTypeToString(card.getType());
        String deepType = DatabaseConverters.DeepSpellTrapTypeToString(card.getDeepSpellTrapTypes());
        PreparedStatement spellTrapStatement = generateSpellTrapCommand(id, name, effect, pack, cardImagePath, mainCardType, deepType, conn);
        spellTrapStatement.executeUpdate();

    }

    public static PreparedStatement generateMonsterCommand(String id, String name, String effect, String pack, String cardImagePath, int level, int attack, int defense, String attribute, String monsterType, boolean effectStatus, String summonType, boolean isPendulum, int pendulumScale, String pendulumEffect, Connection conn) throws SQLException {
        String monsterInsertCommand = "INSERT OR REPLACE INTO Monster_Table (id, Name, Effect, Level, Attack, Defense, Attribute, Monster_Type, Effect_Status, Summoning_Type, Pendulum, Pendulum_Scale, Pendulum_Effect, Pack, Card_Image_Path) " +
                "VALUES((?),(?),(?),(?),(?),(?),(?),(?),(?),(?),(?),(?),(?),(?),(?)) ";
        PreparedStatement monsterStatement = conn.prepareStatement(monsterInsertCommand);
        monsterStatement.setString(1, id);
        monsterStatement.setString(2, name);
        monsterStatement.setString(3, effect);
        monsterStatement.setInt(4, level);
        monsterStatement.setInt(5, attack);
        monsterStatement.setInt(6, defense);
        monsterStatement.setString(7, attribute);
        monsterStatement.setString(8, monsterType);
        monsterStatement.setBoolean(9, effectStatus);
        monsterStatement.setString(10, summonType);
        monsterStatement.setBoolean(11, isPendulum);
        monsterStatement.setInt(12, pendulumScale);
        monsterStatement.setString(13, pendulumEffect);
        monsterStatement.setString(14, pack);
        monsterStatement.setString(15, cardImagePath);
        return monsterStatement;
    }

    public static PreparedStatement generateSpellTrapCommand(String id, String name, String effect, String pack, String cardImagePath, String mainCardType, String deepType, Connection conn) throws SQLException {
        String spellTrapInsertCommand = "INSERT OR REPLACE INTO Spells_and_Traps (id, Name, Effect, Pack, Card_Image_Path, Main_Card_Type, Deep_Type)" +
                "VALUES((?),(?),(?),(?),(?),(?),(?))";
        PreparedStatement spellTrapStatement = conn.prepareStatement(spellTrapInsertCommand);
        spellTrapStatement.setString(1, id);
        spellTrapStatement.setString(2, name);
        spellTrapStatement.setString(3, effect);
        spellTrapStatement.setString(4, pack);
        spellTrapStatement.setString(5, cardImagePath);
        spellTrapStatement.setString(6, mainCardType);
        spellTrapStatement.setString(7, deepType);
        return spellTrapStatement;
    }




    public static DatabaseMetaData createCardDatabase(){
        return null;
    }

    public static void main(){
        createNewDatabase("test.db");

    }
}
