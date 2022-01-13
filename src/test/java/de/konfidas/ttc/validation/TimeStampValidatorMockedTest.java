package de.konfidas.ttc.validation;

import de.konfidas.ttc.errors.TtcError;
import de.konfidas.ttc.messages.LogMessage;
import de.konfidas.ttc.messages.logtime.LogTime;
import de.konfidas.ttc.messages.logtime.UnixLogTime;
import de.konfidas.ttc.tars.LogMessageArchive;
import de.konfidas.ttc.utilities.oid;
import org.bouncycastle.asn1.ASN1Primitive;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class TimeStampValidatorMockedTest {
    ArrayList<LogMessage> messages;

    public TimeStampValidatorMockedTest() {
        messages = new ArrayList<>();
    }

    class TestTar implements LogMessageArchive {
        @Override
        public Map<String, X509Certificate> getIntermediateCertificates() {
            return null;
        }

        @Override
        public Map<String, X509Certificate> getClientCertificates() {
            return null;
        }

        @Override
        public Collection<LogMessage> getLogMessages() {
            return messages;
        }

        @Override
        public Collection<? extends LogMessage> getSortedLogMessages() {
            return messages;
        }

        @Override
        public ArrayList<TtcError> getAllErrors() {
            return new ArrayList<TtcError>();
        }

        @Override
        public String getFileName() {
            return "";
        }
    }

    static class LogMessageMock implements LogMessage {
        LogTime time;
        BigInteger signatureCounter;

        LogMessageMock(LogTime time, BigInteger signatureCounter) {
            this.time = time;
            this.signatureCounter = signatureCounter;
        }

        @Override
        public ArrayList<TtcError> getAllErrors() {
            return new ArrayList<TtcError>();
        }

        @Override
        public LogTime getLogTime() {
            return time;
        }

        @Override
        public BigInteger getSignatureCounter() {
            return signatureCounter;
        }

        @Override
        public byte[] getSerialNumber() {
            return new byte[0];
        }

        @Override
        public String getFileName() {
            return null;
        }

        @Override
        public String getSignatureAlgorithm() {
            return null;
        }

        @Override
        public byte[] getDTBS() {
            return new byte[0];
        }

        @Override
        public byte[] getSignatureValue() {
            return new byte[0];
        }

        @Override
        public int getVersion() {
            return 0;
        }

        @Override
        public oid getCertifiedDataType() {
            return null;
        }

        @Override
        public Collection<ASN1Primitive> getSignatureAlgorithmParameters() {
            return null;
        }

        @Override
        public byte[] getSeAuditData() {
            return new byte[0];
        }

        @Override
        public byte[] getEncoded() {
            return new byte[0];
        }
    }

    @BeforeEach
    public void clean() {
        messages.clear();
    }


    @Test
    public void testEmpty() {
        TimeStampValidator validator = new TimeStampValidator();
        LogMessageArchive tar = new TestTar();

        assertTrue(validator.validate(tar).getValidationErrors().isEmpty());
    }

    @Test
    public void testTwoMessagesOk() {
        TimeStampValidator validator = new TimeStampValidator();
        LogMessageArchive tar = new TestTar();

        this.messages.add(new LogMessageMock(new UnixLogTime(1), BigInteger.ONE));
        this.messages.add(new LogMessageMock(new UnixLogTime(2), BigInteger.TWO));

        assertTrue(validator.validate(tar).getValidationErrors().isEmpty());
    }

    @Test
    public void testTwoMessagesNotOk() {
        TimeStampValidator validator = new TimeStampValidator();
        LogMessageArchive tar = new TestTar();

        this.messages.add(new LogMessageMock(new UnixLogTime(2), BigInteger.ONE));
        this.messages.add(new LogMessageMock(new UnixLogTime(1), BigInteger.TWO));

        assertTrue(validator.validate(tar).getValidationErrors().size() == 1);
    }

}
