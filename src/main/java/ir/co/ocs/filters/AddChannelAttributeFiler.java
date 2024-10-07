package ir.co.ocs.filters;

import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IoSession;

import java.util.HashMap;

public class AddChannelAttributeFiler extends IoFilterAdapter {
    private HashMap<Object, Object> channelAttribute;

    public AddChannelAttributeFiler(HashMap<Object, Object> channelAttribute) {
        this.channelAttribute = channelAttribute;
    }

    @Override
    public void sessionOpened(NextFilter nextFilter, IoSession session) throws Exception {
        addChannelAttribute(session);
        super.sessionOpened(nextFilter, session);
    }

    public void setChannelAttribute(HashMap<Object, Object> channelAttribute) {
        this.channelAttribute = channelAttribute;
    }

    private void addChannelAttribute(IoSession session) {
        if (channelAttribute != null && !channelAttribute.isEmpty()) {
            channelAttribute.forEach(session::setAttribute);
        }
    }
}
