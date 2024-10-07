package ir.co.ocs.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

public class FixedLengthByteArrayEncoder extends ProtocolEncoderAdapter {

    @Override
    public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
        byte[] byteArrayMessage = (byte[]) message;
        int originalLength = byteArrayMessage.length;

        // Ensure the length is formatted as a 4-digit string with leading zeros
        String formattedLength = String.format("%04d", originalLength);

        // Convert the formatted length to a byte array
        byte[] lengthBytes = formattedLength.getBytes();

        // Allocate IoBuffer with capacity for the length (4 bytes) and the message itself
        IoBuffer ioBuffer = IoBuffer.allocate(lengthBytes.length + originalLength, false);

        // Write the formatted length as 4 bytes
        ioBuffer.put(lengthBytes);

        // Write the actual message bytes
        ioBuffer.put(byteArrayMessage);

        // Prepare the buffer for reading by flipping it
        ioBuffer.flip();

        // Write the IoBuffer to the ProtocolEncoderOutput
        out.write(ioBuffer);
    }
}

