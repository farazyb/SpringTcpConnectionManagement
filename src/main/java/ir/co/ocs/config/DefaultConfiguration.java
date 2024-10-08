package ir.co.ocs.config;

import ir.co.ocs.codec.FixedLengthByteArrayFactory;
import ir.co.ocs.ssl.SSLManger;
import ir.co.ocs.statistics.DefaultStatistics;
import ir.co.ocs.statistics.Statistics;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.filter.logging.MdcInjectionFilter;
import org.apache.mina.filter.ssl.SslFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class DefaultConfiguration {

    @Bean
    @Scope("prototype")
    public DefaultIoFilterChainBuilder defaultIoFilterChainBuilder(ExecutorFilter executorFilter, MdcInjectionFilter mdcInjectionFilter,
                                                                   ProtocolCodecFilter protocolCodecFilter,
                                                                   LoggingFilter loggingFilter,
                                                                   Statistics statistics,
                                                                   Optional<SslFilter> sslFilterOptional) {
        DefaultIoFilterChainBuilder defaultIoFilterChainBuilder = new DefaultIoFilterChainBuilder();
        Map<String, IoFilter> filters = new LinkedHashMap<>();
        filters.put("executor", executorFilter);
        filters.put("mdcInjectionFilter", mdcInjectionFilter);
        filters.put("codecFilter", protocolCodecFilter);
        filters.put("loggingFilter", loggingFilter);
        filters.put("Statistic", statistics.getStatisticsFilter());
        sslFilterOptional.ifPresent(sslFilter -> filters.put("sslFilter", sslFilter));
        defaultIoFilterChainBuilder.setFilters(filters);
        return defaultIoFilterChainBuilder;
    }

    @Bean
    @Scope("prototype")
    public Optional<SslFilter> sslFilter(Optional<SSLManger> sslManagerOptional) {
        return sslManagerOptional.map(sslManager -> {
            try {
                return new SslFilter(sslManager.createSSLContext());
            } catch (Exception e) {
                throw new RuntimeException("Error creating SSLFilter", e);
            }
        });
    }

    @Bean
    @Scope("prototype")
    public ExecutorFilter executorFilter() {
        return new ExecutorFilter();
    }

    @Bean
    @Scope("prototype")
    public MdcInjectionFilter mdcInjectionFilter() {
        return new MdcInjectionFilter(MdcInjectionFilter.MdcKey.remoteAddress);
    }

    @Bean
    @Scope("prototype")
    public ProtocolCodecFilter protocolCodecFilter(FixedLengthByteArrayFactory fixedLengthByteArrayFactory) {
        return new ProtocolCodecFilter(fixedLengthByteArrayFactory);
    }

    @Bean
    @Scope("prototype")
    public LoggingFilter loggingFilter() {
        return new LoggingFilter();
    }

    @Bean
    @Scope("prototype")
    public Statistics statistics() {
        return new DefaultStatistics();
    }

}
