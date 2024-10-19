package ir.co.ocs.connection.socketconfiguration;

import ir.co.ocs.connection.exceptions.TcpConfigIsNotValid;

public interface Validate {
    void validate()throws TcpConfigIsNotValid;
}
