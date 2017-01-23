package org.wso2.carbon.esb.mediator.test.fault;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class ProtocolViolationServer implements Runnable {

    private int port = 8989;
    private static boolean stopFlag = false;
    
    public ProtocolViolationServer(int p) {
        this.port = p;
    }

    public ProtocolViolationServer() {

    }

    public void stopServer() {
        stopFlag = true;
    }

    public void runServer() {
        System.out.println("Starting simple server......");
        try {
            new Thread(new ProtocolViolationServer(this.port)).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class RequestHandler implements Runnable {
        private Socket theRequest;

        public RequestHandler(Socket theRequest) {
            this.theRequest = theRequest;
        }

        @Override
        public void run() {
            try {
                BufferedReader br =  new BufferedReader(new InputStreamReader(
                                               this.theRequest.getInputStream()));

                int num = this.theRequest.getInputStream().available();
                System.out.println("length is " + num);

                char[] buf = new char[num];
                br.read(buf);
                System.out.println(new String(buf));

                System.out.println("complete reading and now writing back......");
                this.theRequest.getOutputStream()
                               .write(new String("response from simple server").getBytes());

                this.theRequest.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

        }
    }

    @Override
    public void run() {
        try{
            ServerSocket ss = new ServerSocket(this.port);
            while (true) {
                Socket incoming = ss.accept();
                System.out.println("Processing request......");
                new Thread(new RequestHandler(incoming)).start();
                if (stopFlag) {
                    ss.close();
                }
            }            
        }catch (Exception ex) {
            
        }
        
    }

}
