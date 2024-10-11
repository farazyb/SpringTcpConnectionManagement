package ir.co.ocs.envoriments.client;

import ir.co.ocs.envoriments.NetworkChannel;
import ir.co.ocs.envoriments.Repeater;
import ir.co.ocs.envoriments.State;
import ir.co.ocs.envoriments.StateService;
import ir.co.ocs.handler.NetworkChannelHandler;
import ir.co.ocs.socketconfiguration.BaseTcpSocketConfiguration;
import ir.co.ocs.socketconfiguration.TcpClientConfiguration;
import lombok.extern.log4j.Log4j2;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

@Component
@Log4j2
public class Client implements NetworkChannel {
    private TcpClientConfiguration tcpClientSocketConfiguration;
    private NioSocketConnector nioSocketConnector;
    private final DefaultIoFilterChainBuilder filterChainBuilder;
    private final StateService stateService;
    private Repeater repeater;

    public Client(DefaultIoFilterChainBuilder filterChainBuilder, StateService stateService) {
        this.filterChainBuilder = filterChainBuilder;
        this.stateService = stateService;


    }

    protected void initialize(TcpClientConfiguration configuration) {
        this.tcpClientSocketConfiguration = configuration;
        this.nioSocketConnector = configureSocketConnector();
        this.repeater = new Repeater(tcpClientSocketConfiguration.getMaxTry(), tcpClientSocketConfiguration.getInterval());
    }

    private NioSocketConnector configureSocketConnector() {

        this.nioSocketConnector = new NioSocketConnector();
        this.nioSocketConnector.setDefaultRemoteAddress(
                new InetSocketAddress(tcpClientSocketConfiguration.getHostAddress(),
                        tcpClientSocketConfiguration.getPort()));
        this.nioSocketConnector.setFilterChainBuilder(filterChainBuilder);
//        this.nioSocketConnector.setConnectTimeoutMillis(5000);
        this.nioSocketConnector.setHandler(new NetworkChannelHandler());
//        this.nioSocketConnector.setConnectTimeoutCheckInterval(500);
        return this.nioSocketConnector;
    }

    @Override
    public void start() {
        try {
//            log.info("start Connecting to {}", nioSocketConnector.getDefaultRemoteAddress());
//            ConnectFuture connectFuture = nioSocketConnector.connect();
//            connectFuture.awaitUninterruptibly();
//            IoSession session = connectFuture.getSession();
            repeater.connect(nioSocketConnector);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void stop() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public BaseTcpSocketConfiguration getBaseTcpSocketConfiguration() {
        return tcpClientSocketConfiguration;
    }

    @Override
    public void restart() {

    }

    @Override
    public State state() {
        return stateService.getState();
    }

}
