package ir.co.ocs.envoriments;

import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

public interface TcpServerConfigurationHandler {
    void setTcpServerConfiguration(NioSocketAcceptor server);
}
