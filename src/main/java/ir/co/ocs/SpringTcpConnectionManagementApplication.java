package ir.co.ocs;

import ir.co.ocs.envoriments.Server;
import ir.co.ocs.managers.ServerManager;
import ir.co.ocs.socketconfiguration.TcpServerConfiguration;
import ir.co.ocs.statistics.Statistics;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringTcpConnectionManagementApplication implements CommandLineRunner {
    @Autowired
    ServerManager serverManager;

    public static void main(String[] args) {
        SpringApplication.run(SpringTcpConnectionManagementApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Server server = serverManager.getInstance();
        TcpServerConfiguration tcpServerConfiguration = new TcpServerConfiguration();
        tcpServerConfiguration.setChannelIdentificationName("Test");
        server.setTcpServerConfiguration(tcpServerConfiguration);
        serverManager.add(server);
        serverManager.remove("Test");


    }
}
