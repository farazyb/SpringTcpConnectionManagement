package ir.co.ocs.connection.ssl;

import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;

public interface SSLManger {
    SSLContext createSSLContext() throws Exception;
}
