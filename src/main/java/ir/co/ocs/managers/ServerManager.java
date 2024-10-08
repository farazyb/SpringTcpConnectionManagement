package ir.co.ocs.managers;

import ir.co.ocs.config.DefaultConfiguration;
import ir.co.ocs.email.EmailDetails;
import ir.co.ocs.email.EmailService;
import ir.co.ocs.envoriments.LifeCycle;
import ir.co.ocs.envoriments.Server;
import ir.co.ocs.envoriments.State;
import ir.co.ocs.envoriments.TcpServerConfigurationHandler;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Log4j2
public class ServerManager implements Manager<Server> {
    @Autowired
    EmailService emailService;
    EmailDetails emailDetails;
    private final ConcurrentHashMap<String, Server> serversHashMap = new ConcurrentHashMap<>();

    public ServerManager() {
        emailDetails = new EmailDetails();
        emailDetails.setRecipient("farazyazdanibiuki@gmail.com");
        emailDetails.setSubject("Switch Notification");
    }

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
        emailDetails.setMessageBody("Server add");
        emailService.sendEmail(emailDetails);
        for (Server server : servers) {
            String channelIdentifier = server.getTcpServerConfiguration()
                    .getChannelIdentificationName();
            if (serversHashMap.containsKey(channelIdentifier)) {
                throw new ManagersException("Service with name '" + channelIdentifier + "' already exists.");
            }
            serversHashMap.put(channelIdentifier, server);
        }
    }

    @Override
    public void remove(String... identifiers) {
        emailDetails.setMessageBody("Server removed");
        emailService.sendEmail(emailDetails);
        for (String identifier : identifiers) {
            Server server = serversHashMap.get(identifier);
            if (server != null) {
                String channelInformation = server.getTcpServerConfiguration().getChannelIdentificationName();
                if (server.getState() != State.STOPPED) {
                    throw new ManagersException("Already server with name '" + identifier + "' is not stop , for remove  must stop server");
                }
                serversHashMap.remove(channelInformation);
                log.info("server {} removed", server.getTcpServerConfiguration().getChannelIdentificationName());
            } else {
                throw new ManagersException("server with name '" + identifier + "' not registered");
            }
        }
    }

    @Override
    public Server get(String identifier) {
        Server server = serversHashMap.get(identifier);
        if (server == null) {
            throw new ManagersException("server with name '" + identifier + "' not registered");
        }

        return server;
    }

    @Override
    public void shutdown() {
        for (Server server : serversHashMap.values()) {
            try {
                // Ensure the server is stopped or in a state that allows shutdown
                if (server.getState() != State.STOPPED) {
                    server.stop();  // Assuming stop() method exists in Server class
                }
                log.info("Shutting down server: {}", server.getTcpServerConfiguration().getChannelIdentificationName());
            } catch (Exception e) {
                throw new ManagersException("Failed to shut down server: " +
                        server.getTcpServerConfiguration().getChannelIdentificationName() + " -> " + e.getMessage());
            }
        }
        serversHashMap.clear();
        log.info("All servers have been successfully shut down and cleared from the manager.");

    }

    @Override
    public ConcurrentHashMap<String, Server> getServices() {
        return serversHashMap;
    }

}
