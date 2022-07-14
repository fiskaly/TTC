package de.konfidas.ttc.validation;

import de.konfidas.ttc.exceptions.BadFormatForTARException;
import de.konfidas.ttc.exceptions.CertificateLoadException;
import de.konfidas.ttc.exceptions.ValidationException;
import de.konfidas.ttc.tars.LogMessageArchiveImplementation;
import de.konfidas.ttc.utilities.CertificateHelper;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.security.Security;
import java.security.cert.CertificateExpiredException;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class CertificateFileNameValidatorTest {

  @BeforeEach
  public void initialize() {
    Security.addProvider(new BouncyCastleProvider());
  }

  @Test
  public void testCertificateFileName() throws IOException, BadFormatForTARException {

    LogMessageArchiveImplementation archive = new LogMessageArchiveImplementation(new File("/home/max/go/src/animamea/TTC/exports/6eb330a7-3985-4879-8f9f-bcd97d47792f.tar"));

    final var validator = new CertificateFileNameValidator();
    final var result = validator.validate(archive);
    System.out.println(result);
  }

}
