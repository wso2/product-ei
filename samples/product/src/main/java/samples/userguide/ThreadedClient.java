/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *   * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package samples.userguide;

public class ThreadedClient {

    public static final int STATELESS = 0;
    public static final int SOAP_SESSION = 1;
    public static final int SIMPLE_CLIENT_SESSION = 2;
    public static final int TRANSPORT_SESSION = 3;

    private int session = STATELESS;

    /**
     * @param args  1: epr
     *              2: operation
     *              3: number of requests
     *              4: load
     *              5: number of clients
     *              6: session (client | transport)
     */
    public static void main(String[] args) {
        new ThreadedClient().work();
    }

    public void work() {

        String epr1 = System.getProperty("epr");
        if (epr1 == null) {
            epr1 = "http://localhost:8280";
        }
        System.out.println("EPR: " + epr1);

        String op = System.getProperty("op");
        if (op == null) {
            op = "sleepOperation";
        }
        System.out.println("Operation: " + op);

        long requests = 10;
        String requestsProp = System.getProperty("req");
        if (requestsProp != null) {
            requests = Long.parseLong(requestsProp);
        }
        System.out.println("Number of requests: " + requests);

        String loadParameter = System.getProperty("load");
        if (loadParameter == null) {
            loadParameter = "1000";
        }
        System.out.println("Load: " + loadParameter);

        long msgSize = 0;
        String msgSizeProp = System.getProperty("msg");
        if (msgSizeProp != null) {
            msgSize = Long.parseLong(msgSizeProp);
        }
        System.out.println("Number of dummy elements in the message: " + msgSize);

        String sessionProp = System.getProperty("session");
        if (sessionProp == null) {
            session = STATELESS;
        } else {
            if (sessionProp.equalsIgnoreCase("client")) {
                session = SIMPLE_CLIENT_SESSION;
            } else if (sessionProp.equalsIgnoreCase("transport")) {
                session = TRANSPORT_SESSION;
            }
        }

        int clients = 2;
        String clientsProp = System.getProperty("t");
        if (clientsProp != null) {
            clients = Integer.parseInt(clientsProp);
        }
        System.out.println("Number of client threads: " + clients);

        ServiceInvoker[] invokers = new ServiceInvoker[clients];

        for (int i = 0; i< clients; i++) {

            ServiceInvoker invoker = new ServiceInvoker(epr1, op);

            if (session != STATELESS) {
                invoker.setStatefull(true);
            }

            invoker.setInvokerName("CLIENT " + i);
            invoker.setIterations(requests);
            invoker.setLoad(loadParameter);
            invoker.addDummyElements(msgSize);

            if (session == SIMPLE_CLIENT_SESSION) {
                invoker.setClientSessionID("CLIENT " + i);
            }

            invokers[i] = invoker;
        }

        long t1 = System.currentTimeMillis();

        for (int i = 0; i < clients; i++) {
            invokers[i].start();
        }

        try {
            for (int i = 0; i < clients; i++) {
                invokers[i].join();
            }
        } catch (InterruptedException e) {
            System.out.println("ERROR: A client thread is interrupted while sending requests.");
        }

        long t2 = System.currentTimeMillis();

        System.out.println("\n================================================================\n");

        for (int i = 0; i < clients; i++) {
            System.out.println(invokers[i].getInvokerName() +
                    " completed requests in " + invokers[i].getRunningTime() + " milliseconds.");
        }

        System.out.println
                ("Time taken for completing all the requests is " + (t2 - t1) + " milliseconds.");

    }
}
