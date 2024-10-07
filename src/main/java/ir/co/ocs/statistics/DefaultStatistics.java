package ir.co.ocs.statistics;

import ir.co.ocs.filters.DefaultStatisticsFilter;
import org.apache.mina.core.filterchain.IoFilterAdapter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class DefaultStatistics implements Statistics {
    private final AtomicLong messagesReceived = new AtomicLong();
    private final AtomicLong messagesSent = new AtomicLong();
    private final AtomicInteger activeSessions = new AtomicInteger();
    private final AtomicInteger totalSessionsCreated = new AtomicInteger();
    private final AtomicInteger exceptionsOccurred = new AtomicInteger();
    private final AtomicLong idleReadTime = new AtomicLong();
    private final AtomicLong idleWriteTime = new AtomicLong();

    public void incrementMessagesReceived() {
        messagesReceived.incrementAndGet();
    }

    public void incrementMessagesSent() {
        messagesSent.incrementAndGet();
    }

    public void incrementActiveSessions() {
        activeSessions.incrementAndGet();
    }

    public void decrementActiveSessions() {
        activeSessions.decrementAndGet();
    }

    public void incrementTotalSessionsCreated() {
        totalSessionsCreated.incrementAndGet();
    }

    public void incrementExceptionsOccurred() {
        exceptionsOccurred.incrementAndGet();
    }

    public long getMessagesReceived() {
        return messagesReceived.get();
    }

    public long getMessagesSent() {
        return messagesSent.get();
    }

    public int getActiveSessions() {
        return activeSessions.get();
    }

    public int getTotalSessionsCreated() {
        return totalSessionsCreated.get();
    }

    public int getExceptionsOccurred() {
        return exceptionsOccurred.get();
    }

    @Override
    public Map<String, Object> getStatistics() {
        Map<String, Object> statistic = new HashMap<>();
        statistic.put("Messages Received", getMessagesReceived());
        statistic.put("Messages Sent", getMessagesSent());
        statistic.put("Active Sessions", getActiveSessions());
        statistic.put("Total Sessions Created", getTotalSessionsCreated());
        statistic.put("Exceptions Occurred", getExceptionsOccurred());
        return statistic;
    }

    @Override
    public IoFilterAdapter getStatisticsFilter() {
        return new DefaultStatisticsFilter();
    }
}
