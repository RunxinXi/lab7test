package mie.ether_example;
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

import edu.toronto.dbservice.types.EtherAccount;

import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;


/* Run with parameters */
@RunWith(Parameterized.class)
public class Lab7_Registry_UnitTest extends LabBaseUnitTest {
	
	@BeforeClass
	public static void setupFile() {
		filename = "src/main/resources/diagrams/Lab7_Registry_Diagram.bpmn";
	}
	
	/* START OF PARAMETERIZED CODE */
	private String anItemParameter;
	private String anOwnerParameter;
	
	/* Constructor has two string parameters */
	public Lab7_Registry_UnitTest(String itemParam, String ownerParam) {
		this.anItemParameter = itemParam;
		this.anOwnerParameter = ownerParam;
	}
	
	/* Setup parameters to provide pairs of strings to the constructor */
	@Parameters
	public static Collection<String[]> data() {
		ArrayList<String[]> parameters = new ArrayList<>();
		parameters.add(new String[] {"999-1234", "1"});
		parameters.add(new String[] {"999-2345", "2"});
		parameters.add(new String[] {"999-3456", "3"});
	    parameters.add(new String[] {"999-4567", "4"}); // New test data
	    parameters.add(new String[] {"999-5678", "5"}); // New test data
		return parameters;
	}
	/* END OF PARAMETERIZED CODE */
	
	private void startProcess() {	
		RuntimeService runtimeService = flowableContext.getRuntimeService();
		processInstance = runtimeService.startProcessInstanceByKey("process_pool1");
		System.out.println("process_pool1===================");
	}
	
	private void fillAuditForm(String auditedItem) {
		/* form fields are filled using a map from field ids to values */
		HashMap<String, String> formEntries = new HashMap<>();
		formEntries.put("anAuditedItem", auditedItem);
		
		/* get the user task "select audited item" */
		TaskService taskService = flowableContext.getTaskService();
		Task proposalsTask = taskService.createTaskQuery().taskDefinitionKey("usertask1")
				.singleResult();
		
		/* get the list of fields in the form */
		List<String> bpmnFieldNames = new ArrayList<>();
		TaskFormData taskFormData = flowableContext.getFormService().getTaskFormData(proposalsTask.getId());
		for (FormProperty fp : taskFormData.getFormProperties()){
			bpmnFieldNames.add(fp.getId());
		}
		
		/* build a list of required fields that must be filled */
		List<String> requiredFields = new ArrayList<>(
				Arrays.asList("anAuditedItem"));
		
		/* make sure that each of the required fields is in the form */
		for (String requiredFieldName : requiredFields) {
			assertTrue(bpmnFieldNames.contains(requiredFieldName));
		}
		
		/* make sure that each of the required fields was assigned a value */
		for (String requiredFieldName : requiredFields) {
			assertTrue(formEntries.keySet().contains(requiredFieldName));
		}
		
		/* submit the form (will lead to completing the use task) */
		flowableContext.getFormService().submitTaskFormData(proposalsTask.getId(), formEntries);
	}
	
	@SuppressWarnings("unchecked")
	private void auditItem(Integer expectedClientId) {
		/* get audited item owner */
		String auditedItemOwner = (String) flowableContext.getRuntimeService().getVariableLocal(processInstance.getId(), "auditedItemOwner");
		
		/* get expected owner address */
		HashMap<Integer, EtherAccount> accounts = (HashMap<Integer, EtherAccount>) flowableContext.getRuntimeService().getVariableLocal(processInstance.getId(), "accounts");
		String expectedOwner = accounts.get(expectedClientId).getCredentials().getAddress();
		assertTrue(auditedItemOwner.equals(expectedOwner));
	}
	
	@Test
	public void checkRegisterAndPaused() {
		/* Check process is paused at usertask1 */
		startProcess();
		assertNotNull(processInstance);
		
		/* get pending tasks */
		List<Task> list = flowableContext.getTaskService().createTaskQuery()
				.list();
		
		/* assert one pending task */
		assertTrue(list.size() == 1);
		
		/* assert pending task id */
		assertTrue(list.get(0).getTaskDefinitionKey().equals("usertask1"));
	}
	
	@Test
	public void checkRegisterAndAudit() {
		startProcess();
		fillAuditForm(anItemParameter);
		auditItem(Integer.valueOf(anOwnerParameter));
		
		/* assert process ended */
		HistoryService historyService = flowableContext.getHistoryService();
		HistoricProcessInstance historicProcessInstance = historyService
				.createHistoricProcessInstanceQuery()
				.processInstanceId(processInstance.getId()).singleResult();
		assertNotNull(historicProcessInstance);

		System.out.println("Process instance end time: "
				+ historicProcessInstance.getEndTime());
	}
	
	@Test
	public void checkRegisterRecordedInDB() throws SQLException {
		/* Start process. will pause at usertask1 */
		startProcess();
		
		/* Check records in Registered table match records in the Request table */
		
		Statement statement;
		ResultSet resultSet;
		
		/* First, we load all requested items into a list */
		ArrayList<String> requestedItems = new ArrayList<>();
		statement = dbCon.createStatement();
		resultSet = statement.executeQuery("SELECT * FROM Request");
		while (resultSet.next()) {
			String item = resultSet.getString("item");
			requestedItems.add(item);
		}
		
		/* Then, we load all registered items into a list */
		ArrayList<String> registeredItems = new ArrayList<>();
		statement = dbCon.createStatement();
		resultSet = statement.executeQuery("SELECT * FROM Registered");
		while (resultSet.next()) {
			String item = resultSet.getString("item");
			registeredItems.add(item);
		}
		
		/* Finally, we make sure that each requested item is registered, and no extra item was registered */
		for (String item : registeredItems) {
			assertTrue(registeredItems.contains(item));
		}
		assertTrue(registeredItems.size() == requestedItems.size());
	}
	
	@Test
	public void checkQueryResultsRecordedInDB() throws SQLException {
	    // Start the process
	    startProcess();

	    // Check records in Results table match the expected query results
	    Statement statement = dbCon.createStatement();
	    ResultSet resultSet = statement.executeQuery("SELECT * FROM Result");

	    // Assuming you have a method to get expected results
	    List<String[]> expectedResults = getExpectedResults(); // Implement this method

	    while (resultSet.next()) {
	        int id = resultSet.getInt("id");
	        String owner = resultSet.getString("owner");

	        // Find the expected result for this id and compare
	        boolean found = false;
	        for (String[] expectedResult : expectedResults) {
	            if (Integer.parseInt(expectedResult[0]) == id && expectedResult[1].equals(owner)) {
	                found = true;
	                break;
	            }
	        }
	        assertTrue(found);
	    }
	}

	private List<String[]> getExpectedResults() {
	    // Implement logic to get expected results
	    return Arrays.asList(
	        new String[]{"1", "ExpectedOwnerForQuery1"},
	        new String[]{"2", "ExpectedOwnerForQuery2"}
	        // Add more expected results as needed
	    );
	}

	
	@Test
	public void printActivityDetails() {
	    startProcess();

	    HistoryService historyService = flowableContext.getHistoryService();
	    List<HistoricActivityInstance> activities = historyService.createHistoricActivityInstanceQuery()
	                                                               .processInstanceId(processInstance.getId())
	                                                               .finished()
	                                                               .list();

	    for (HistoricActivityInstance activity : activities) {
	        System.out.println("Activity Name: " + activity.getActivityName() + ", End Time: " + activity.getEndTime());
	    }
	}



}
