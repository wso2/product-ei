package org.wso2.carbon.esb.tcp.transport.test.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Java client for TCP transport.
 */
public class NativeTCPClient {

    private String host = "localhost";
    private int port = 6789;
    private Socket socket = null;
    private int messageCount = 0;
    private String delimiterType;
    private String stringDelimiter;
    private Character byteDelimiter;
    private String characterDelimiter;
    private String message;

    public NativeTCPClient(String delimiterType, int messageCount) throws Exception {
        this.socket = new Socket(this.host, this.port);
        this.messageCount = messageCount;
        this.delimiterType = delimiterType;
    }

    public void sendToServer() throws Exception {
        PrintWriter outToServer = null;
        outToServer = new PrintWriter(new OutputStreamWriter(this.socket.getOutputStream()));
        String fullMessage = "";
        for (int i = 0; i < this.messageCount; ++i) {
            fullMessage += this.message;
            if (NativeTCPClient.DelimiterTypeEnum.BYTE.getDelimiterType().equalsIgnoreCase(this.delimiterType)) {
                fullMessage += this.byteDelimiter;
            } else if (NativeTCPClient.DelimiterTypeEnum.CHARACTER.getDelimiterType()
                    .equalsIgnoreCase(this.delimiterType)) {
                fullMessage += this.characterDelimiter;
            } else {
                fullMessage += this.stringDelimiter;
            }
        }
        outToServer.print(fullMessage);
        outToServer.flush();
    }

    public String[] receiveCharactorTypeDelimiterResonse() throws Exception {
        //Wait for some time to get server response
        Thread.sleep(3000);
        char delimiter = 0;
        ByteArrayOutputStream bos = null;
        if (NativeTCPClient.DelimiterTypeEnum.BYTE.getDelimiterType().equalsIgnoreCase(this.delimiterType)) {
            delimiter = this.byteDelimiter;
        } else if (NativeTCPClient.DelimiterTypeEnum.CHARACTER.getDelimiterType()
                .equalsIgnoreCase(this.delimiterType)) {
            delimiter = this.characterDelimiter.charAt(0);
        }

        if (delimiter == 0) {
            throw new Exception("Delimiter has not specified");
        } else {
            String[] responses;
            try {
                responses = new String[this.messageCount];
                InputStream stream = this.socket.getInputStream();
                int tempMessageCount = 0;
                int next = stream.read();
                bos = new ByteArrayOutputStream();
                while (next > -1) {
                    if (delimiter != next) {
                        bos.write(next);
                    }
                    next = stream.read();
                    if (delimiter == next) {
                        responses[tempMessageCount] = new String(bos.toByteArray());
                        ++tempMessageCount;
                        if (tempMessageCount == this.messageCount) {
                            break;
                        }
                        bos = new ByteArrayOutputStream();
                    }
                }
            } finally {
                try {
                    this.closeSocket();
                } catch (IOException var12) {
                    //ignore
                }
                if (bos != null) {
                    bos.close();
                }
            }
            return responses;
        }
    }

    public String[] receiveStringTypeDelimiterResonse() throws Exception {
        ByteArrayOutputStream bos = null;
        Thread.sleep(3000);
        try {
            InputStream stream = this.socket.getInputStream();
            int next = stream.read();
            bos = new ByteArrayOutputStream();
            while (true) {
                if (next > -1) {
                    bos.write(next);
                    if (stream.available() > 0) {
                        next = stream.read();
                        continue;
                    }
                }

                String[] responses = (new String(bos.toByteArray())).split(this.stringDelimiter);
                return responses;
            }
        } finally {
            try {
                this.closeSocket();
            } catch (IOException var10) {
                //ignore
            }
            if (bos != null) {
                bos.close();
            }
        }
    }

    void closeSocket() throws IOException {
        this.socket.close();
    }

    public String getDelimiterType() {
        return this.delimiterType;
    }

    public void setDelimiterType(String delimiterType) {
        this.delimiterType = delimiterType;
    }

    public String getStringDelimiter() {
        return this.stringDelimiter;
    }

    public void setStringDelimiter(String stringDelimiter) {
        this.stringDelimiter = stringDelimiter;
    }

    public String getCharacterDelimiter() {
        return this.characterDelimiter;
    }

    public void setCharacterDelimiter(String characterDelimiter) {
        this.characterDelimiter = characterDelimiter;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setByteDelimiter(Character byteDelimiter) {
        this.byteDelimiter = byteDelimiter;
    }

    public static enum DelimiterTypeEnum {
        STRING("STRING"), CHARACTER("CHARACTER"), BYTE("BYTE");

        private final String delimiterType;

        private DelimiterTypeEnum(String delimiterType) {
            this.delimiterType = delimiterType;
        }

        public String getDelimiterType() {
            return this.delimiterType;
        }
    }
}