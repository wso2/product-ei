/*
* Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
* WSO2 Inc. licenses this file to you under the Apache License,
* Version 2.0 (the "License"); you may not use this file except
* in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied. See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.wso2.ei.connector.as2.message;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.synapse.MessageContext;
import org.apache.synapse.commons.vfs.VFSConstants;
import org.apache.synapse.core.axis2.Axis2MessageContext;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.mail.smime.SMIMEEnvelopedGenerator;
import org.bouncycastle.mail.smime.SMIMESignedGenerator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CertStore;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Properties;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.bouncycastle.util.encoders.Base64;
import org.wso2.ei.connector.as2.security.KeyCertManager;
import org.wso2.ei.connector.as2.util.AS2Constants;

/**
 * Generates the whole signed and encrypted mime package
 */
public class SMIMEMessageCreator {
    public static final Log log = LogFactory.getLog(SMIMEMessageCreator.class);

    /**
     * Create the SMIME package
     *
     * @param messageContext synapse message context
     * @param configurations configs from send_template
     */
    public void createMimeMessage(MessageContext messageContext, Map configurations) {
        // Take axis2 message context from synapse message context
        org.apache.axis2.context.MessageContext axis2MessageCtx =
                ((Axis2MessageContext) messageContext).getAxis2MessageContext();

        // Take the content from the vfs - BinaryBuilder - content is base64 formatted
        String content = axis2MessageCtx.getEnvelope().getBody().getFirstElement().getText();

        // Get a session
        Properties systemProperties = System.getProperties();
        Session session = Session.getDefaultInstance(systemProperties);

        // Creates mime message
        MimeMessage mimeMessage = new MimeMessage(session);

        try {
            // SET THE CONTENT
            mimeMessage.setText(content);

            // TODO: 11/28/17 comment below
            /*
            Content in text variable is already formatted to base64, but here content will again get formatted
            according to the Content-Transfer-Encoding header.
            When the Content-Transfer-Encoding is set to "binary", the content is added without any formatting.
            But as the content is all ready formatted to base64, the header is wrong.
            When the Content-Transfer-Encoding is set to "base64", the content is again formatted into base64.
            But as the content is already in base64, the content gets wrong when it is converted again to base64.

            So have commented setting the Content-Transfer-Encoding header to binary/base64 here.
            "Content-Transfer-Encoding: 7bit" header will get set by the javax.mail api.
             */
//            mimeMessage.setHeader(AS2Constants.CONTENT_TRANSFER_ENCODING, AS2Constants.BINARY);

            // Take file name from axis2 transport headers map
            String fileName = (String) ((Map)axis2MessageCtx.getProperty(axis2MessageCtx.TRANSPORT_HEADERS))
                    .get(VFSConstants.FILE_NAME);
            // Set file name to MIME message
            mimeMessage.setFileName(fileName);

            // Set the content-type header
            mimeMessage.setHeader(AS2Constants.CONTENT_TYPE, AS2Constants.APPLICATION_EDI_CONSENT);
            // Save message
            mimeMessage.saveChanges();

            // Register BouncyCastleProvider in java security before begin anything with BouncyCastle
            Security.addProvider(new BouncyCastleProvider());


            // SIGNING
            // Sign generator for signing
            SMIMESignedGenerator signedGenerator = new SMIMESignedGenerator();

            // Take keys and certs needed for signing process
            KeyCertManager keyCertManager = new KeyCertManager((String) configurations.get(AS2Constants.KEY_STORE_NAME));
            PrivateKey privateKey = keyCertManager.getPrivateKey((String) configurations.get(AS2Constants.PRIVATE_KEY));
            X509Certificate publicCert = keyCertManager.getCertificate((String)configurations.get(AS2Constants.PUBLIC_CERT));
            CertStore signCertStore = keyCertManager.getSignCertStore((String[]) configurations.get(AS2Constants.CERT_LIST));

            // Add new signer
            signedGenerator.addSigner(privateKey, publicCert, SMIMESignedGenerator.DIGEST_SHA1);
            signedGenerator.setContentTransferEncoding(AS2Constants.BASE64);

            // Adds certs to go with the signature
            signedGenerator.addCertificatesAndCRLs(signCertStore);

            // Generate the signed multipart
            MimeMultipart signedMimeMultipart = signedGenerator.generate(mimeMessage, AS2Constants.BC);

            // Calculates MIC
            String messageMIC = computeMIC(mimeMessage);

            // signedMimeMultipart contains both the content and signature. So previous mimeMessage is longer wanted.
            // Create a new mimeMessage and add the signedMultipart
            mimeMessage = new MimeMessage(session);
            mimeMessage.setContent(signedMimeMultipart);
            mimeMessage.saveChanges();


            // ENCRYPTING
            // Get the encrypt generator
            SMIMEEnvelopedGenerator encryptGenerator = new SMIMEEnvelopedGenerator();
            // Encoding of the encrypted content
            encryptGenerator.setContentTransferEncoding(AS2Constants.BINARY);
            // Add the public key for encryption
            encryptGenerator.addKeyTransRecipient(publicCert);

            // Encrypt the message
            MimeBodyPart encryptedPart =
                    encryptGenerator.generate(mimeMessage, SMIMEEnvelopedGenerator.DES_EDE3_CBC, AS2Constants.BC);

            // encryptedPart contains all including the headers. So previous mimeMessage is longer wanted.
            // Create a new mimeMessage and add the encryptedPart
            mimeMessage = new MimeMessage(session);
            mimeMessage.setContent(encryptedPart.getContent(), encryptedPart.getContentType());
            mimeMessage.setHeader(AS2Constants.CONTENT_TRANSFER_ENCODING, AS2Constants.BINARY);
            mimeMessage.saveChanges();

        } catch (MessagingException e) {
            log.error("Messaging Exception", e);
        } catch (Exception e) {
            log.error("Exception", e);
        }
    }

    /**
     * Calculates the MIC including MIME headers according to the AS2 Spec
     *
     * @param mimeMessage a MimeMessage
     * @return calculated MIC
     */
    private String computeMIC(MimeMessage mimeMessage){
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(AS2Constants.SHA_1, AS2Constants.BC);
            DigestOutputStream digestOutputStream = new DigestOutputStream(new ByteArrayOutputStream(), messageDigest);
            mimeMessage.writeTo(digestOutputStream);
            byte[] bytes = Base64.encode(messageDigest.digest());
            String mic = new String(bytes);
            mic = mic.concat(", sha1");
            return mic;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Convert whole mime message to string with/without outer HTTP headers
     *
     * @param mimeMessage a MimeMessage
     * @param withOuterHeaders with or with out HTTP headers in mimeMessage
     * @return string mime message
     * @throws MessagingException
     * @throws IOException
     */
    private String mimeMessageToString(MimeMessage mimeMessage, boolean withOuterHeaders)
            throws MessagingException, IOException{
        if(withOuterHeaders){
            OutputStream os = new ByteArrayOutputStream();
            mimeMessage.writeTo(os);
            return os.toString();
        }else {
            StringWriter writer = new StringWriter();
            IOUtils.copy(mimeMessage.getInputStream(), writer);
            return writer.toString();
        }
    }
}