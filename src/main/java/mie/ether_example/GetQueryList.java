package mie.ether_example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

public class GetQueryList implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        // Database connection details
        String url = "jdbc:mysql://localhost:3306/yourDatabase"; // Update with your database details
        String user = "username"; // Update with your database username
        String password = "password"; // Update with your database password

        List<String[]> queryList = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Query")) {

            while (rs.next()) {
                int id = rs.getInt("id");
                int account = rs.getInt("account");
                String item = rs.getString("item");
                queryList.add(new String[]{String.valueOf(id), String.valueOf(account), item});
            }

            execution.setVariable("clientQueryList", queryList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

