package ir.co.ocs.connection.filters;

import lombok.Getter;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.filterchain.IoFilterAdapter;


@Getter
public class Filter {
    public Filter(IoFilterAdapter ioFilterAdapter, String name) {
        this.ioFilterAdapter = ioFilterAdapter;
        this.name = name;
    }

    private IoFilterAdapter ioFilterAdapter;
    private String name;

}
