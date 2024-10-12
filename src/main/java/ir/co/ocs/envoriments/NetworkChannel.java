package ir.co.ocs.envoriments;

import ir.co.ocs.filters.Filter;
import ir.co.ocs.socketconfiguration.BaseTcpSocketConfiguration;
import org.apache.mina.core.filterchain.IoFilterAdapter;

public interface NetworkChannel {


    public void start();

    public void stop();

    public void restart();

    public void dispose();

    public BaseTcpSocketConfiguration getBaseTcpSocketConfiguration();

    public State state();

    void addFilter(Filter filter);

}
