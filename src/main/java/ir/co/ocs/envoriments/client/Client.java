package ir.co.ocs.envoriments.client;

import ir.co.ocs.envoriments.NetworkChannel;
import ir.co.ocs.envoriments.Repeater;
import ir.co.ocs.envoriments.State;
import ir.co.ocs.envoriments.StateService;
import ir.co.ocs.filters.Filter;
import ir.co.ocs.handler.NetworkChannelHandler;
import ir.co.ocs.socketconfiguration.BaseTcpSocketConfiguration;
import ir.co.ocs.socketconfiguration.TcpClientConfiguration;
import lombok.extern.log4j.Log4j2;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.Objects;

@Component
@Log4j2
public class Client implements NetworkChannel {
    private TcpClientConfiguration tcpClientSocketConfiguration;
    private NioSocketConnector nioSocketConnector;
    private final DefaultIoFilterChainBuilder filterChainBuilder;
    private final StateService stateService;
    private Repeater repeater;
    private boolean shouldMonitor = false;

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
        this.nioSocketConnector.setHandler(new NetworkChannelHandler());
        return this.nioSocketConnector;
    }

    @Override
    public void start() {
        try {
            repeater.connect(nioSocketConnector);
            shouldMonitor = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Scheduled(fixedDelay = 500)
    @Async
    public void monitorConnection() {
        if (shouldMonitor)
            if (!nioSocketConnector.isActive()) {
                start();
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

    @Override
    public void addFilter(Filter filter) {
        Objects.requireNonNull(filter);
        filterChainBuilder.addLast(filter.getName(), filter.getIoFilterAdapter());
    }

}
