package mie.ether_example;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.form.FormProperty;
import org.flowable.engine.form.TaskFormData;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.task.api.Task;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import edu.toronto.dbservice.types.EtherAccount;

import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;

@RunWith(Parameterized.class)
public class Lab7_RealEstateTransaction_UnitTest extends LabBaseUnitTest {
	@BeforeClass
	public static void setupFile() {
		filename = "src/main/resources/diagrams/Lab7_RealEstateTransaction.bpmn";
	}

	String buyerName;
	String sellerName;
	String propertyAddress;
	String soldPrice;
	String fundsInEscrow;
	boolean expectSuccessfulTransaction;

	public Lab7_RealEstateTransaction_UnitTest(String buyerName, String sellerName, String propertyAddress,
			String soldPrice, String fundsInEscrow, String expectSuccessfulTransaction) {
		this.buyerName = buyerName;
		this.sellerName = sellerName;
		this.propertyAddress = propertyAddress;
		this.soldPrice = soldPrice;
		this.fundsInEscrow = fundsInEscrow;
		this.expectSuccessfulTransaction = Boolean.parseBoolean(expectSuccessfulTransaction);
	}

	@Parameters
	public static Collection<String[]> data() {
		ArrayList<String[]> parameters = new ArrayList<>();
		parameters.add(new String[] { "Aimee Lowe", "Lauryn Talbot", "5 King's College Rd, Toronto, ON M5S 3G8",
				"300000", "true", "true" });
		return parameters;
	}

	@Test
	public void testRealEstateTransaction() {
		RuntimeService runtimeService = flowableContext.getRuntimeService();
		processInstance = runtimeService.startProcessInstanceByKey("process1");
		fillTransactionForm();
		
		boolean fundsReleased = (boolean) flowableContext.getHistoryService().createHistoricVariableInstanceQuery().variableName("fundsReleased").singleResult().getValue();
		
		if (expectSuccessfulTransaction) {
			assertTrue(fundsReleased);
			assertTrue(checkOwnership());
		} else {
			assertFalse(fundsReleased);
		}
	}

	private void fillTransactionForm() {
		TaskService taskService = flowableContext.getTaskService();
		Task transactionForm = taskService.createTaskQuery().taskDefinitionKey("transactionForm")
				.singleResult();
		
		HashMap<String, String> formEntries = new HashMap<>();
		formEntries.put("buyerName", buyerName);
		formEntries.put("sellerName", sellerName);
		formEntries.put("propertyAddress", propertyAddress);
		formEntries.put("soldPrice", soldPrice);
		formEntries.put("fundsInEscrow", fundsInEscrow);
		
		flowableContext.getFormService().submitTaskFormData(transactionForm.getId(), formEntries);
	}
	
	private boolean checkOwnership() {
		HashMap<Integer, EtherAccount> accounts = (HashMap<Integer, EtherAccount>) flowableContext.getHistoryService().createHistoricVariableInstanceQuery().variableName("accounts").singleResult().getValue();
		int buyerId = (int) flowableContext.getHistoryService().createHistoricVariableInstanceQuery().variableName("buyerId").singleResult().getValue();
		String buyerAddress = accounts.get(buyerId).getCredentials().getAddress();
		
		Web3j web3 = Web3j.build(new HttpService());
		String contractAddress = (String)  flowableContext.getHistoryService().createHistoricVariableInstanceQuery().variableName("contractAddress").singleResult().getValue();
		Registry registry = Registry.load(contractAddress, web3, accounts.get(0).getCredentials(), EtherUtils.GAS_PRICE, EtherUtils.GAS_LIMIT_CONTRACT_TX);
		
		Utf8String encodedPropertyAddress = new Utf8String(propertyAddress);
		
		try {
			return registry.getOwner(encodedPropertyAddress).get().toString().equals(buyerAddress);
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}
