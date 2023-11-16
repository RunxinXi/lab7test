package mie.ether_example;


import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;


public class ConfirmFundsInEscrow implements JavaDelegate{
	
	@Override
	public void execute(DelegateExecution execution) {
		System.out.println("Confirm funds in escrow.");
		boolean fundsInEscrow = Boolean.parseBoolean((String) execution.getVariable("fundsInEscrow"));
		execution.setVariable("fundsConfirmed", fundsInEscrow);
	}
}
