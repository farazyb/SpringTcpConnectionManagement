package ir.co.ocs.envoriments;


import ir.co.ocs.envoriments.exceptions.NetworkBindingException;
import ir.co.ocs.filters.Filter;
import ir.co.ocs.handler.NetworkChannelHandler;
import ir.co.ocs.managers.ManagersException;
import ir.co.ocs.socketconfiguration.TcpServerConfiguration;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;


@Component
@Scope("prototype")
public class Server implements LifeCycle {
    private TcpServerConfiguration tcpServerConfiguration;
    private State state;
    final
    DefaultIoFilterChainBuilder defaultIoFilterChainBuilder;


    private NioSocketAcceptor nioSocketAcceptor;


    public Server(DefaultIoFilterChainBuilder defaultIoFilterChainBuilder) {
        state = State.STOPPED;
        System.out.println("Create Server Instance");
        this.defaultIoFilterChainBuilder = defaultIoFilterChainBuilder;
    }

    public TcpServerConfiguration getTcpServerConfiguration() {
        return tcpServerConfiguration;
    }

    public void setTcpServerConfiguration(TcpServerConfiguration tcpServerConfiguration) {
        this.tcpServerConfiguration = tcpServerConfiguration;
    }


    public void setNioSocketAcceptor(NioSocketAcceptor nioSocketAcceptor) {
        this.nioSocketAcceptor = nioSocketAcceptor;
    }

    public State getState() {
        return state;
    }

    @Override
    public void start() {
        bindSocketAcceptorAsync()
                .handle(this::handleBindingResult);
    }

    private CompletableFuture<Boolean> bindSocketAcceptorAsync() {
        return CompletableFuture.supplyAsync(() -> {
            NioSocketAcceptor nioSocketAcceptor = configureSocketAcceptor();
            try {
                nioSocketAcceptor.bind();
                return true;
            } catch (Exception e) {
                throw new NetworkBindingException("Failed to bind NioSocketAcceptor", e);
            }
        });
    }

    private NioSocketAcceptor configureSocketAcceptor() {
        NioSocketAcceptor nioSocketAcceptor = new NioSocketAcceptor();
        nioSocketAcceptor.setDefaultLocalAddress(new InetSocketAddress(tcpServerConfiguration.getPort()));
        nioSocketAcceptor.setReuseAddress(true);
        nioSocketAcceptor.setFilterChainBuilder(defaultIoFilterChainBuilder);
        nioSocketAcceptor.setHandler(new NetworkChannelHandler());
        return nioSocketAcceptor;
    }

    private Boolean handleBindingResult(Boolean result, Throwable exception) {
        if (exception != null) {
            throw new ManagersException("Error during binding", exception);
        }
        state = result ? State.RUNNING : State.STOPPED;
        return result;
    }

    @Override
    public void restart() {
        try {
            state = State.RESTARTING;
            stop(); // Stop the server safely
            start(); // Start the server again
            state = State.RUNNING;
        } catch (Exception e) {
            throw new ManagersException("Failed to restart the server", e);
        }
    }

    @Override
    public void stop() {
        try {
            if (nioSocketAcceptor != null && nioSocketAcceptor.isActive()) {
                nioSocketAcceptor.unbind();
                state = State.STOPPED;
            }
        } catch (Exception e) {
            throw new ManagersException("Failed to unbind the NioSocketAcceptor", e);
        }
    }

    @Override
    public void stopAndRemove() {
        try {
            if (nioSocketAcceptor != null && nioSocketAcceptor.isActive()) {
                nioSocketAcceptor.unbind();
            }

            if (nioSocketAcceptor != null) {
                nioSocketAcceptor.dispose();
                nioSocketAcceptor = null; // Clear the reference to fully remove it
                state = State.DISPOSE;
            }
        } catch (Exception e) {
            throw new ManagersException("Failed to stop and remove the NioSocketAcceptor", e);
        }
    }

    public void addFilterChain(Filter... filters) {
        for (Filter filter : filters) {
            defaultIoFilterChainBuilder.addLast(filter.getName(), filter.getIoFilterAdapter());
        }

    }

    private void setState(State state) {
        this.state = state;
    }
}
