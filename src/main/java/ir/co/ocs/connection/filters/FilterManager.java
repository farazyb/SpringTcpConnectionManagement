package ir.co.ocs.connection.filters;

import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.service.IoService;

public interface FilterManager {
    void addFilter(String name, IoFilterAdapter filter);
    void setDefaultFilter(IoService ioService);
}
