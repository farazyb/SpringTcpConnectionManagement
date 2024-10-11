package ir.co.ocs.envoriments.server;


import ir.co.ocs.envoriments.State;
import ir.co.ocs.envoriments.exceptions.NetworkBindingException;
import ir.co.ocs.managers.Manager;
import ir.co.ocs.managers.ManagersException;
import ir.co.ocs.socketconfiguration.TcpServerConfiguration;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
@Log4j2
public class ServerManager extends Manager<Server> {
    private final ApplicationContext context;

    @Autowired
    public ServerManager(ApplicationContext context) {
        this.context = context;
    }

    public Server createServer(TcpServerConfiguration configuration) {
        Server server = context.getBean(Server.class);
        server.initialize(configuration);
        log.info("New server initialized by name {} ", configuration.getChannelIdentificationName());
        return server;
    }

}
