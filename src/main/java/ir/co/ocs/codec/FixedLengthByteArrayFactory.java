package ir.co.ocs.codec;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.springframework.stereotype.Component;

@Component
public class FixedLengthByteArrayFactory implements ProtocolCodecFactory {

    @Override
    public ProtocolEncoder getEncoder(IoSession session) throws Exception {
        return new FixedLengthByteArrayEncoder();
    }

    @Override
    public ProtocolDecoder getDecoder(IoSession session) throws Exception {
        return new FixedLengthByteArrayDecoder();
    }
}
