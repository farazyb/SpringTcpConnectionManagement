package ir.co.ocs.envoriments.server;


import ir.co.ocs.envoriments.State;
import ir.co.ocs.envoriments.exceptions.NetworkBindingException;
import ir.co.ocs.managers.Manager;
import ir.co.ocs.managers.ManagersException;
import ir.co.ocs.socketconfiguration.TcpServerConfiguration;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
@Log4j2
public class ServerManager extends Manager<Server> {
    @Autowired
    ObjectFactory<Server> serverObjectFactory;


    public Server createServer(TcpServerConfiguration configuration) {
        Server server = serverObjectFactory.getObject();
        server.initialize(configuration);
        log.info("New server initialized by name {} ", configuration.getChannelIdentificationName());
        return server;
    }

}
