package de.konfidas.ttc.messages.systemlogs;

import de.konfidas.ttc.exceptions.BadFormatForLogMessageException;
import de.konfidas.ttc.messages.SystemLogMessage;
import de.konfidas.ttc.utilities.DLTaggedObjectConverter;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DLTaggedObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;


/**
 * Diese Klasse repräsentiert eine authenticateUserSystemLog Message. Dabei werden in der Methode
 * parseSystemOperationDataContent die folgenden Elemente aus systemOperationData geparst
 * <pre>
 * ╔═══════════════════════╤══════╤═══════════════════════════════════════════════════════════════╤════════════╗
 * ║ Data field            │ Tag  │ Data Type                                                     │ Mandatory? ║
 * ╠═══════════════════════╪══════╪═══════════════════════════════════════════════════════════════╪════════════╣
 * ║ userID                │ 0x81 │ PrintableString                                               │ m          ║
 * ╟───────────────────────┼──────┼───────────────────────────────────────────────────────────────┼────────────╢
 * ║ role                  │ 0x82 │ ENUMERATED{ admin, timeAdmin }                                │ c          ║
 * ╟───────────────────────┼──────┼───────────────────────────────────────────────────────────────┼────────────╢
 * ║ authenticationResult  │ 0x83 │ BOOLEAN                                                       │ m          ║
 * ╟───────────────────────┼──────┼───────────────────────────────────────────────────────────────┼────────────╢
 * ║ remainingRetries      │ 0x84 │ INTEGER                                                       │ o          ║
 * ╚═══════════════════════╧══════╧══════════════════════════════════╧═════════════════════════════════════════╝
 * </pre>
 */
public class AuthenticateSmaersAdminSystemLogMessage extends SystemLogMessage {
    final static Logger logger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);

    static Locale locale = new Locale("de", "DE"); //NON-NLS
    static ResourceBundle properties = ResourceBundle.getBundle("ttc",locale); //NON-NLS


    public DLTaggedObject getUserId() {
        return userId;
    }

    public void setUserId(DLTaggedObject userId) {
        this.userId = userId;
    }

    public DLTaggedObject getRole() {
        return role;
    }

    public void setRole(DLTaggedObject role) {
        this.role = role;
    }

    public DLTaggedObject getAuthenticationResult() {
        return authenticationResult;
    }

    public void setAuthenticationResult(DLTaggedObject authenticationResult) {
        this.authenticationResult = authenticationResult;

    }

    public DLTaggedObject getRemainingRetries() {
        return remainingRetries;
    }

    public void setRemainingRetries(DLTaggedObject remainingRetries) {
        this.remainingRetries = remainingRetries;
    }

    public String getUserIDAsString() {
        return userIDAsString;
    }

    public void setUserIDAsString(String userIDAsString) {
        this.userIDAsString = userIDAsString;
    }

    public BigInteger getRoleAsBigInteger() {
        return roleAsBigInteger;
    }

    public void setRoleAsBigInteger(BigInteger roleAsBigInteger) {
        this.roleAsBigInteger = roleAsBigInteger;
    }

    public boolean isAuthenticationResultAsBoolean() {
        return authenticationResultAsBoolean;
    }

    public void setAuthenticationResultAsBoolean(boolean authenticationResultAsBoolean) {
        this.authenticationResultAsBoolean = authenticationResultAsBoolean;
    }

    public BigInteger getRemainingRetriesAsBigInteger() {
        return remainingRetriesAsBigInteger;
    }

    public void setRemainingRetriesAsBigInteger(BigInteger remainingRetriesAsBigInteger) {
        this.remainingRetriesAsBigInteger = remainingRetriesAsBigInteger;
    }

    DLTaggedObject userId;
    DLTaggedObject role;
    DLTaggedObject authenticationResult;
    DLTaggedObject remainingRetries;

    String userIDAsString;
    BigInteger roleAsBigInteger;
    boolean authenticationResultAsBoolean;
    BigInteger remainingRetriesAsBigInteger;



    public AuthenticateSmaersAdminSystemLogMessage(byte[] content, String filename) throws BadFormatForLogMessageException {
        super(content, filename);
    }


    @Override
        protected void parseSystemOperationDataContent(ASN1InputStream stream) throws  IOException {

        ASN1Primitive systemOperationData = stream.readObject();
        if (!(systemOperationData instanceof ASN1Sequence)){
            this.allErrors.add(new SystemLogParsingError(properties.getString("de.konfidas.ttc.messages.systemlogs.errorParsingSystemOperationDataContent")));
        }

//            throw new SystemLogParsingException(properties.getString("de.konfidas.ttc.messages.systemlogs.errorParsingSystemOperationDataContent"));

        List<ASN1Primitive> systemOperationDataAsAsn1List = Collections.list(((ASN1Sequence) systemOperationData).getObjects());
        ListIterator<ASN1Primitive> systemOperationDataIterator = systemOperationDataAsAsn1List.listIterator();

        try {
            //userID einlesen
            DLTaggedObject nextElement = (DLTaggedObject) systemOperationDataAsAsn1List.get(systemOperationDataIterator.nextIndex());
            if (nextElement.getTagNo() != 1) allErrors.add(new SystemLogParsingError(properties.getString("de.konfidas.ttc.messages.systemlogs.errorUserIDNotFound")));

            this.userId = (DLTaggedObject) systemOperationDataIterator.next();
            this.userIDAsString = DLTaggedObjectConverter.dLTaggedObjectToString(this.userId);

            //role einlesen
            nextElement = (DLTaggedObject) systemOperationDataAsAsn1List.get(systemOperationDataIterator.nextIndex());
            if (nextElement.getTagNo() != 2) {logger.debug(properties.getString("de.konfidas.ttc.messages.systemlogs.errorUnblockResultNotFound"));}
            else {
                this.role = (DLTaggedObject) systemOperationDataIterator.next();
                this.roleAsBigInteger = DLTaggedObjectConverter.dLTaggedObjectFromEnumerationToBigInteger(this.role);
            }

            //authenticationResult einlesen
             nextElement = (DLTaggedObject) systemOperationDataAsAsn1List.get(systemOperationDataIterator.nextIndex());
            if (nextElement.getTagNo() != 3) this.allErrors.add(new SystemLogParsingError(properties.getString("de.konfidas.ttc.messages.systemlogs.errorAuthenticationResultNotFound")));

            this.authenticationResult = (DLTaggedObject) systemOperationDataIterator.next();
            this.authenticationResultAsBoolean = DLTaggedObjectConverter.dLTaggedObjectToBoolean(this.authenticationResult);

            //remainingRetries einlesen
            nextElement = (DLTaggedObject) systemOperationDataAsAsn1List.get(systemOperationDataIterator.nextIndex());
            if (nextElement.getTagNo() != 4) {logger.debug("The field remainingRetries has not been found.");}  //NON-NLS
            else {
                this.remainingRetries = (DLTaggedObject) systemOperationDataIterator.next();
                this.remainingRetriesAsBigInteger = DLTaggedObjectConverter.dLTaggedObjectFromEnumerationToBigInteger(this.remainingRetries);
            }


        }
        catch (NoSuchElementException ex){
            this.allErrors.add( new SystemLogParsingError(properties.getString("de.konfidas.ttc.messages.systemlogs.errorEarlyEndOfSystemOperationData"), ex));
        }
    }


}