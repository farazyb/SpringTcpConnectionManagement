package ir.co.ocs.springtcpconnectionmanagement.managers.steps;

import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.BeforeStep;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import ir.co.ocs.envoriments.State;
import ir.co.ocs.envoriments.exceptions.NetworkBindingException;
import ir.co.ocs.envoriments.server.Server;
import ir.co.ocs.socketconfiguration.TcpServerConfiguration;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.net.InetSocketAddress;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest()
@ActiveProfiles("test")
public class ServerTest {
    @Autowired
    private DefaultIoFilterChainBuilder filterChainBuilder;

    @Mock
    private TcpServerConfiguration tcpServerConfiguration;

    @Mock
    private NioSocketAcceptor nioSocketAcceptor;

    @Mock
    private IoFilterAdapter filter;
    @Spy
    @InjectMocks
    private Server server;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);

    }

    @Given("a TcpServerConfiguration with port {int}")
    public void a_tcp_server_configuration_with_port(Integer port) {
        server = new Server(filterChainBuilder);
        when(tcpServerConfiguration.getPort()).thenReturn(port);
    }

    @When("the server is initialized with this configuration")
    public void the_server_is_initialized_with_this_configuration() {
        server.initialize(tcpServerConfiguration);
    }

    @Then("the server should be configured with the provided TcpServerConfiguration")
    public void the_server_should_be_configured_with_the_provided_configuration() {
        assertEquals(tcpServerConfiguration, server.getTcpServerConfiguration());
    }

    @Then("the server should not be {string}")
    public void the_server_should_not_be_running(String state) {
        assertNotEquals(State.valueOf(state), server.getState());
    }

    @Given("a server that is initialized with a valid TcpServerConfiguration")
    public void aServerThatIsInitializedWithAValidTcpServerConfiguration() {
        when(tcpServerConfiguration.getPort()).thenReturn(8080);
        server.initialize(tcpServerConfiguration);

    }

    @When("the server is started")
    public void theServerIsStarted() throws IOException {
        doNothing().when(nioSocketAcceptor).bind();
        server.start();
    }

    @Then("the server state should be {string}")
    public void theServerStateShouldBeRUNNING(String state) {
        assertEquals(State.valueOf(state), server.getState());
    }

    @Then("a NetworkBindingException should be thrown")
    public void aNetworkBindingExceptionShouldBeThrown() throws IOException {
        doThrow(new IOException()).when(nioSocketAcceptor).bind();
        assertThrows(NetworkBindingException.class, server::start);
    }

    @And("the server state should remain STOPPED")
    public void theServerStateShouldRemainSTOPPED() {
        assertEquals(State.STOPPED, server.getState());
    }

    @Given("a server that is started and is in RUNNING state")
    public void aServerThatIsStartedAndIsInRunningState() throws IOException {
        server = spy(new Server(filterChainBuilder));
        TcpServerConfiguration configuration = mock(TcpServerConfiguration.class);
        when(configuration.getPort()).thenReturn(8080);
        server.initialize(configuration);

        doNothing().when(nioSocketAcceptor).bind();
        InetSocketAddress socketAddress = new InetSocketAddress("localhost", 8080);
        doAnswer(invocation -> {
            when(nioSocketAcceptor.getLocalAddress()).thenReturn(socketAddress);
            return null; // Return type of bind() is void
        }).when(nioSocketAcceptor).bind();
        server.start();
        assertEquals(State.RUNNING, server.getState());
    }

    @When("the server is stopping")
    public void theServerIsStopping() {
        when(nioSocketAcceptor.isActive()).thenReturn(true);
        server.stop();
    }
}
