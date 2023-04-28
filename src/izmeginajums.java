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

    public static void handOut (Connection conn, Scanner scanner) throws SQLException {
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
    }

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



}

