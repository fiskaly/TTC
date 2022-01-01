package de.konfidas.ttc.messages.systemlogs;

import de.konfidas.ttc.exceptions.BadFormatForLogMessageException;
import de.konfidas.ttc.messages.SystemLogMessage;
import de.konfidas.ttc.utilities.DLTaggedObjectConverter;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DLTaggedObject;

import java.io.IOException;
import java.util.*;


/**
 * Diese Klasse repräsentiert eine deregisterClient Message. Dabei werden in der Methode
 * parseSystemOperationDataContent die folgenden Elemente aus systemOperationData geparst
 * <pre>
 * ╔═══════════════════════╤══════╤═══════════════════════════════════════════════════════════════╤════════════╗
 * ║ Data field            │ Tag  │ Data Type                                                     │ Mandatory? ║
 * ╠═══════════════════════╪══════╪═══════════════════════════════════════════════════════════════╪════════════╣
 * ║ client                │ 0x81 │ PrintableString                                               │ m          ║
 * ╚═══════════════════════╧══════╧════════════════════════════════════════════════════════════════════════════╝
 * </pre>
 */
public class DeregisterClientLogMessage extends SystemLogMessage {
    static Locale locale = new Locale("de", "DE");//NON-NLS
    static ResourceBundle properties = ResourceBundle.getBundle("ttc",locale);//NON-NLS

    public DLTaggedObject getClientID() {
        return clientID;
    }

    public void setClientID(DLTaggedObject clientID) {
        this.clientID = clientID;
    }

    public String getClientIDAsString() {
        return clientIDAsString;
    }

    public void setClientIDAsString(String clientIDAsString) {
        this.clientIDAsString = clientIDAsString;
    }

    DLTaggedObject clientID;
    String clientIDAsString;


    public DeregisterClientLogMessage(byte[] content, String filename) throws BadFormatForLogMessageException {
        super(content, filename);
    }

    @Override
    protected void parseSystemOperationDataContent(ASN1InputStream stream) throws IOException {

        ASN1Primitive systemOperationData = stream.readObject();
        if (!(systemOperationData instanceof ASN1Sequence))
            this.allErrors.add(new SystemLogParsingError(properties.getString("de.konfidas.ttc.messages.systemlogs.errorParsingSystemOperationDataContent")));

        List<ASN1Primitive> systemOperationDataAsAsn1List = Collections.list(((ASN1Sequence) systemOperationData).getObjects());
        ListIterator<ASN1Primitive> systemOperationDataIterator = systemOperationDataAsAsn1List.listIterator();

        try {
            //clientID einlesen
            DLTaggedObject nextElement = (DLTaggedObject) systemOperationDataAsAsn1List.get(systemOperationDataIterator.nextIndex());
            if (nextElement.getTagNo() != 1)
                this.allErrors.add(new SystemLogParsingError(properties.getString("de.konfidas.ttc.messages.systemlogs.errorUserIDNotFound")));

            this.clientID = (DLTaggedObject) systemOperationDataIterator.next();
            this.clientIDAsString = DLTaggedObjectConverter.dLTaggedObjectToString(this.clientID);
        } catch (NoSuchElementException ex) {
            this.allErrors.add( new SystemLogParsingError(properties.getString("de.konfidas.ttc.messages.systemlogs.errorEarlyEndOfSystemOperationData"), ex));
        }

    }
}