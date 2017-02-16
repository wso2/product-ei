/*
 * Copyright 2011-2012 WSO2, Inc. (http://wso2.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package demo.jaxrs.client;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.FileRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.resource.URIResolver;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public final class Client {

    private Client() {
    }

    public static void main(String args[]) throws Exception {
        // First set the URL of the service
        // Default is : http://localhost:9763/jax_rs_basic/services/customerservice
        String serviceURL = "http://localhost:9763/jaxrs_basic/services/customers/customerservice";
        if (args[0] != null) {
            serviceURL = args[0];
        }

        // Sent HTTP GET request to query customer info
        System.out.println("Sent HTTP GET request to query customer info");
        //URL url = new URL("http://localhost:9000/customerservice/customers/123");
        URL url = new URL(serviceURL + "/customers/123");
        URLConnection connection = url.openConnection();
        connection.setRequestProperty("accept", "text/xml");
        InputStream in = connection.getInputStream();
        System.out.println(getStringFromInputStream(in));

        // Sent HTTP GET request to query sub resource product info
        System.out.println("\n");
        System.out.println("Sent HTTP GET request to query sub resource product info");
        //url = new URL("http://localhost:9000/customerservice/orders/223/products/323");
        url = new URL(serviceURL + "/orders/223/products/323");
        connection = url.openConnection();
        connection.setRequestProperty("accept", "text/xml");
        in = connection.getInputStream();
        System.out.println(getStringFromInputStream(in));

        // Sent HTTP PUT request to update customer info
        System.out.println("\n");
        System.out.println("Sent HTTP PUT request to update customer info");
        Client client = new Client();
        String inputFile = client.getClass().getResource("update_customer.xml").getFile();
        URIResolver resolver = new URIResolver(inputFile);
        File input = new File(resolver.getURI());
        //PutMethod put = new PutMethod("http://localhost:9000/customerservice/customers");
        PutMethod put = new PutMethod(serviceURL + "/customers");
        RequestEntity entity = new FileRequestEntity(input, "text/xml; charset=ISO-8859-1");
        put.setRequestEntity(entity);
        HttpClient httpclient = new HttpClient();

        try {
            int result = httpclient.executeMethod(put);
            System.out.println("Response status code: " + result);

            String responseBody = put.getResponseBodyAsString();
            if (responseBody != null) {
                System.out.println("Response body: \n" + responseBody);
            }
        } finally {
            // Release current connection to the connection pool once you are
            // done
            put.releaseConnection();
        }

        // Sent HTTP POST request to add customer
        System.out.println("\n");
        System.out.println("Sent HTTP POST request to add customer");
        inputFile = client.getClass().getResource("add_customer.xml").getFile();
        resolver = new URIResolver(inputFile);
        input = new File(resolver.getURI());
        //PostMethod post = new PostMethod("http://localhost:9000/customerservice/customers");
        PostMethod post = new PostMethod(serviceURL + "/customers");
        post.addRequestHeader("Accept", "text/xml");
        entity = new FileRequestEntity(input, "text/xml; charset=ISO-8859-1");
        post.setRequestEntity(entity);
        httpclient = new HttpClient();

        try {
            int result = httpclient.executeMethod(post);
            System.out.println("Response status code: " + result);
            System.out.println("Response body: ");
            System.out.println(post.getResponseBodyAsString());
        } finally {
            // Release current connection to the connection pool once you are
            // done
            post.releaseConnection();
        }

        // Testing the new method which Consumes and Produces text/plain
        System.out.println("\n");
        System.out.println("Sent HTTP POST request to get customer name");
        PostMethod post2 = new PostMethod(serviceURL + "/customers/name");
        post2.addRequestHeader("Accept", "text/plain");
        RequestEntity myEntity = new StringRequestEntity("123456", "text/plain", "ISO-8859-1");
        post2.setRequestEntity(myEntity);
        httpclient = new HttpClient();

        try {
            int result = httpclient.executeMethod(post2);
            System.out.println("Response status code: " + result);
            System.out.println("Response body: ");
            System.out.println(post2.getResponseBodyAsString());
        } finally {
            // Release current connection to the connection pool once you are
            // done
            post2.releaseConnection();
        }

        System.out.println("\n");
        System.exit(0);
    }

    private static String getStringFromInputStream(InputStream in) throws Exception {
        CachedOutputStream bos = new CachedOutputStream();
        IOUtils.copy(in, bos);
        in.close();
        bos.close();
        return bos.getOut().toString();
    }

}
