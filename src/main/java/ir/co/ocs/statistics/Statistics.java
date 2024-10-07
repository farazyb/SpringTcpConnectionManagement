package ir.co.ocs.statistics;

import org.apache.mina.core.filterchain.IoFilterAdapter;

import java.util.Map;

public interface Statistics {
    public Map<String, Object> getStatistics();

    public IoFilterAdapter getStatisticsFilter();
}
