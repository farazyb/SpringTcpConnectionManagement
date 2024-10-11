package ir.co.ocs.envoriments.client;

import ir.co.ocs.managers.Manager;
import ir.co.ocs.managers.ManagersException;
import ir.co.ocs.socketconfiguration.TcpClientConfiguration;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
@Log4j2
public class ClientManager extends Manager<Client> {

    final ApplicationContext applicationContext;

    public ClientManager(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public Client createClient(TcpClientConfiguration tcpClientConfiguration) {

        Client client = applicationContext.getBean(Client.class);
        client.initialize(tcpClientConfiguration);
        log.info("New client initialized by name {} ", tcpClientConfiguration.getChannelIdentificationName());
        return client;
    }

}
