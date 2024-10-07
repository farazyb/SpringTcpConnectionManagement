package ir.co.ocs.managers;

import ir.co.ocs.config.DefaultConfiguration;
import ir.co.ocs.envoriments.LifeCycle;
import ir.co.ocs.envoriments.Server;
import ir.co.ocs.envoriments.TcpServerConfigurationHandler;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Log4j2
public class ServerManager implements Manager<Server> {
    TcpServerConfigurationHandler tcpServerConfigurationHandler;
    private final ConcurrentHashMap<String, Server> serversHashMap = new ConcurrentHashMap<>();


    @Lookup
    protected Server server() {
        return null;
    }


    @Override
    public Server getInstance() {
        return server();
    }

    @Override
    public void add(Server... servers) {
        for (Server server : servers) {
            String channelIdentifier = server.getTcpServerConfiguration()
                    .getChannelIdentificationName();
            if (serversHashMap.containsKey(channelIdentifier)) {
                throw new IllegalArgumentException("Service with name '" + channelIdentifier + "' already exists.");
            }
            serversHashMap.put(channelIdentifier, server);
        }
    }

    @Override
    public void remove(String... identifiers) {
        for (String identifier : identifiers) {
            Server server = serversHashMap.get(identifier);
            if (server != null) {
                log.info("server {} stopped", server.getTcpServerConfiguration().getChannelIdentificationName());
            } else {
                throw new IllegalArgumentException("server with name '" + identifier + "' not registered");
            }
        }
    }

    @Override
    public Server get(String identifier) {
        return null;
    }

    @Override
    public void shutdown() {

    }

}
