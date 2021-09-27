package de.konfidas.ttc.messages.systemlogs;

import de.konfidas.ttc.exceptions.BadFormatForLogMessageException;
import de.konfidas.ttc.messages.SystemLogMessage;
import de.konfidas.ttc.utilities.DLTaggedObjectConverter;
import org.bouncycastle.asn1.*;
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
 * ║ updateResult          │ 0x81 │ ENMERATED                                                     │ m          ║
 * ╟───────────────────────┼──────┼───────────────────────────────────────────────────────────────┼────────────╢
 * ║ reasonForFailure      │ 0x81 │ PrintableString                                               │ c          ║
 * ╟───────────────────────┼──────┼───────────────────────────────────────────────────────────────┼────────────╢
 * ║ newVersion            │ 0x84 │ OCTECTSTRING                                                  │ m          ║
 * ╚═══════════════════════╧══════╧══════════════════════════════════╧═════════════════════════════════════════╝
 * </pre>
 */
public class UpdateDeviceCompletedSystemLogMessage extends SystemLogMessage {
    final static Logger logger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    static Locale locale = new Locale("de", "DE"); //NON-NLS
    static ResourceBundle properties = ResourceBundle.getBundle("ttc",locale);//NON-NLS



    DLTaggedObject userId;
    DLTaggedObject reasonForFailure;
    DLTaggedObject oldVersion;


    String userIDAsString;
    BigInteger updateResultAsBigInteger;
    String reasonForFailureAsString;
    String newVersionComponentName;
    String newVersionManufacturer;
    String newVersionModel;
    String newVersionVersion;
    String newVersionCertificationID;



    public UpdateDeviceCompletedSystemLogMessage(byte[] content, String filename) throws BadFormatForLogMessageException {
        super(content, filename);
    }


    @Override
        protected void parseSystemOperationDataContent(ASN1InputStream stream) throws IOException {

        ASN1Primitive systemOperationData = stream.readObject();
        if (!(systemOperationData instanceof ASN1Sequence)) this.allErrors.add(new SystemLogParsingError(properties.getString("de.konfidas.ttc.messages.systemlogs.errorParsingSystemOperationDataContent")));

        List<ASN1Primitive> systemOperationDataAsAsn1List = Collections.list(((ASN1Sequence) systemOperationData).getObjects());
        ListIterator<ASN1Primitive> systemOperationDataIterator = systemOperationDataAsAsn1List.listIterator();

        try {
            //userID einlesen
            DLTaggedObject nextElement = (DLTaggedObject) systemOperationDataAsAsn1List.get(systemOperationDataIterator.nextIndex());
            if (nextElement.getTagNo() != 1) this.allErrors.add(new SystemLogParsingError(properties.getString("de.konfidas.ttc.messages.systemlogs.errorParsingSystemOperationDataContentUserIDNotFound")));

            this.userId = (DLTaggedObject) systemOperationDataIterator.next();
            this.userIDAsString = DLTaggedObjectConverter.dLTaggedObjectToString(this.userId);

            //oldVersion einlesen
             nextElement = (DLTaggedObject) systemOperationDataAsAsn1List.get(systemOperationDataIterator.nextIndex());
            if (nextElement.getTagNo() != 3) this.allErrors.add(new SystemLogParsingError(properties.getString("de.konfidas.ttc.messages.systemlogs.errorParsingSystemOperationDataContentOldVersionNotFound")));

            this.oldVersion = (DLTaggedObject) systemOperationDataIterator.next();

            //oldVersion is a Sequence in itself. Parsing follows

            if (this.oldVersion.getObject() instanceof ASN1Sequence) {

                List<ASN1Primitive> deviceInformationSetAsASN1List = Collections.list(((ASN1Sequence) this.oldVersion.getObject()).getObjects());
                ListIterator<ASN1Primitive> deviceInformationSetIterator = deviceInformationSetAsASN1List.listIterator();

                if (!deviceInformationSetIterator.hasNext()) this.allErrors.add(new SystemLogParsingError(properties.getString("de.konfidas.ttc.messages.systemlogs.errorDeviceInformationSetEndedEarly")));

                List<ASN1Primitive> componentInformationSetAsASN1 = Collections.list(((ASN1Sequence) deviceInformationSetAsASN1List.get(deviceInformationSetIterator.nextIndex())).getObjects());
                ListIterator<ASN1Primitive> componentInformationSetItertator = componentInformationSetAsASN1.listIterator();

                //component Name
                if (!deviceInformationSetIterator.hasNext()) this.allErrors.add(new SystemLogParsingError(properties.getString("de.konfidas.ttc.messages.systemlogs.errorComponentInformationSetOfUpdateTimeEndedEarly")));
                ASN1Primitive element = deviceInformationSetIterator.next();
                this.newVersionComponentName = ((ASN1String) element).getString();

                //manufacturer
                if (!deviceInformationSetIterator.hasNext()) this.allErrors.add(new SystemLogParsingError(properties.getString("de.konfidas.ttc.messages.systemlogs.errorComponentInformationSetOfUpdateTimeEndedEarly")));
                element = deviceInformationSetIterator.next();
                this.newVersionManufacturer = ((ASN1String) element).getString();

                //model
                if (!deviceInformationSetIterator.hasNext()) this.allErrors.add(new SystemLogParsingError(properties.getString("de.konfidas.ttc.messages.systemlogs.errorComponentInformationSetOfUpdateTimeEndedEarly")));
                element = deviceInformationSetIterator.next();
                this.newVersionModel = ((ASN1String) element).getString();

                //version
                if (!deviceInformationSetIterator.hasNext()) this.allErrors.add(new SystemLogParsingError(properties.getString("de.konfidas.ttc.messages.systemlogs.errorComponentInformationSetOfUpdateTimeEndedEarly")));

                element = deviceInformationSetIterator.next();
                this.newVersionVersion = ((ASN1String) element).getString();                //version

                if (deviceInformationSetIterator.hasNext()) {
                element = deviceInformationSetIterator.next();
                this.newVersionCertificationID = ((ASN1String) element).getString();
                }

            }
            else this.allErrors.add(new SystemLogParsingError(properties.getString("de.konfidas.ttc.messages.systemlogs.errorParsingSystemOperationDataContentOldVersionDoesNotStartWithSequence")));




        }
        catch (NoSuchElementException ex){
            this.allErrors.add(new SystemLogParsingError(properties.getString("de.konfidas.ttc.messages.systemlogs.errorParsingSystemOperationDataContentEarlyEnd"), ex));
        }
    }


}