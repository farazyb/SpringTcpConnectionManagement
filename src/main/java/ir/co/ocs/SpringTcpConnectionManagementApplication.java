package ir.co.ocs;

import ir.co.ocs.envoriments.client.Client;
import ir.co.ocs.envoriments.client.ClientManager;
import ir.co.ocs.envoriments.server.Server;
import ir.co.ocs.envoriments.server.ServerManager;
import ir.co.ocs.socketconfiguration.TcpClientConfiguration;
import ir.co.ocs.socketconfiguration.TcpServerConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SpringTcpConnectionManagementApplication implements CommandLineRunner {
    @Autowired
    ServerManager serverManager;
    @Autowired
    ClientManager clientManager;
    @Autowired
    private Environment env;

    public static void main(String[] args) {
        SpringApplication.run(SpringTcpConnectionManagementApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        if (!env.acceptsProfiles("test")) {
//            TcpServerConfiguration tcpServerConfiguration = new TcpServerConfiguration();
//            tcpServerConfiguration.setPort(8085);
//            tcpServerConfiguration.setChannelIdentificationName("server");
//            Server server = serverManager.createServer(tcpServerConfiguration);
//            server.start();
//            serverManager.addService(server);
            TcpClientConfiguration tcpClientConfiguration = new TcpClientConfiguration();
            tcpClientConfiguration.setHostAddress("localhost");
            tcpClientConfiguration.setPort(8080);
            tcpClientConfiguration.setChannelIdentificationName("client");
            tcpClientConfiguration.setMaxTry(10);
            tcpClientConfiguration.setInterval(500);
            tcpClientConfiguration.setPermanent(true);
            tcpClientConfiguration.setMaxIdleToReset(40000);
            Client client = clientManager.createClient(tcpClientConfiguration);
            client.start();
            clientManager.addService(client);



//        serverManager.remove("Test");
        }

    }
}
