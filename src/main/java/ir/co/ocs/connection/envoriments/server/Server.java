package ir.co.ocs.connection.envoriments.server;


import ir.co.ocs.connection.envoriments.NetworkChannel;
import ir.co.ocs.connection.envoriments.State;
import ir.co.ocs.connection.envoriments.StateService;
import ir.co.ocs.connection.envoriments.exceptions.NetworkBindingException;
import ir.co.ocs.connection.filters.Filter;
import ir.co.ocs.connection.handler.NetworkChannelHandler;
import ir.co.ocs.connection.socketconfiguration.BaseTcpSocketConfiguration;
import ir.co.ocs.connection.socketconfiguration.TcpServerConfiguration;
import lombok.extern.log4j.Log4j2;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;


@Component
@Scope("prototype")
@Log4j2
public class Server implements NetworkChannel {
    private TcpServerConfiguration tcpServerConfiguration;
    private final DefaultIoFilterChainBuilder filterChainBuilder;
    private NioSocketAcceptor nioSocketAcceptor;
    private final StateService stateService;
    final
    NetworkChannelHandler networkChannelHandler;

    public Server(DefaultIoFilterChainBuilder filterChainBuilder, StateService stateService, NetworkChannelHandler networkChannelHandler) {
        System.out.println("Server initialized");
        this.filterChainBuilder = filterChainBuilder;
        stateService.transitionTo(State.DISPOSE);
        this.stateService = stateService;
        this.networkChannelHandler = networkChannelHandler;
    }

    public void initialize(TcpServerConfiguration configuration) {
        this.tcpServerConfiguration = configuration;
    }

    private void initialize() {
        Objects.requireNonNull(tcpServerConfiguration);
        this.nioSocketAcceptor = configureSocketAcceptor();
    }

    @Override
    public void start() {
        if (stateService.getState() == State.RUNNING) {
            log.warn("Server is already running.");
            return;
        }
        if (stateService.getState() == State.DISPOSE) {
            initialize();
        }
        CompletableFuture<Boolean> future = bindSocketAcceptorAsync();
        try {
            future.join();
            stateService.transitionTo(State.RUNNING);
        } catch (CompletionException e) {
            handleCompletionException(e);
        }
    }

    private CompletableFuture<Boolean> bindSocketAcceptorAsync() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                nioSocketAcceptor.bind();
                log.info("Server started on port: {}", nioSocketAcceptor.getLocalAddress().getPort());
                return true;
            } catch (IOException e) {
                throw new NetworkBindingException("Failed to bind NioSocketAcceptor", e);
            }
        });
    }

    private NioSocketAcceptor configureSocketAcceptor() {
        NioSocketAcceptor acceptor = new NioSocketAcceptor();
        acceptor.setDefaultLocalAddress(new InetSocketAddress(tcpServerConfiguration.getPort()));
        acceptor.setReuseAddress(true);
        acceptor.setFilterChainBuilder(filterChainBuilder);
        acceptor.getSessionConfig().setAll(tcpServerConfiguration);
        acceptor.setHandler(networkChannelHandler);
        return acceptor;
    }

    @Override
    public void stop() {
        if (nioSocketAcceptor != null && nioSocketAcceptor.isActive()) {
            nioSocketAcceptor.unbind();
            stateService.transitionTo(State.STOPPED);
        }
    }

    @Override
    public void restart() {
        stop();
        start();
        log.info("Client restarted.");
    }

    public void dispose() {
        if (nioSocketAcceptor != null) {
            nioSocketAcceptor.unbind();
            nioSocketAcceptor.dispose();
            nioSocketAcceptor = null;
            stateService.transitionTo(State.DISPOSE);
        }
    }

    @Override
    public BaseTcpSocketConfiguration getBaseTcpSocketConfiguration() {
        return tcpServerConfiguration;
    }

    @Override
    public State state() {
        return stateService.getState();
    }

    @Override
    public void addFilter(Filter filter) {
        filterChainBuilder.addLast(filter.getName(), filter.getIoFilterAdapter());
    }

    private void handleCompletionException(CompletionException e) {
        Throwable cause = e.getCause();
        if (cause instanceof NetworkBindingException) {
            throw (NetworkBindingException) cause;
        } else {
            throw new RuntimeException("Unexpected error occurred during server start", e);
        }
    }

}
