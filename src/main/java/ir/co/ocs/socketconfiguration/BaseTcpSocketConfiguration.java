package ir.co.ocs.socketconfiguration;


import ir.co.ocs.exceptions.TcpConfigIsNotValid;
import ir.co.ocs.ssl.SSLManger;
import org.apache.mina.transport.socket.DefaultSocketSessionConfig;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Configuration class for TCP server socket settings.
 * <p>
 * This class extends {@link DefaultSocketSessionConfig} and is used to configure various aspects
 * of a TCP server socket, including the port, SSL settings, and protocol factories.
 * It utilizes Lombok annotations {@code @Getter} for generating getters and
 * {@code @Builder} for creating instances via the builder pattern.
 * </p>
 *
 * <p>
 * The following fields are available for configuration:
 * </p>
 *
 * <ul>
 *   <li>{@code channelIdentificationName}: A unique name used to identify the channel.</li>
 *   <li>{@code port}: The port number on which the server socket will listen.</li>
 *   <li>{@code ssl}: A boolean flag indicating whether SSL/TLS is enabled for the socket.</li>
 *   <li>{@code keyStorePath}: The file path to the keystore containing the server's private key and certificate.</li>
 *   <li>{@code trustStorePath}: The file path to the truststore containing the server's trusted certificates.</li>
 *   <li>{@code channelAttribute}: A map of attributes associated with the channel, used for custom configurations.</li>
 *   <li>{@code messageProtocolFactory}: A factory for creating message protocol instances.</li>
 *   <li>{@code protocolCodecFactory}: A factory for creating protocol codec instances, which handle the encoding and decoding of messages.</li>
 *   <li>{@code processor}: The processor responsible for handling the processing of incoming and outgoing messages.</li>
 * </ul>
 *
 * <p>
 * Example usage:
 * </p>
 *
 * <pre>{@code
 * DefaultTcpSocketConfiguration config = DefaultTcpSocketConfiguration.builder()
 *     .channelIdentificationName("MyServerChannel")
 *     .port(8080)
 *     .ssl(true)
 *     .keyStorePath("/path/to/keystore")
 *     .trustStorePath("/path/to/truststore")
 *     .channelAttribute(new HashMap<>())
 *     .messageProtocolFactory(new MyMessageProtocolFactory())
 *     .protocolCodecFactory(new MyProtocolCodecFactory())
 *     .processor(new MyProcessor())
 *     .build();
 * }</pre>
 *
 * @see DefaultSocketSessionConfig
 */

public class BaseTcpSocketConfiguration extends DefaultSocketSessionConfig implements Validate {
    private String channelIdentificationName;//mandatory
    private int port;//mandatory
    private SSLManger sslManger;
    private HashMap<Object, Object> channelAttribute;//optional if is not set default
    // private Processor processor;//mandatory
    private boolean permanent;
    private int sessionTimeOut;

    private int maxTry;
    private int interval;


    public BaseTcpSocketConfiguration() {
        setKeepAlive(true);
        permanent = true;
        channelAttribute = new HashMap<>();
    }


    @Override
    public void validate() throws TcpConfigIsNotValid {
        if (channelIdentificationName == null || channelIdentificationName.isEmpty()) {
            throw new TcpConfigIsNotValid("Channel Identification Name is mandatory.");
        }

        if (port <= 0) {
            throw new TcpConfigIsNotValid("Port must be a positive integer.");
        }

        if (!permanent) {
            if (sessionTimeOut > 0) {
                setBothIdleTime(sessionTimeOut);
            } else {
                setBothIdleTime(60);
            }

        }
    }

    public String getChannelIdentificationName() {
        return channelIdentificationName;
    }

    public void setChannelIdentificationName(String channelIdentificationName) {
        this.channelIdentificationName = channelIdentificationName;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public SSLManger getSslManger() {
        return sslManger;
    }

    public void setSslManger(SSLManger sslManger) {
        this.sslManger = sslManger;
    }

    public HashMap<Object, Object> getChannelAttribute() {
        return channelAttribute;
    }

    public void setChannelAttribute(HashMap<Object, Object> channelAttribute) {
        this.channelAttribute = channelAttribute;
    }

    public boolean isPermanent() {
        return permanent;
    }

    public void setPermanent(boolean permanent) {
        this.permanent = permanent;
    }

    public int getSessionTimeOut() {
        return sessionTimeOut;
    }

    public void setSessionTimeOut(int sessionTimeOut) {
        this.sessionTimeOut = sessionTimeOut;
    }

    public int getMaxTry() {
        return maxTry;
    }

    public void setMaxTry(int maxTry) {
        this.maxTry = maxTry;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }
}
