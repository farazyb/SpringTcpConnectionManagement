package ir.co.ocs.springtcpconnectionmanagement.managers.steps;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import ir.co.ocs.envoriments.server.Server;
import ir.co.ocs.envoriments.State;
import ir.co.ocs.managers.ManagersException;
import ir.co.ocs.envoriments.server.ServerManager;
import ir.co.ocs.socketconfiguration.TcpServerConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@CucumberContextConfiguration
@SpringBootTest()
@ActiveProfiles("test")
public class ServerManagerTest{

    @Autowired
    private ServerManager serverManager;

    private Server mockServer;
    private TcpServerConfiguration mockConfig;
    private Exception caughtException;

    @Before
    public void setUp() {
        mockServer = mock(Server.class);
        mockConfig = mock(TcpServerConfiguration.class);

        when(mockServer.getTcpServerConfiguration()).thenReturn(mockConfig);
        when(mockConfig.getChannelIdentificationName()).thenReturn("server1");
        caughtException = null;
    }

    @After
    public void clear() {
        serverManager.getServices().clear();
    }

    @Given("the server does not exist")
    public void serverDoesNotExist() {
        // No setup needed as serverManager is empty
    }

    @Given("the server already exists")
    public void serverAlreadyExists() throws ManagersException {
        serverManager.addService(mockServer);
    }

    @Given("the server is stopped")
    public void serverIsStopped() throws ManagersException {
        when(mockServer.getState()).thenReturn(State.STOPPED);
        serverManager.addService(mockServer);
    }

    @Given("the server is running")
    public void serverIsRunning() throws ManagersException {
        when(mockServer.getState()).thenReturn(State.RUNNING);
        serverManager.addService(mockServer);
    }

    @When("I add the server")
    public void iAddTheServer() {
        try {
            serverManager.addService(mockServer);
        } catch (ManagersException e) {
            caughtException = e;
        }
    }

    @When("I remove the server")
    public void iRemoveTheServer() {
        try {
            serverManager.remove("server1");
        } catch (ManagersException e) {
            caughtException = e;
        }
    }

    @Then("the server should be added successfully")
    public void serverShouldBeAddedSuccessfully() {
        Server retrievedServer = serverManager.get("server1");
        assertEquals(mockServer, retrievedServer);
    }

    @Then("the server should be removed")
    public void serverShouldBeRemoved() {
        assertNull(serverManager.get("server1"));
    }
    @Then("an exception should be thrown with message {string}")
    public void exceptionShouldBeThrown(String expectedMessage) {
        assertNotNull(caughtException);
        assertEquals(expectedMessage, caughtException.getMessage());
    }
}



