package ir.co.ocs.springtcpconnectionmanagement.managers;

import ir.co.ocs.envoriments.Server;
import ir.co.ocs.envoriments.State;
import ir.co.ocs.envoriments.TcpServerConfigurationHandler;
import ir.co.ocs.managers.ManagersException;
import ir.co.ocs.managers.ServerManager;
import ir.co.ocs.socketconfiguration.TcpServerConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class ServerManagerTest {

    private ServerManager serverManager;

    @Mock
    private Server mockServer;

    @Mock
    private TcpServerConfiguration mockConfig;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        serverManager = new ServerManager();

        // Mock Server configuration
        when(mockServer.getTcpServerConfiguration()).thenReturn(mockConfig);
        when(mockConfig.getChannelIdentificationName()).thenReturn("server1");
    }

    @Test
    void testAddNewServerSuccessfully() {
        // Add a new server and verify it was added correctly
        serverManager.add(mockServer);
        Server retrievedServer = serverManager.get("server1");

        assertEquals(mockServer, retrievedServer, "The server should be retrieved successfully after being added.");
    }

    @Test
    void testAddDuplicateServerThrowsException() {
        // Add the server once
        serverManager.add(mockServer);

        // Attempt to add the same server again and expect an exception
        Exception exception = assertThrows(ManagersException.class, () -> serverManager.add(mockServer));
        assertEquals("Service with name 'server1' already exists.", exception.getMessage());
    }

    @Test
    void testRemoveServerSuccessfully() {
        when(mockServer.getState()).thenReturn(State.STOPPED);
        serverManager.add(mockServer);

        // Act
        serverManager.remove("server1");

        // Assert
        assertFalse(serverManager.getServices().containsKey("server1"),
                "Server should be removed from the serversHashMap after calling remove.");
    }

    @Test
    void testRemoveServerNotStoppedThrowsException() {
        // Set the server state to RUNNING
        when(mockServer.getState()).thenReturn(State.RUNNING);

        // Add the server and then attempt to remove it, expecting an exception
        serverManager.add(mockServer);
        Exception exception = assertThrows(ManagersException.class, () -> serverManager.remove("server1"));
        assertEquals("Already server with name 'server1' is not stop , for remove  must stop server", exception.getMessage());
    }

    @Test
    void testRemoveNonExistentServerThrowsException() {
        // Attempt to remove a server that was never added and expect an exception
        Exception exception = assertThrows(ManagersException.class, () -> serverManager.remove("nonExistentServer"));
        assertEquals("server with name 'nonExistentServer' not registered", exception.getMessage());
    }

    @Test
    void testGetNonExistentServerThrowsException() {
        // Attempt to get a server that was never added and expect an exception
        Exception exception = assertThrows(ManagersException.class, () -> serverManager.get("nonExistentServer"));
        assertEquals("server with name 'nonExistentServer' not registered", exception.getMessage());
    }

    @Test
    void testShutdown() {
        // Placeholder for shutdown logic; verify shutdown behavior as per implementation details
        serverManager.shutdown();
        // Assertions based on shutdown behavior (e.g., checking if all servers are removed or stopped)
    }
}



