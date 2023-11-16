package mie.ether_example;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.HashMap;

import java.util.concurrent.ExecutionException;

import org.web3j.abi.datatypes.Address;

import org.web3j.abi.datatypes.Utf8String;

import org.web3j.abi.datatypes.generated.Uint256;

import org.web3j.protocol.Web3j;

import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;


import edu.toronto.dbservice.config.MIE354DBHelper;
import edu.toronto.dbservice.types.ClientRequest;
import edu.toronto.dbservice.types.EtherAccount;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;


public class ClientRegisterTask implements JavaDelegate{
	
	Connection dbCon = null;

	public ClientRegisterTask() {
		dbCon = MIE354DBHelper.getDBConnection();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void execute(DelegateExecution execution) {
		
		// get current registry request
		ClientRequest currentClientRequest = (ClientRequest) execution.getVariable("currentClientRequest");
		Integer clientNum = currentClientRequest.getClientNum();
		String item = currentClientRequest.getItem();
		
		// connect to the blockchain and load the registry contract
		Web3j web3 = Web3j.build(new HttpService());
		String contractAddress = (String) execution.getVariable("contractAddress");
		HashMap<Integer, EtherAccount> accounts = (HashMap<Integer, EtherAccount>) execution.getVariable("accounts");
		Registry clientRegistry = Registry.load(contractAddress, web3, accounts.get(clientNum).getCredentials(), EtherUtils.GAS_PRICE, EtherUtils.GAS_LIMIT_CONTRACT_TX);
		
		// encode the item key before registering with the contract
		Utf8String encodedItem = new Utf8String(item);
		System.out.println(item + " : " + encodedItem.getValue());
		
		// register the item and report result
		TransactionReceipt registerReceipt = null;
		try {
			registerReceipt = clientRegistry.register(encodedItem).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
		EtherUtils.reportTransaction("Client " + clientNum + " registered " + item, registerReceipt);
		
		//querying back the registered item's owner address and time
		Address ownerAddress = null;
		try {
			ownerAddress = clientRegistry.getOwner(encodedItem).get();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
//			e1.printStackTrace();
		} catch (ExecutionException e1) {
			// TODO Auto-generated catch block
//			e1.printStackTrace();
		}
		Uint256 registryTime = null;
		try {
			registryTime = clientRegistry.getTime(encodedItem).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
		
		// decode owner address and time to strings
		String strOwnerAddress = ownerAddress.toString();
		Date decodedTime = new Date(registryTime.getValue().intValue());
		SimpleDateFormat timeFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		String strTime = timeFormatter.format(decodedTime);
		
		// save record on registration in Registered database table
		String query = "INSERT INTO Registered (account, address, item, time) VALUES (?, ?, ?, ?)";
		PreparedStatement preparedStmt = null;
		try {
			preparedStmt = dbCon.prepareStatement(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
		try {
			preparedStmt.setInt (1, clientNum);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		} // field 1 is an int
		try {
			preparedStmt.setString (2, strOwnerAddress);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		} // field 2 is a string
		try {
			preparedStmt.setString (3, item);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
		try {
			preparedStmt.setString (4, strTime);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
		try {
			preparedStmt.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}

	}

}
