package ir.co.ocs.filters;

import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequest;

public class ConnectionTypeFilter extends IoFilterAdapter {

    private boolean isPermanent = true;

    public ConnectionTypeFilter() {
    }

    public ConnectionTypeFilter(boolean isPermanent) {
        this.isPermanent = isPermanent;
    }

    @Override
    public void messageSent(NextFilter nextFilter, IoSession session, WriteRequest writeRequest) throws Exception {

        super.messageSent(nextFilter, session, writeRequest);
    }

    @Override
    public void exceptionCaught(NextFilter nextFilter, IoSession session, Throwable cause) throws Exception {

        super.exceptionCaught(nextFilter, session, cause);
    }
}
