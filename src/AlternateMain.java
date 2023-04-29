import java.sql.*;
import java.util.Scanner;

public class AlternateMain {
    public static void main(String[] args) {
        String dbURL = "jdbc:mysql://localhost:3306/java35";
        String username = "root";
        String password = "$arezgitaPar0le!";
        try (Connection conn = DriverManager.getConnection(dbURL, username, password)) {
            System.out.println("Connected to database!");
            updateMain(conn);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void updateMain(Connection conn) throws SQLException {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Confirm tool ID");
        String toolIDSc = scanner.nextLine().toUpperCase().trim();
        System.out.println("Enter customer ID:");
        String customerID = scanner.nextLine().trim();

        PreparedStatement toolSelect = conn.prepareStatement("SELECT * FROM tools WHERE id = ?");
        toolSelect.setString(1, toolIDSc);
        ResultSet resultSet = toolSelect.executeQuery();

        PreparedStatement customerSelect = conn.prepareStatement("SELECT * FROM customers WHERE personalIDNo = ?");
        customerSelect.setString(1, customerID);
        ResultSet resultSet2 = customerSelect.executeQuery();

        if (resultSet.next() && resultSet2.next()) {
            PreparedStatement setUpdate = conn.prepareStatement
                    ("INSERT INTO main (toolID, toolName) SELECT id, name FROM tools WHERE id IN (SELECT id FROM tools WHERE id= ?)");
                        setUpdate.setString(1, toolIDSc);
            PreparedStatement setUpdate2 = conn.prepareStatement
                    ("UPDATE main SET rented = ?, rentedPIN = ?, contacts = ? WHERE toolID = ?");
            setUpdate2.setString(1, resultSet2.getString("name"));
            setUpdate2.setString(2, customerID);
            setUpdate2.setString(3, resultSet2.getString("phoneNumber"));
            setUpdate2.setString(4, toolIDSc);
            setUpdate2.executeUpdate();

            if (setUpdate.executeUpdate() > 0) {
                System.out.println("Success");
            } else {
                System.out.println("Something went wrong");
            }

//
//
//
//            if (resultSet2.next()) {
//                PreparedStatement updateCustomer = conn.prepareStatement
//                        ("INSERT INTO main (rented, rentedPIN, contacts)\n" +
//                                "SELECT name, personalIDNo, phoneNumber FROM customers WHERE (SELECT toolID FROM = ?)");
////                ("INSERT INTO main (rented, rentedPIN, contacts) VALUES SELECT (name, personalIDNo, phoneNumber FROM customers) where toolID = ?");
//                updateCustomer.setString(1, toolIDSc);
//                if (updateCustomer.executeUpdate() > 0) {
//                    System.out.println("Success");
//                } else {
//                    System.out.println("Something went wrong");
//                }
//            }
        }
    }
}
//        Scanner scanner = new Scanner(System.in);
//    }
//        System.out.println("Confirm tool ID");
//        String toolIDSc = scanner.nextLine().toUpperCase().trim();
//
//        PreparedStatement statement = conn.prepareStatement("SELECT * FROM tools WHERE id = ?");
//        statement.setString(1, toolIDSc);
//        ResultSet resultSet = statement.executeQuery();
//
//        if (resultSet.next()) {
//            PreparedStatement updateTools = conn.prepareStatement("INSERT INTO main (toolID, toolName) SELECT id, name FROM tools WHERE toolID = ?");
//            updateTools.setString(1, toolIDSc);
//            ResultSet resultSet1 = updateTools.executeQuery();
//        }
//
//
//        System.out.println("Enter customer ID:");
//        String customerID = scanner.nextLine().trim();
//
//        PreparedStatement updateCustomers = conn.prepareStatement("SELECT * FROM customers WHERE personalIDNo = ?");
//        updateCustomers.setString(1, customerID);
//        ResultSet resultSet2 = statement.executeQuery();
//
//        if (resultSet2.next()) {
//            PreparedStatement updateTools = conn.prepareStatement("INSERT INTO main (rented, rentedPIN, contacts) SELECT name, personalIDNo,phoneNumber FROM customers where toolID = ?");
//            updateTools.setString(1, toolIDSc);
//            ResultSet resultSet3 = updateTools.executeQuery();
//        }
//    }
//
//}



