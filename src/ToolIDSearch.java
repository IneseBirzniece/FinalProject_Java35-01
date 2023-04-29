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

//            serviceReset(conn);
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

        if (resultSet.next()) {
            System.out.println("ID: " + resultSet.getString(2) + "\t Name: " + resultSet.getString(3));
            if (!resultSet2.next() || resultSet3.next()) {
                System.out.println("Tool is available");

            } else if (resultSet4.next()) {
                System.out.println("Handed out to:\n"
                        + resultSet4.getString(6) + ", " + resultSet4.getString(7) + ", " + resultSet4.getString(8));
            }
        } else {
            System.out.println("ID does not exist");
        }
    }
//public static void serviceReset (Connection conn) throws SQLException{
//        // Parāda pie atdošanas, kad aprēķinātais stundu skaits ir mazāks par 24 vai 20 (ka vienojas)
//    // Kaut kas līdz galam nestrādā, bet jāieliek kopēja kodā, tad vajadzētu strādāt
//    Scanner scanner = new Scanner(System.in);
//    String sql = "SELECT * FROM main WHERE untilService <20";
//    Statement toService = conn.createStatement();
//    ResultSet resultSet3 = toService.executeQuery(sql);
//
//    // Izdrukā brīdinājumu vai ieliek esošo brīdinājuma metodi (tad tikai jautājums Send now?)
//    System.out.println("Warning: tool " + resultSet3.getString(2) + " has less than <20/24> hours till service. Send to service now? y/n");
//    char confirm = scanner.nextLine().charAt(0);
//    scanner.nextLine();
//    System.out.println("Confirm tool ID: ");
//    String toolIDsC = scanner.nextLine();
//
//    // Atiestata stundas līdz apkopei main tabulā
//    while(confirm == 'y') {
//        //Salīdzina SQL esošo ID ar lietotāja apstiprināto. Ja sakrīt, atjaunina
//        if ((resultSet3.getString(2)).equals(toolIDsC)) {
//            PreparedStatement toolReset = conn.prepareStatement("UPDATE main SET untilService = 500 WHERE toolID = ?");
//            toolReset.setString(1, toolIDsC);
//            if (toolReset.executeUpdate() > 0) {
//                System.out.printf("Service hours for tool %s have been reset to %d", resultSet3.getString(2), resultSet3.getInt(5));
//            } else {
//                System.out.println("Resetting service hours failed");
//            }
//        } else {
//            System.out.println("Something went wrong. Check the tool ID");
//        }
//    }
    
    }

