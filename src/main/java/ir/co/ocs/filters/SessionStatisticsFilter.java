package ir.co.ocs.filters;

//import lombok.extern.log4j.Log4j;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequest;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class SessionStatisticsFilter extends IoFilterAdapter {
    private final AtomicInteger messagesRead = new AtomicInteger();
    private final AtomicInteger messagesWritten = new AtomicInteger();
    private final AtomicLong idleReadTime = new AtomicLong();
    private final AtomicLong idleWriteTime = new AtomicLong();
    private final AtomicLong openedSessionInTotal = new AtomicLong();
    private final AtomicLong openedSessionInInterval = new AtomicLong();
    private final AtomicLong exceptionOccurredInTotal = new AtomicLong();

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public SessionStatisticsFilter() {

        scheduler.scheduleAtFixedRate(this::logStatistics, 1, 10, TimeUnit.SECONDS);
    }


    @Override
    public void sessionCreated(NextFilter nextFilter, IoSession session) throws Exception {

        Thread.currentThread().setName(UUID.randomUUID().toString());
        super.sessionCreated(nextFilter, session);
    }

    @Override
    public void sessionOpened(NextFilter nextFilter, IoSession session) throws Exception {

        openedSessionInTotal.incrementAndGet();
        openedSessionInInterval.incrementAndGet();
        super.sessionOpened(nextFilter, session);
    }

    @Override
    public void messageReceived(NextFilter nextFilter, IoSession session, Object message) throws Exception {
        messagesRead.incrementAndGet();
        super.messageReceived(nextFilter, session, message);
    }

    @Override
    public void messageSent(NextFilter nextFilter, IoSession session, WriteRequest writeRequest) throws Exception {

        messagesWritten.incrementAndGet();
        super.messageSent(nextFilter, session, writeRequest);
    }

    @Override
    public void sessionIdle(NextFilter nextFilter, IoSession session, IdleStatus status) throws Exception {
        if (status == IdleStatus.READER_IDLE) {
            idleReadTime.addAndGet(System.currentTimeMillis() - session.getLastReadTime());
        } else if (status == IdleStatus.WRITER_IDLE) {
            idleWriteTime.addAndGet(System.currentTimeMillis() - session.getLastWriteTime());
        }
        super.sessionIdle(nextFilter, session, status);
    }

    @Override
    public void exceptionCaught(NextFilter nextFilter, IoSession session, Throwable cause) throws Exception {
//        log.error("Exception - >", cause);
        this.exceptionOccurredInTotal.incrementAndGet();
        super.exceptionCaught(nextFilter, session, cause);
    }

    private void logStatistics() {
//        log.info("Opened Session InTotal: " + openedSessionInTotal.get());
//        log.info("exception occured : " + exceptionOccurredInTotal.get());
//        log.info("Opened Session in Interval: " + openedSessionInInterval.getAndSet(0));
//        log.info("Messages Read: " + messagesRead.getAndSet(0));
//        log.info("Messages Written: " + messagesWritten.getAndSet(0));
//        log.info("Idle Read Time (ms): " + idleReadTime.getAndSet(0));
//        log.info("Idle Write Time (ms): " + idleWriteTime.getAndSet(0));
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("It Is called");
        scheduler.shutdown();  // Initiates an orderly shutdown
        try {
            if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();  // Force shutdown if not completed in 60 seconds
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();  // Force shutdown on interruption
            Thread.currentThread().interrupt();
        }
        super.destroy();
    }
}
