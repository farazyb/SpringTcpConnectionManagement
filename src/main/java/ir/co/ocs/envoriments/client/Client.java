package ir.co.ocs.envoriments.client;

import ir.co.ocs.envoriments.NetworkChannel;
import ir.co.ocs.envoriments.Repeater;
import ir.co.ocs.envoriments.State;
import ir.co.ocs.envoriments.StateService;
import ir.co.ocs.envoriments.exceptions.NetworkBindingException;
import ir.co.ocs.filters.Filter;
import ir.co.ocs.handler.NetworkChannelHandler;
import ir.co.ocs.socketconfiguration.BaseTcpSocketConfiguration;
import ir.co.ocs.socketconfiguration.TcpClientConfiguration;
import lombok.extern.log4j.Log4j2;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.springframework.beans.factory.annotation.Autowired;
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
    private IoSession session;

    public Client(DefaultIoFilterChainBuilder filterChainBuilder, StateService stateService) {
        this.filterChainBuilder = filterChainBuilder;
        this.stateService = stateService;
        stateService.transitionTo(State.DISPOSE);


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
        this.nioSocketConnector.getSessionConfig().setAll(tcpClientSocketConfiguration);
        this.nioSocketConnector.setFilterChainBuilder(filterChainBuilder);
        this.nioSocketConnector.setHandler(new NetworkChannelHandler());
        return this.nioSocketConnector;
    }

    @Override
    public void start() {
        if (stateService.getState() == State.RUNNING) {
            log.warn("Client is already running.");
            return;
        }

        try {
            session = repeater.connect(nioSocketConnector);
            stateService.transitionTo(State.RUNNING);
            log.info("Client started and connected to: {}:{}",
                    tcpClientSocketConfiguration.getHostAddress(), tcpClientSocketConfiguration.getPort());
        } catch (NetworkBindingException e) {
            log.error("Failed to start client due to network binding issue: ", e);
            stateService.transitionTo(State.DISPOSE);
        }

    }

    @Scheduled(fixedDelay = 500)
    @Async
    public void monitorConnectionActivation() {
        if (stateService.getState() == State.RUNNING && (session == null || !session.isActive())) {
            log.warn("Connection lost. Attempting to reconnect...");
            stateService.transitionTo(State.STOPPED);
            start();
        }
    }

    @Scheduled(fixedDelay = 5000)
    @Async
    public void monitorConnectionIdle() {
        if (stateService.getState() == State.RUNNING) {

            long lastIoTime = session.getLastIoTime();
            long currentTime = System.currentTimeMillis();
            long difference = currentTime - lastIoTime;

            if (tcpClientSocketConfiguration.getMaxIdleToReset() > 0 && difference > tcpClientSocketConfiguration.getMaxIdleToReset()) {
                log.info("Max idle time happened try to restart  {} : {}", tcpClientSocketConfiguration.getHostAddress(), tcpClientSocketConfiguration.getPort());
                restart();
            }
        }
    }

    @Override
    public void stop() {
        if (session != null && session.isConnected()) {
            session.closeNow();
            stateService.transitionTo(State.STOPPED);
            log.info("Client stopped successfully.");
        } else {
            log.warn("Client stop called, but session is not connected or is null.");
        }
    }

    @Override
    public void dispose() {
        if (nioSocketConnector.isDisposed() && stateService.getState() == State.DISPOSE) {
            stop();
            if (nioSocketConnector != null) {
                nioSocketConnector.dispose();
                nioSocketConnector = null;
            }
            stateService.transitionTo(State.DISPOSE);
            log.info("Client resources disposed.");
        } else {
            log.warn("Dispose called, but client is already disposed.");
        }
    }

    @Override
    public BaseTcpSocketConfiguration getBaseTcpSocketConfiguration() {
        return tcpClientSocketConfiguration;
    }

    @Override
    public void restart() {
        if (stateService.getState() != State.DISPOSE) {
            stateService.transitionTo(State.RESTARTING);
            stop();
            start();
            log.info("Client restarted.");
        } else {
            log.error("Cannot restart a disposed client. Please reinitialize.");
        }
    }

    @Override
    public State state() {
        return stateService.getState();
    }

    @Override
    public void addFilter(Filter filter) {
        Objects.requireNonNull(filter, "Filter cannot be null");
        filterChainBuilder.addLast(filter.getName(), filter.getIoFilterAdapter());
        log.info("Filter added: {}", filter.getName());
    }

}
