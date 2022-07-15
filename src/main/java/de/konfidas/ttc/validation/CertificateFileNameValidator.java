package de.konfidas.ttc.validation;

import de.konfidas.ttc.exceptions.CertificateInconsistentToFilenameException;
import de.konfidas.ttc.exceptions.ValidationException;
import de.konfidas.ttc.tars.LogMessageArchive;
import de.konfidas.ttc.utilities.CertificateHelper;
import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPublicKey;
import java.util.*;

public class CertificateFileNameValidator implements Validator {

    static Locale locale = new Locale("de", "DE");//NON-NLS
    static ResourceBundle properties = ResourceBundle.getBundle("ttc",locale);//NON-NLS

    public static void validateCertificateAgainstFilename(X509Certificate cert, String filename) throws CertificateInconsistentToFilenameException {
        X500Name certSubject;
        try {
            certSubject = new JcaX509CertificateHolder(cert).getSubject();
        } catch (CertificateEncodingException e) {
            throw new CertificateInconsistentToFilenameException(String.format(properties.getString("de.konfidas.ttc.validation.consistencyErrorForCert"), filename),e);
        }

        RDN cn = certSubject.getRDNs(BCStyle.CN)[0];

        String certSubjectClean = IETFUtils.valueToString(cn.getFirst().getValue()).toUpperCase();

        String keyHashFromFilename = filename.split("_")[0].toUpperCase();

        if (!(certSubjectClean.contains(keyHashFromFilename))){
            throw new CertificateInconsistentToFilenameException.FilenameToSubjectMismatchException(certSubjectClean, keyHashFromFilename);
        }

        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new CertificateInconsistentToFilenameException(String.format(properties.getString("de.konfidas.ttc.validation.consistencyErrorForCert"),filename),e);
        }

        byte[] encodedCertPublicKey = CertificateHelper.publicKeyToUncompressedPoint((ECPublicKey) cert.getPublicKey());

        byte[] hash = digest.digest(encodedCertPublicKey);
        String sha256HexString = Hex.encodeHexString(hash).toUpperCase();

        if (!(sha256HexString.equals(keyHashFromFilename))){
            throw new CertificateInconsistentToFilenameException.FilenameToPubKeyMismatchException(filename, sha256HexString);
        }
    }

    @Override
    public ValidationResult validate(LogMessageArchive tar) {
        LinkedList<ValidationException> errors = new LinkedList<>();

        for(Map.Entry<? extends String,? extends X509Certificate> entry : tar.getClientCertificates().entrySet()) {
            try {
                validateCertificateAgainstFilename(entry.getValue(),entry.getKey());
            }catch (CertificateInconsistentToFilenameException e) {
                errors.add(e);
            }
        }
        return new ValidationResultImpl().append(Collections.singleton(this), errors);
    }
}
