/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jboss.fuse.security.role.cxf;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.Scanner;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public final class Client {

    private static final String CLIENT_CONFIG_FILE = "ClientConfig.xml";
    private static final String BASE_SERVICE_URL = "http://localhost:9000/customerservice/customers";

    private static final Logger log = LoggerFactory.getLogger(Client.class);

    private static GetMethod get;

    public static void main(String args[]) throws Exception {

        // String keyStoreLoc = "src/main/config/clientKeystore.jks";

        // KeyStore keyStore = KeyStore.getInstance("JKS");
        // keyStore.load(new FileInputStream(keyStoreLoc), "cspass".toCharArray());

        /* 
         * Send HTTP GET request to query customer info using portable HttpClient
         * object from Apache HttpComponents
         */

        try {
            // Define the Get Method with the String of the url to access the HTTP Resourc
            get = new GetMethod(BASE_SERVICE_URL + "/123");
            get.setRequestHeader("Accept", "text/xml");
            HttpClient httpclient = new HttpClient();
            int status = httpclient.executeMethod(get);

            InputStream is = get.getResponseBodyAsStream();
            String response = inputStreamToString(is);
            log.info("Response : " + response);
            log.info("Status : " + status);

/*
        *//*
         *  Send HTTP PUT request to update customer info, using CXF WebClient method
         *  Note: if need to use basic authentication, use the WebClient.create(baseAddress,
         *  username,password,configFile) variant, where configFile can be null if you're
         *  not using certificates.
         *//*
        System.out.println("\n\nSending HTTPS PUT to update customer name");
        WebClient wc = WebClient.create(BASE_SERVICE_URL, CLIENT_CONFIG_FILE);
        Customer customer = new Customer();
        customer.setId(123);
        customer.setName("Mary");
        Response resp = wc.put(customer);

        *//*
         *  Send HTTP POST request to add customer, using JAXRSClientProxy
         *  Note: if need to use basic authentication, use the JAXRSClientFactory.create(baseAddress,
         *  username,password,configFile) variant, where configFile can be null if you're
         *  not using certificates.
         *//*
        System.out.println("\n\nSending HTTPS POST request to add customer");
        CustomerService proxy = JAXRSClientFactory.create(BASE_SERVICE_URL, CustomerService.class,
              CLIENT_CONFIG_FILE);
        customer = new Customer();
        customer.setName("Jack");
        resp = wc.post(customer);
        */
            System.out.println("\n");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Release current connection to the connection pool once you are done
            get.releaseConnection();
            System.exit(0);
        }
    }

    protected static String inputStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
