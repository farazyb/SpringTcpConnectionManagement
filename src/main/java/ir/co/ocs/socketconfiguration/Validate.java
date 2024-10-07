package ir.co.ocs.socketconfiguration;

import ir.co.ocs.exceptions.TcpConfigIsNotValid;

public interface Validate {
    void validate()throws TcpConfigIsNotValid;
}
