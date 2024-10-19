package ir.co.ocs.connection.envoriments;

import ir.co.ocs.connection.filters.Filter;
import ir.co.ocs.connection.socketconfiguration.BaseTcpSocketConfiguration;

public interface NetworkChannel {


    public void start();

    public void stop();

    public void restart();

    public void dispose();

    public BaseTcpSocketConfiguration getBaseTcpSocketConfiguration();

    public State state();

    void addFilter(Filter filter);

}
