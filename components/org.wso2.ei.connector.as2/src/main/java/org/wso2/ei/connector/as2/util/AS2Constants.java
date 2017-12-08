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

package org.wso2.ei.connector.as2.util;

/**
 * Holds all he constant values
 */
public class AS2Constants {

    private AS2Constants() {
    }

    // HEADERS
    // user-agent header in as2 message
    public static final String USER_AGENT = "User-Agent";
    public static final String USER_AGENT_NAME = "WSO2-EI";

    //  as2-version header
    public static final String AS2_VERSION = "AS2-Version";
    public static final String AS2_VERSION_NUMBER = "1.1";

    // mime-version header
    public static final String MIME_VERSION = "Mime-Version";
    public static final String MIME_VERSION_NUMBER = "1.0";

    // as2-to header
    public static final String AS2_TO = "AS2-To";

    // as2-from header
    public static final String AS2_FROM = "AS2-From";

    // subject header
    public static final String SUBJECT = "Subject";

    // message-id header
    public static final String MESSAGE_ID = "Message-ID";

    // date header
    public static final String DATE = "Date";

    // content related headers
    public static final String CONTENT_TRANSFER_ENCODING = "Content-Transfer-Encoding";
    public static final String CONTENT_DISPOSITION = "Content-Disposition";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String APPLICATION_PKCS7_MIME
            = "application/pkcs7-mime; smime-type=enveloped-data; name=smime.p7m";
    public static final String APPLICATION_EDI_CONSENT = "application/EDI-Consent";

    // mdn-to header
    public static final String MDN_TO = "Disposition-Notification-To";

    // from header
    public static final String FROM = "From";
    public static final String FROM_MDN_EMAIL = "wso2ei@wso2.com";

    // content-transfer-encoding types
    public static final String BINARY = "binary";
    public static final String BASE64 = "base64";

    // message disposition
    public static final String DISPOSITION_NOTIFICATION_OPTIONS = "Disposition-Notification-Options";
    public static final String DISPOSITION_NOTIFICATION_OPTIONS_VALUE
            = "signed-receipt-protocol=optional, pkcs7-signature; signed-receipt-micalg=optional, sha1";

    // OTHERS/PARAMETERS
    // key store & public, private key string
    public static final String KEY_STORE_NAME = "key-store-name";
    public static final String PUBLIC_CERT = "public-cert";
    public static final String PRIVATE_KEY = "private-key";
    public static final String CERT_LIST = "cert-list";

    public static final String AS2_TO_PARAM = "as2-to";
    public static final String AS2_FROM_PARAM = "as2-from";
    public static final String SUBJECT_PARAM = "subject";

    // bouncycastle provider
    public static final String BC = "BC";

    // Algorithm names
    public static final String SHA_1 = "SHA-1";
}