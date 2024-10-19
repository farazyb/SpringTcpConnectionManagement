package ir.co.ocs.connection.sessionmanager;

import org.apache.mina.core.session.IoSession;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
@Scope("singleton")
public class SessionManager {
    private final ConcurrentHashMap<Long, IoSession> sessions;
    private final int timeout=60000; // Timeout in seconds
    private final boolean permanent=true;

    public SessionManager() {
        this.sessions = new ConcurrentHashMap<>();

        manageTimeout();
    }

    public long add(IoSession ioSession) {
        sessions.put(ioSession.getId(), ioSession);
        return ioSession.getId();
    }

    public void remove(Long sessionId) {
        IoSession session = sessions.remove(sessionId);
        if (session != null && session.isConnected()) {
            session.closeNow(); // Closes the session if still connected
        }
    }

    public IoSession get(Long sessionId) {
        return sessions.get(sessionId);
    }

    @Scheduled(fixedDelay = 10000)
    private void manageTimeout() {


        long currentTime = System.currentTimeMillis();
        sessions.forEach((id, session) -> {
            if (!session.isClosing() && session.isConnected()) {
                long idleTime = currentTime - session.getLastIoTime();
                if (idleTime >= timeout * 1000 && !isPermanent()) {
                    remove(id);
                }
            }
        });

    }

    private boolean isPermanent() {
        return permanent;
    }

}
