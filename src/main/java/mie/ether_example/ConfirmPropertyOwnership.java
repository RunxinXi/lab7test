package mie.ether_example;


import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import edu.toronto.dbservice.types.EtherAccount;


public class ConfirmPropertyOwnership implements JavaDelegate{
	
	@Override
	public void execute(DelegateExecution execution) {
		System.out.println("ConfirmPropertyOwnership: IMPLEMENT ME");
	}
}
