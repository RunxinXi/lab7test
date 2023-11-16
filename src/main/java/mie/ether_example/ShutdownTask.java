package mie.ether_example;

import java.io.IOException;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;


public class ShutdownTask implements JavaDelegate{
	
	
	@Override
	public void execute(DelegateExecution execution) {
		
		//shut down rpc
		try {
			Runtime.getRuntime().exec("taskkill /FI \"WINDOWTITLE eq TestRpc*\"");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Ganache Client stopped");
	}

}
