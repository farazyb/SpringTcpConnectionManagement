package ir.co.ocs.socketconfiguration;

public class TcpClientConfiguration extends BaseTcpSocketConfiguration {
    private String hostAddress;
    private long maxIdleToReset;

    public String getHostAddress() {
        return hostAddress;
    }

    public void setHostAddress(String hostAddress) {
        this.hostAddress = hostAddress;
    }

    public long getMaxIdleToReset() {
        return maxIdleToReset;
    }

    public void setMaxIdleToReset(long maxIdleToReset) {
        this.maxIdleToReset = maxIdleToReset;
    }
}
