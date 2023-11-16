//package mie.ether_example;
//
//import java.sql.Connection;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.AbstractMap.SimpleEntry;
//
//import org.flowable.engine.delegate.DelegateExecution;
//import org.flowable.engine.delegate.JavaDelegate;
//
//import edu.toronto.dbservice.config.MIE354DBHelper;
//
//public class CheckClientList implements JavaDelegate {
//	Connection conn = null;
//	
//	public CheckClientList() {
//		conn = MIE354DBHelper.getDBConnection();
//	}
//	
//	@Override
//	public void execute(DelegateExecution execution) {
//		System.out.println("CheckClientList: IMPLEMENT ME");
//	}
//}
package mie.ether_example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

import edu.toronto.dbservice.config.MIE354DBHelper;

public class CheckClientList implements JavaDelegate {
    Connection conn = null;
    
    public CheckClientList() {
        conn = MIE354DBHelper.getDBConnection();
    }
    
    @Override
    public void execute(DelegateExecution execution) {
        String buyerName = (String) execution.getVariable("buyerName");
        String sellerName = (String) execution.getVariable("sellerName");

        checkClient(execution, buyerName, "buyerId", "buyerIsClient");
        checkClient(execution, sellerName, "sellerId", "sellerIsClient");
    }

    private void checkClient(DelegateExecution execution, String name, String idVar, String isClientVar) {
        String sql = "SELECT id FROM ACCOUNTDETAILS WHERE NAME = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                execution.setVariable(idVar, rs.getInt("id"));
                execution.setVariable(isClientVar, true);
            } else {
                execution.setVariable(isClientVar, false);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exception
        }
    }
}
