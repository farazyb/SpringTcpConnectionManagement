package ir.co.ocs.connection.envoriments.client;

import ir.co.ocs.connection.managers.Manager;
import ir.co.ocs.connection.socketconfiguration.TcpClientConfiguration;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class ClientManager extends Manager<Client> {

    final
    ObjectFactory<Client> clientObjectFactory;

    public ClientManager(ObjectFactory<Client> clientObjectFactory) {
        this.clientObjectFactory = clientObjectFactory;
    }


    public Client createClient(TcpClientConfiguration tcpClientConfiguration) {

        Client client = clientObjectFactory.getObject();
        client.initialize(tcpClientConfiguration);
        log.info("New client initialized by name {} ", tcpClientConfiguration.getChannelIdentificationName());
        return client;
    }

}
