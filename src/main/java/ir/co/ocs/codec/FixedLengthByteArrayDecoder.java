package ir.co.ocs.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

public class FixedLengthByteArrayDecoder extends CumulativeProtocolDecoder {

    @Override
    protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
        // Check if there are at least 4 bytes (length prefix) available
        if (in.remaining() >= 4) {
            // Mark the current buffer position,so we can reset if necessary
            in.mark();

            // Read the 4-byte length prefix
            byte[] lengthBytes = new byte[4];
            in.get(lengthBytes);
            String lengthString = new String(lengthBytes).trim();

            // Parse the length from the 4-digit string
            int messageLength;
            try {
                messageLength = Integer.parseInt(lengthString);
            } catch (NumberFormatException e) {
                // If the length is not valid, reset the buffer and return false
                in.reset();
                return false;
            }

            // Check if the buffer has enough bytes for the entire message
            if (in.remaining() >= messageLength) {
                // Read the message bytes
                byte[] messageBytes = new byte[messageLength];
                in.get(messageBytes);

                // Output the decoded message
                out.write(messageBytes);

                // Return true indicating that the decoding was successful
                return true;
            } else {
                // Not enough bytes available for the full message, reset the buffer
                in.reset();
                return  false;
            }
        }

        // Not enough bytes available to read the length prefix, return false
        return false;
    }
}

