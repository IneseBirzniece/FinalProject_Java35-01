package tasks;

import java.sql.*;
import java.util.Scanner;


public class izmeginajums {


    public static void resetHours(Connection conn) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        String sql = "SELECT * FROM main WHERE untilService <20";
        Statement toService = conn.createStatement();
        ResultSet resultSet3 = toService.executeQuery(sql);

        // Izdrukā brīdinājumu vai ieliek esošo brīdinājuma metodi (tad tikai jautājums Send now?)
        System.out.println("Warning: tool " + resultSet3.getString(2) + " has less than <20/24> hours till service. Send to service now? y/n");
        char confirm = scanner.nextLine().charAt(0);
        scanner.nextLine();
        System.out.println("Confirm tool ID: ");
        String toolIDsC = scanner.nextLine();

        // Atiestata stundas līdz apkopei main tabulā
        while (confirm == 'y') {
            //Salīdzina SQL esošo ID ar lietotāja apstiprināto. Ja sakrīt, atjaunina
            if ((resultSet3.getString(2)).equals(toolIDsC)) {
                PreparedStatement toolReset = conn.prepareStatement("UPDATE main SET untilService = 500 WHERE toolID = ?");
                toolReset.setString(1, toolIDsC);
                if (toolReset.executeUpdate() > 0) {
                    System.out.printf("Service hours for tool %s have been reset to %d", resultSet3.getString(2), resultSet3.getInt(5));
                } else {
                    System.out.println("Resetting service hours failed");
                }
            } else {
                System.out.println("Something went wrong. Check the tool ID");
            }
        }
    }


}

