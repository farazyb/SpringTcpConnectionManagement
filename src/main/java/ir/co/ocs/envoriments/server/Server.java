package ir.co.ocs.envoriments.server;


import ir.co.ocs.envoriments.NetworkChannel;
import ir.co.ocs.envoriments.State;
import ir.co.ocs.envoriments.StateService;
import ir.co.ocs.envoriments.exceptions.NetworkBindingException;
import ir.co.ocs.filters.Filter;
import ir.co.ocs.handler.NetworkChannelHandler;
import ir.co.ocs.socketconfiguration.BaseTcpSocketConfiguration;
import ir.co.ocs.socketconfiguration.TcpServerConfiguration;
import lombok.extern.log4j.Log4j2;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;


@Component
@Scope("prototype")
@Log4j2
public class Server implements NetworkChannel {
    private TcpServerConfiguration tcpServerConfiguration;
    private final DefaultIoFilterChainBuilder filterChainBuilder;
    private NioSocketAcceptor nioSocketAcceptor;
    @Autowired
    private StateService state;

    public Server(DefaultIoFilterChainBuilder filterChainBuilder) {
        System.out.println("Server initialized");
        this.filterChainBuilder = filterChainBuilder;
    }

    public void initialize(TcpServerConfiguration configuration) {
        state.transitionTo(State.STOPPED);
        this.tcpServerConfiguration = configuration;
        this.nioSocketAcceptor = configureSocketAcceptor();

    }

    @Override
    public void start() {
        CompletableFuture<Boolean> future = bindSocketAcceptorAsync();
        try {
            future.join();
            state.transitionTo(State.RUNNING);
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
        acceptor.setHandler(new NetworkChannelHandler());
        return acceptor;
    }

    @Override
    public void stop() {
        if (nioSocketAcceptor != null && nioSocketAcceptor.isActive()) {
            nioSocketAcceptor.unbind();
            state.transitionTo(State.STOPPED);
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
            state.transitionTo(State.DISPOSE);
        }
    }

    @Override
    public BaseTcpSocketConfiguration getBaseTcpSocketConfiguration() {
        return tcpServerConfiguration;
    }

    @Override
    public State state() {
        return state.getState();
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
            e.printStackTrace();
            throw new RuntimeException("Unexpected error occurred during server start", e);
        }
    }

}
