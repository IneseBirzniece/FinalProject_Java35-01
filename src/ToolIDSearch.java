import java.sql.*;
import java.util.Scanner;

public class ToolIDSearch {
    public static void main(String[] args) {
        String dbURL = "jdbc:mysql://localhost:3306/java35";
        String username = "root";
        String password = "$arezgitaPar0le!";
        try (Connection conn = DriverManager.getConnection(dbURL, username, password)) {
            System.out.println("Connected to database!");
            toolIDSearch(conn);

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void toolIDSearch(Connection conn) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter tool ID:");
        String toolIDSc = scanner.nextLine().toUpperCase().trim();

        // Salīdzina SQL ar tool ID scanner ievadi. Ja ID atrasts, izdrukā id un name
        PreparedStatement statement = conn.prepareStatement("SELECT * FROM tools WHERE id = ?");
        statement.setString(1, toolIDSc);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            System.out.println("ID: " + resultSet.getString(2) + "\t Name: " + resultSet.getString(3));
        } else {
            System.out.println("ID does not exist");
        }

        // Pārbauda, vai instruments vispār ir ticis ievadīts main tabulā
        PreparedStatement everTaken = conn.prepareStatement("SELECT available FROM main WHERE toolID = ?");
        everTaken.setString(1, toolIDSc);
        ResultSet resultSet2 = everTaken.executeQuery();

        // Ja instruments ir main tabulā, pārbauda, vai available nav 0
        PreparedStatement isNowAvailable = conn.prepareStatement("SELECT * FROM main WHERE available <> 0 and toolID = ?");
        isNowAvailable.setString(1, toolIDSc);
        ResultSet resultSet3 = isNowAvailable.executeQuery();

        // Pārbauda, vai un kas ir paņēmis instrumentu. Ja ir paņemts, norāda klienta datus
        PreparedStatement takenBy = conn.prepareStatement("SELECT * FROM main WHERE available = 0 AND toolID = ?");
        takenBy.setString(1, toolIDSc);
        ResultSet resultSet4 = takenBy.executeQuery();


        if (!resultSet2.next() || resultSet3.next()) {
            System.out.println("Tool is available");
        } else if (resultSet4.next()) {
            System.out.println("Handed out to:\n"
                    + resultSet4.getString(6) + ", " + resultSet4.getString(7) + ", " + resultSet4.getString(8));
        }
    }

}