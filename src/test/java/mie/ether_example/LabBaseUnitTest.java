package mie.ether_example;
import org.junit.*;


import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.Rule;


import edu.toronto.dbservice.config.MIE354DBHelper;

import org.flowable.engine.test.FlowableRule;
import org.flowable.engine.*;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.repository.Deployment;
import org.h2.tools.Server;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;


public abstract class LabBaseUnitTest {

	public static String filename = null;

	@Rule
	public FlowableRule flowableContext  = new FlowableRule();
	
	@Rule
	public TestWatcher watchman= new TestWatcher() {
		
		@Override
		protected void starting(Description description) {
		      System.out.println("### Starting test: " + description.getMethodName() + " ###");
		}
		
	    @Override
	    protected void failed(Throwable e, Description description) {
	    	System.out.println("### Failed test: " + description.getMethodName() + " ###");
	    }

	    @Override
	    protected void succeeded(Description description) {
	    	System.out.println("### Succeeded test: " + description.getMethodName() + " ###");
	       }
	};

	public Connection dbCon = null;
	public ProcessInstance processInstance;
	private Deployment deployment;
	private static Server server = null;
	  
	@Before
	public void deploy() throws Exception {
		RepositoryService repositoryService = flowableContext.getRepositoryService();
		try {
			deployment = repositoryService.createDeployment().addInputStream("process1.bpmn20.xml",
					new FileInputStream(filename)).deploy();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		MIE354DBHelper.main(null);
		dbCon = MIE354DBHelper.getDBConnection();
	}
	
	@BeforeClass
	public static void startServer() {
		try {
			server = Server.createTcpServer();
			server.start();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@AfterClass
	public static void stopServer() {
		server.stop();
	}
		
	@After
	public void endProcess() {
		flowableContext.getRepositoryService().deleteDeployment(deployment.getId(), true);
		try {
			Runtime.getRuntime().exec("taskkill /FI \"WINDOWTITLE eq TestRpc*\"");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	}