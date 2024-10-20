package ir.co.ocs.connection.handler;

import ir.co.ocs.connection.sessionmanager.SessionManager;
import lombok.extern.log4j.Log4j2;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.FilterEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@Log4j2
public class NetworkChannelHandler extends IoHandlerAdapter {
    @Autowired
    SessionManager sessionManager;

    public NetworkChannelHandler() {

    }

    @Override
    public final void inputClosed(IoSession session) throws Exception {
        super.inputClosed(session);
    }

    @Override
    public final void sessionOpened(IoSession session) throws Exception {
        sessionManager.add(session);
        log.info("Session stored: {} ", session.getId());

    }

    @Override
    public final void sessionCreated(IoSession session) throws Exception {
        log.info("Adding channel attribute");
        log.info("Session TimeOut : {}", session.getConfig().getWriteTimeout());
    }

    @Override
    public final void messageReceived(IoSession session, Object message) throws Exception {
        System.out.println(message);
    }


    @Override
    public final void event(IoSession session, FilterEvent event) throws Exception {

    }

    @Override
    public final void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        if (status == IdleStatus.BOTH_IDLE) {
            log.error("No response sent for 60 seconds, closing session : {}", session.getId());
            session.closeNow();  // Close the session if it's idle for more than 60 seconds
        }
    }

    @Override
    public final void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        log.error(cause.getMessage());
        closeSession(session);
    }

    @Override
    public final void messageSent(IoSession session, Object message) throws Exception {
    }

    @Override
    public final void sessionClosed(IoSession session) throws Exception {
        log.info("Session closed: {}", session.getId());
        // Remove the session from the map
        sessionManager.remove(session.getId());
        log.info("Session removed: {}", session.getId());
    }

    private void closeSession(IoSession session) {
        session.closeNow();
        log.info("Session forcibly closed and removed: {}", session.getId());
    }
}
