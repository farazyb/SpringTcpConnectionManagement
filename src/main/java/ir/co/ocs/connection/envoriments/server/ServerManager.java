package ir.co.ocs.connection.envoriments.server;


import ir.co.ocs.connection.managers.Manager;
import ir.co.ocs.connection.socketconfiguration.TcpServerConfiguration;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
