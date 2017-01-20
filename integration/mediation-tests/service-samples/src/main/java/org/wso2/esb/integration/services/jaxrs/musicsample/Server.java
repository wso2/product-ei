/*
 * Copyright 2014 WSO2, Inc. (http://wso2.com)
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

package org.wso2.esb.integration.services.jaxrs.musicsample;

import org.apache.catalina.Context;
import org.apache.catalina.loader.WebappLoader;
import org.apache.catalina.startup.Tomcat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import java.io.File;
import java.io.IOException;

public class Server {

    private final static Log log = LogFactory.getLog(Server.class);

    public static void main(final String[] args) throws Exception {
        final File base = createBaseDirectory();
        log.info("Using base folder: " + base.getAbsolutePath());

        final Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);
        tomcat.setBaseDir( base.getAbsolutePath() );

        Context context = tomcat.addContext( "/", base.getAbsolutePath() );
        Tomcat.addServlet( context, "CXFServlet", new CXFServlet() );

        context.addServletMapping( "/rest/*", "CXFServlet" );
        context.addApplicationListener( ContextLoaderListener.class.getName() );
        context.setLoader( new WebappLoader( Thread.currentThread().getContextClassLoader() ) );

        context.addParameter( "contextClass", AnnotationConfigWebApplicationContext.class.getName() );
        context.addParameter( "contextConfigLocation", MusicConfig.class.getName() );

        tomcat.start();
        tomcat.getServer().await();
    }

    private static File createBaseDirectory() throws IOException {
        final File base = File.createTempFile( "tmp-", "", new File("/home/dimuthu/Desktop/JMS"));

        if( !base.delete() ) {
            throw new IOException( "Cannot (re)create base folder: " + base.getAbsolutePath()  );
        }

        if( !base.mkdir() ) {
            throw new IOException( "Cannot create base folder: " + base.getAbsolutePath()  );
        }
        return base;
    }
}
