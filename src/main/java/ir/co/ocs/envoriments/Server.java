package ir.co.ocs.envoriments;

import ir.co.ocs.socketconfiguration.TcpServerConfiguration;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


@Component
@Scope("prototype")
public class Server {
    TcpServerConfiguration tcpServerConfiguration;


    private NioSocketAcceptor nioSocketAcceptor;


    public Server() {

        System.out.println("Create Server Instance");
    }

    public TcpServerConfiguration getTcpServerConfiguration() {
        return tcpServerConfiguration;
    }

    public void setTcpServerConfiguration(TcpServerConfiguration tcpServerConfiguration) {
        this.tcpServerConfiguration = tcpServerConfiguration;
    }

    public NioSocketAcceptor getNioSocketAcceptor() {
        return nioSocketAcceptor;
    }

    public void setNioSocketAcceptor(NioSocketAcceptor nioSocketAcceptor) {
        this.nioSocketAcceptor = nioSocketAcceptor;
    }
}
