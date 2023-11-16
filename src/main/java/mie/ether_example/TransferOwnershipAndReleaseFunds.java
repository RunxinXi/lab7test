package mie.ether_example;


import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;

import edu.toronto.dbservice.types.EtherAccount;


public class TransferOwnershipAndReleaseFunds implements JavaDelegate{
	
	@Override
	public void execute(DelegateExecution execution) {
		System.out.println("Transfer ownership and release funds.");
		
		int buyerId = (int) execution.getVariable("buyerId");
		int sellerId = (int) execution.getVariable("sellerId");
		
		Web3j web3 = Web3j.build(new HttpService());
		
		HashMap<Integer, EtherAccount> accounts = (HashMap<Integer, EtherAccount>) execution.getVariable("accounts");
		
		String contractAddress = (String) execution.getVariable("contractAddress");
		Registry buyerRegistry = Registry.load(contractAddress, web3, accounts.get(buyerId).getCredentials(), EtherUtils.GAS_PRICE, EtherUtils.GAS_LIMIT_CONTRACT_TX);
		Registry sellerRegistry = Registry.load(contractAddress, web3, accounts.get(sellerId).getCredentials(), EtherUtils.GAS_PRICE, EtherUtils.GAS_LIMIT_CONTRACT_TX);
		
		String propertyAddress = (String) execution.getVariable("propertyAddress");
		Utf8String encodedPropertyAddress = new Utf8String(propertyAddress);
		
		Address newOwnerAddress;
		TransactionReceipt receipt;
		try {
			newOwnerAddress = new Address(accounts.get(buyerId).getCredentials().getAddress());
			receipt = sellerRegistry.transfer(encodedPropertyAddress, newOwnerAddress).get();
			execution.setVariable("fundsReleased", true);
			EtherUtils.reportTransaction("Transfer Successful", receipt);
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
