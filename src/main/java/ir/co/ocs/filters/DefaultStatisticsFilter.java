package ir.co.ocs.filters;

import ir.co.ocs.statistics.DefaultStatistics;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequest;

public class DefaultStatisticsFilter extends IoFilterAdapter {
    private final DefaultStatistics defaultStatistics;

    public DefaultStatisticsFilter() {
        defaultStatistics = new DefaultStatistics();
    }

    @Override
    public void sessionCreated(NextFilter nextFilter, IoSession session) throws Exception {
        defaultStatistics.incrementTotalSessionsCreated();
        defaultStatistics.incrementActiveSessions();
        System.out.println("New session created: " + session.getId());
        nextFilter.sessionCreated(session);
    }

    @Override
    public void sessionClosed(NextFilter nextFilter, IoSession session) throws Exception {
        defaultStatistics.decrementActiveSessions();
        System.out.println("Session closed: " + session.getId());
        nextFilter.sessionClosed(session);
    }

    @Override
    public void messageReceived(NextFilter nextFilter, IoSession session, Object message) throws Exception {
        defaultStatistics.incrementMessagesReceived();
        System.out.println("Message received: " + message);
        nextFilter.messageReceived(session, message);
    }

    @Override
    public void messageSent(NextFilter nextFilter, IoSession session, WriteRequest writeRequest) throws Exception {
        defaultStatistics.incrementMessagesSent();
        super.messageSent(nextFilter, session, writeRequest);
    }

    @Override
    public void exceptionCaught(NextFilter nextFilter, IoSession session, Throwable cause) throws Exception {
        defaultStatistics.incrementExceptionsOccurred();
        System.out.println("Exception occurred in session " + session.getId() + ": " + cause);
        nextFilter.exceptionCaught(session, cause);
    }
}