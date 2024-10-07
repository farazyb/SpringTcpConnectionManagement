package ir.co.ocs.ssl;

import org.springframework.stereotype.Component;

import javax.net.ssl.SSLContext;

public interface SSLManger {
    SSLContext createSSLContext() throws Exception;
}
