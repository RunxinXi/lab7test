package mie.ether_example;

import java.sql.*;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

public class ClientQuery implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        // Database connection details
        String url = "jdbc:mysql://localhost:3306/yourDatabase"; // Update with your database details
        String user = "username"; // Update with your database username
        String password = "password"; // Update with your database password

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO Results (id, owner) VALUES (?, ?)")) {
            // Retrieve query data from process variable
            String[] queryData = (String[]) execution.getVariable("currentQuery");
            int queryId = Integer.parseInt(queryData[0]);
            String item = queryData[2];

            // Get the owner's address based on the item
            String ownerAddress = getOwnerAddress(item);


            pstmt.setInt(1, queryId);
            pstmt.setString(2, ownerAddress);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String getOwnerAddress(String item) {
        // Implement the logic to retrieve the owner's address
        return "ownerAddress"; // Placeholder
    }
}


