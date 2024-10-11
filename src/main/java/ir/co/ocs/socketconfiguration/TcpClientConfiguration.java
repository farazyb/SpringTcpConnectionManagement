package ir.co.ocs.socketconfiguration;

public class TcpClientConfiguration extends BaseTcpSocketConfiguration {
    private String hostAddress;

    public String getHostAddress() {
        return hostAddress;
    }

    public void setHostAddress(String hostAddress) {
        this.hostAddress = hostAddress;
    }
}
