package tasks;

import java.sql.*;
import java.util.Scanner;


public class izmeginajums {


    public static void handOutAvailibility(Connection conn) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        //we will interact with data
        //have to catch any problems - throws

        String sql = "SELECT * FROM tools";
        Statement statement = conn.prepareStatement(sql); //creates new page where to write
        ResultSet resultSet = statement.executeQuery(sql);

        System.out.println("Enter tool ID: ");
        String toolID = scanner.nextLine();

        ((PreparedStatement) statement).setString(1, toolID);

        while (resultSet.next()) {
            int available = resultSet.getInt(4);
            if (available == 0) {
                System.out.println("This tool is available for rent");
            } else {
                System.out.println("This tool is not available");
            }
        }

    }

    public static void handOutHoursTillService(Connection conn) throws SQLException {
        //we will interact with data
        //have to catch any problems - throws
        Scanner scanner = new Scanner(System.in);
        String sql = "select untilService from main where toolID = '?'";
        Statement statement = conn.prepareStatement(sql); //creates new page where to write
        ResultSet resultSet = statement.executeQuery(sql);

        System.out.println("Enter tool ID: ");
        String toolID = scanner.nextLine();

        ((PreparedStatement) statement).setString(1, toolID);

        while (resultSet.next()) {
            int hoursTillService = resultSet.getInt(5);
            if (hoursTillService < 20) {
                System.out.println("Sorry, we have to do maintenance on our tool");
            } else if (hoursTillService < 50){
                System.out.println("It is less than 50 hours till maintenance");
            }else{
                System.out.println("This tool is available for rent");
            }
        }
    }

    /*public static void handOut(Connection conn) throws SQLException {
        //we will interact with data
        //have to catch any problems - throws

        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter tool ID: ");
        String toolID = scanner.nextLine();
        System.out.println("Enter personal ID:");
        String personalID = scanner.nextLine();


        String sql = "insert into main (toolID, rentedPIN) values (?, ?)";
        PreparedStatement preparedStatement = conn.prepareStatement(sql); //creates new page where to write
        //ResultSet resultSet = statement.executeQuery(sql);
        ((PreparedStatement) preparedStatement).setString(1, toolID);
        ((PreparedStatement) preparedStatement).setString(2, personalID);

        //PreparedStatement preparedStatement;

           int rowInserted = preparedStatement.executeUpdate();
           if (rowInserted > 0) {
               System.out.println("Table was updated");
           } else {
               System.out.println("Something went wrong");
           }

    }*/

    /*public static void handOut (Connection conn, Scanner scanner) throws SQLException {
        String sql = "SELECT available FROM main WHERE toolID = ?";
        System.out.println("Enter tool ID: ");
        String toolID = scanner.nextLine();
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, toolID);

        ResultSet toolAvalibl = preparedStatement.executeQuery();
        int avalible = toolAvalibl.getInt(4);

        if (avalible == 1) {
            handout11(conn, toolID);
        } else if (!toolIdExistsInMain(conn, toolID)) {
            insertMainTools(conn);
            insertMainCustomers(conn);
        } else {

            System.out.println("nevar izsniegt");

        }
    }*/

    public static void handout11(Connection conn, String toolID) throws SQLException {
        String sql = "UPDATE main SET available = 0 WHERE toolID = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, toolID);

        if (preparedStatement.executeUpdate() > 0) {
            System.out.println("Its handed our success");
        } else {
            System.out.println("smth went wrong");
        }

    }

    public static boolean toolIdExistsInMain(Connection conn, String toolID) throws SQLException {
        String sql = "SELECT COUNT(*) FROM main WHERE toolID = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, toolID);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        int count = resultSet.getInt(1);
        return count > 0;

    }

    public static void insertMainTools(Connection conn) throws SQLException {
        String sql = "INSERT INTO main (toolID, toolName, untilService) SELECT (id, name, serviceHours) FROM tools ";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        int rowInserted = preparedStatement.executeUpdate();

        if (rowInserted > 0){
            System.out.println("A new tool was inserted successfully");
        }else {
            System.out.println("Something went wrong");
        }
    }

    public static void insertMainCustomers(Connection conn) throws SQLException {
        String sql = "INSERT INTO main (rented, rentedPIN, contacts) SELECT (name, personalIDNo,phoneNumber) FROM customers";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        int rowInserted = preparedStatement.executeUpdate();

        if (rowInserted > 0){
            System.out.println("A new tool was inserted successfully");
        }else {
            System.out.println("Something went wrong");
        }
    }
    public static void handOut(Connection conn, Scanner scanner) throws SQLException {
        System.out.println("Enter tool ID: ");
        String toolID = scanner.nextLine();

        String sql = "SELECT COUNT(*) FROM tools WHERE id= ?";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, toolID);
        ResultSet resultSet = preparedStatement.executeQuery();
        resultSet.next();
        int count = resultSet.getInt(1);
        if (count == 0) {
            System.out.println("We don't have such tool");
            return;
        } else if (count > 1) {
            System.out.println("Error: More than one tool found with the same ID");
            return;
        }

        sql = "SELECT available FROM main WHERE toolID = ?";
        preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, toolID);
        resultSet = preparedStatement.executeQuery();
        resultSet.next();
        int available = resultSet.getInt(1);
        if (available == 0) {
            System.out.println("The tool is not available for rent");
            return;
        }

        System.out.println("Enter customer ID: ");
        String personalID = scanner.nextLine();
        sql = "SELECT COUNT(*) FROM main WHERE toolID = ? AND rentedPIN = ?";
        preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, toolID);
        preparedStatement.setString(2, personalID);
        resultSet = preparedStatement.executeQuery();
        resultSet.next();
        count = resultSet.getInt(1);
        if (count > 0) {
            System.out.println("The tool is already rented by the customer");
            return;
        }

        sql = "UPDATE main SET available = 0, rented = 1, rentedPIN = ? WHERE toolID = ?";
        preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, personalID);
        preparedStatement.setString(2, toolID);
        if (preparedStatement.executeUpdate() > 0) {
            System.out.println("The tool has been rented successfully");
        } else {
            System.out.println("Something went wrong");
        }
    }

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

    /*public static void popServiceWin (Connection conn, String newToolID) throws SQLException{
        String sql = "SELECT untilService FROM main WHERE toolID = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1,newToolID);

        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()){
            int hours = resultSet.getInt(1);
            if (hours < 25) {
                System.out.println("Warning!!! Till service less than 24 hours");
            } else {
                System.out.println( "Till service " + hours + "hours. All ok");
            }
        }else {

        }

    }*/


}

