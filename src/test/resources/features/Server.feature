Feature: Server

  Scenario: Initialize server with configuration
    Given a TcpServerConfiguration with port 8080
    When the server is initialized with this configuration
    Then the server should be configured with the provided TcpServerConfiguration
    And the server state should be "STOPPED"

  Scenario: Start server successfully
    Given a TcpServerConfiguration with port 8081
    When the server is initialized with this configuration
    And the server is started
    Then the server state should be "RUNNING"

  Scenario: Fail to start server due to network binding issue
    Given a TcpServerConfiguration with port 8081
    When the server is initialized with this configuration
    Then a NetworkBindingException should be thrown
    And the server state should remain STOPPED

  Scenario: Stop running server
    Given a server that is started and is in RUNNING state
    When the server is stopping
    Then the server state should be "STOPPED"