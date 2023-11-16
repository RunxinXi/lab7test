package mie.ether_example;



import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import org.web3j.abi.datatypes.Address;

import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.protocol.Web3j;

import org.web3j.protocol.http.HttpService;



import edu.toronto.dbservice.types.EtherAccount;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;


public class CollectAuditInfo implements JavaDelegate{

	@SuppressWarnings("unchecked")
	@Override
	public void execute(DelegateExecution execution) {
		
		// Connect to blockchain server
		Web3j web3 = Web3j.build(new HttpService());
		
		// load the list of accounts
		HashMap<Integer, EtherAccount> accounts = (HashMap<Integer, EtherAccount>) execution.getVariable("accounts");
		
		// load registry contract based on the process variable contractAddress
		String contractAddress = (String) execution.getVariable("contractAddress");
		Registry myRegistry = Registry.load(contractAddress, web3, accounts.get(0).getCredentials(), EtherUtils.GAS_PRICE, EtherUtils.GAS_LIMIT_CONTRACT_TX);
		
		// Get audited item details
		String selectedAuditItem = (String) execution.getVariable("anAuditedItem");
		Utf8String encodedAuditedItem = new Utf8String(selectedAuditItem);
		Address ownerAddress = null;
		try {
			ownerAddress = myRegistry.getOwner(encodedAuditedItem).get();
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Uint256 registryTime = null;
		try {
			registryTime = myRegistry.getTime(encodedAuditedItem).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Convert data to strings
		String strOwnerAddress = ownerAddress.toString();
		Date decodedTime = new Date(registryTime.getValue().intValue());
		SimpleDateFormat timeFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		String strTime = timeFormatter.format(decodedTime);
		
		// Save audited item details as variable
		execution.setVariable("auditedItemOwner", strOwnerAddress);
		execution.setVariable("auditedItemTime", strTime);
		
		// Print audited item
		System.out.println(" Auditing item" + selectedAuditItem + ": registered by " + ownerAddress.toString() + " at " + strTime);
		
	}
}
