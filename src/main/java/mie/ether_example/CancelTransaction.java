package mie.ether_example;


import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;


public class CancelTransaction implements JavaDelegate{
	
	@Override
	public void execute(DelegateExecution execution) {
		execution.setVariable("transactionCancelled", true);
		System.out.println("Cancel transaction.");
	}

}
