package ir.co.ocs.envoriments;

import ir.co.ocs.socketconfiguration.BaseTcpSocketConfiguration;

public interface NetworkChannel {


    public void start();

    public void stop();

    public void restart();

    public void dispose();

    public BaseTcpSocketConfiguration getBaseTcpSocketConfiguration();

    public State state();

}
