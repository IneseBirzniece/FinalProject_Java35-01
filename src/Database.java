import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;
import java.sql.*;

public class Database {
    // AGNESE: Main failā jāizveido Database klases objekts metožu izsaukšanai
    // Katra metode no DB faila jāizsauc ar database.metodesNosaukums
    Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        String dbURL = "jdbc:mysql://localhost:3306/java35";
        String username = "root";
        String password = "$arezgitaPar0le!";

// AGNESE: Savienojuma pārbaude
        try (Connection conn = DriverManager.getConnection(dbURL, username, password)) {
            System.out.println("Connected to database!");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /* Metodēs pēc sql rindiņas:
     Statement stmt = con.createStatement();
     ResultSet rs = stmt.executeQuery */


    // IZVĒLNES KOKS
    // ARTA: Metode, lai parādītu instrumentu tabulu
    public static void readTools(Connection conn) throws SQLException {

        String sql = "SELECT * FROM tools";
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        while (resultSet.next()){
            String category = resultSet.getString(1);
            String id = resultSet.getString(2);
            String name = resultSet.getString(3);
            String specifications = resultSet.getString(4);
            int serviceHours = resultSet.getInt(5);
            String priceDay = resultSet.getString(6);

            String output = "Tool : %s - %s - %s - %s - %s - %s";
            System.out.println(String.format(output, category, id, name, specifications, serviceHours, priceDay));
        }
    }

    /* ARTA: Metode, lai parādītu main tabulu
    Vai nepieciešams viss vai tikai toolName, available, untilService?
    String sql = "SELECT toolName, [toolID], untilService FROM tools ORDER BY available DESC"; */
    public static void readMain(Connection conn) throws SQLException {

        String sql = "SELECT * FROM main";
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);

        while (resultSet.next()) {
            int nPk = resultSet.getInt(1);
            String toolID = resultSet.getString(2);
            String toolName = resultSet.getString(3);
            int available = resultSet.getInt(4);
            int untilService = resultSet.getInt(5);
            String rented = resultSet.getString(6);
            String rentedPIN= resultSet.getString(7);
            String contacts = resultSet.getString(8);


            String output = ": %s - %s - %s - %s - %s - %s - %s - %s";
            System.out.println(String.format(output, nPk, toolID, toolName, available, untilService, rented, rentedPIN, contacts));
        }
    }

    // ARTA: Metode, kas pēc Tool ID pārbauda, vai instruments eksistē.
    // Varbūt Main failā jāsavieno ar insertTool un deleteTool?
    private static boolean toolIdExists(Connection conn, String id) throws SQLException {
        String sql = "SELECT COUNT(*) FROM tools WHERE id = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        int count = resultSet.getInt(1);
        return count > 0;
    }
    // ARTA: metode, lai pievienotu jaunu instrumentu DB
    public static void insertTool(Connection conn, String category, String ID, String name, String specifications, int serviceHours, String priceDay) throws SQLException {

        String sql = "INSERT INTO tools (category, ID, name, specifications, serviceHours, priceDay) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, category);
        preparedStatement.setString(2, ID);
        preparedStatement.setString(3, name);
        preparedStatement.setString(4, specifications);
        preparedStatement.setInt(5, serviceHours);
        preparedStatement.setString(6, priceDay);

        int rowInserted = preparedStatement.executeUpdate();

        if (rowInserted > 0){
            System.out.println("A new tool was inserted successfully");
        }else {
            System.out.println("Something went wrong");
        }
    }

    // ARTA: metode, lai izdzēstu instrumentu
    public static void deleteTool(Connection conn, String id) throws SQLException{

        String sql = "DELETE FROM tools WHERE id = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, id);

        if (preparedStatement.executeUpdate() > 0){
            System.out.println("Tool was deleted successfully");
        }else{
            System.out.println("Something went wrong");
        }
    }

    // ARTA: Metode, ar kuru pārbauda, vai peronas kods ir tabulā
    // Main failā varūt savienot ar metodēm insertCustomer un deleteUser?
    private static boolean personalIdExists(Connection conn, String personalIDNo) throws SQLException {
        String sql = "SELECT COUNT(*) FROM customers WHERE personalIDNo = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, personalIDNo);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        int count = resultSet.getInt(1);
        return count > 0;
    }
    // ARTA: Metode, lai DB pievienotu jaunu klientu
        public static void insertCustomer(Connection conn, String name, String personalIDNo, String phoneNumber) throws
        SQLException {

            String sql = "INSERT INTO customers (name, personalIDNo, phoneNumber) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, personalIDNo);
            preparedStatement.setString(3, phoneNumber);

            int rowInserted = preparedStatement.executeUpdate();

            if (rowInserted > 0){
                System.out.println("A new customer was inserted successfully");
            }else {
                System.out.println("Something went wrong");
            }
        }

    // ARTA: Metode, lai izdzēstu klientu
    public static void deleteUser(Connection conn, String personalIDNo) throws SQLException{

        String sql = "DELETE FROM customers WHERE personalIDNo = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, personalIDNo);

        if (preparedStatement.executeUpdate() > 0){
            System.out.println("Customer was deleted successfully");
        }else{
            System.out.println("Something went wrong");
        }
    }

// INESE: izsniegšana (hand-out metožu bloks)
    /* INESE: metode, ar kuru pārbauda instrumenta pieejamību noliktavā:
    "SELECT available FROM main"
    Pārbauda atlikušās stundas līdz apkopei:
    "SELECT untilService FROM main" --> limits, no kura neizsniedz

    Ja pieejams, Piereģistrē jaunu izsniegšanas darbību:
    Ievada instrumenta ID un klienta personas kodu:
    String sql = "INSERT INTO main (toolID, rentedPIN) VALUES (?, ?)";
    Nomaina pieejamību uz 0:
    "UPDATE main SET available = 0";

    Metode cenas aprēķināšanai
    double sql = "SELECT priceDay FROM tools";
    */

// LAURA: atgriešana (return metožu bloks)
/* Nomaina pieejamību
int sql = "UPDATE main SET available = 1";

Metode, ar kur aprēķina atlikušās stundas līdz apkopei:

    int sql = "SELECT untilService FROM main";
    Statement stmt = con.createStatement();
    ResultSet rs = stmt.executeQuery;

Skenerī ievada lietošanas stundu skaitu, ko minējis klients.
Aprēķina atlikušo stundu skaitu, rezultātu ievada DB:
UPDATE main SET untilService = [rezultāts];
 */

    /* QUIT metode: "Anything else? y/n"
    conn.close(); - jānoskaidro, vai pievienot šajā failā (pēc katras metodes) vai main failā
    */



}
