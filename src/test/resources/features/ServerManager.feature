Feature: Server management

  Scenario: Successfully add a new server
    Given the server does not exist
    When I add the server
    Then the server should be added successfully

  Scenario: Add a duplicate server
    Given the server already exists
    When I add the server
    Then an exception should be thrown with message "Server with identifier 'server1' already exists."

  Scenario: Successfully remove a stopped server
    Given the server is stopped
    When I remove the server
    Then the server should be removed

  Scenario: Try to remove a running server
    Given the server is running
    When I remove the server
    Then an exception should be thrown with message "Server 'server1' is not stopped. Stop it before removal."