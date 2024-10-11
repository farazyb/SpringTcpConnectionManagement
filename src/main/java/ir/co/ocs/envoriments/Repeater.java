package ir.co.ocs.envoriments;

import ir.co.ocs.envoriments.exceptions.NetworkBindingException;
import lombok.extern.log4j.Log4j2;
import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.Arrays;

@Log4j2
public class Repeater {
    private RetryTemplate retryTemplate;
    private int maxTry;
    private int interval;

    public Repeater(int maxTry, int interval) {
        this.maxTry = maxTry;
        this.interval = interval;
        retryTemplate = createRetryTemp(maxTry, interval);
    }

    private RetryTemplate createRetryTemp(int maxTry, int interval) {
        return RetryTemplate.builder()
                .maxAttempts(maxTry)
                .fixedBackoff(interval)
                .retryOn(Arrays.asList(RuntimeIoException.class, IllegalStateException.class,NetworkBindingException.class))
                .build();
    }


    public void connect(NioSocketConnector nioSocketConnector) {
        try {


            ConnectFuture connectFuture = retryTemplate.execute(context -> {

                log.info("try to connect {}th", context.getRetryCount());
                ConnectFuture future = nioSocketConnector.connect();
                future.awaitUninterruptibly();
                if (!future.isConnected()) {
                    throw new NetworkBindingException("cant connect to " + nioSocketConnector.getDefaultRemoteAddress());
                }
                return future;
            });

        } catch (NetworkBindingException e) {
            e.printStackTrace();
            log.error("error {}",e.getMessage());

        }

        log.info("end");
    }

}
