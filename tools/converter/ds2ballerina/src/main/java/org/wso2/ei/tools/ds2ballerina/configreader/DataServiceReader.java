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

package org.wso2.ei.tools.ds2ballerina.configreader;

import org.ballerinalang.model.BLangPackage;
import org.ballerinalang.model.BLangProgram;
import org.ballerinalang.model.BallerinaFile;
import org.ballerinalang.model.GlobalScope;
import org.ballerinalang.model.NativeScope;
import org.ballerinalang.model.builder.BLangModelBuilder;
import org.ballerinalang.util.program.BLangPrograms;
import org.ballerinalang.util.repository.PackageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.ei.tools.converter.common.generator.BallerinaSourceGenerator;
import org.wso2.ei.tools.ds2ballerina.beans.DataService;
import org.wso2.ei.tools.ds2ballerina.util.Util;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 * {@code DataServiceReader} class reads data service configuration file.
 */
public class DataServiceReader {

    private static Logger logger = LoggerFactory.getLogger(DataServiceReader.class);

    public static DataService readDataServiceFile(File dsFile) throws IOException {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(DataService.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            return (DataService) jaxbUnmarshaller.unmarshal(dsFile);
        } catch (JAXBException e) {
            throw new IOException("Error occurred while mapping the DataService", e);
        }
    }

    public static void createBalModel(DataService dataservice, String outputFile) throws IOException {
        PackageRepository packageRepository = new PackageRepository() {
            @Override public PackageSource loadPackage(Path path) {
                return null;
            }

            @Override public PackageSource loadFile(Path path) {
                return null;
            }
        };

        GlobalScope globalScope = BLangPrograms.populateGlobalScope();
        NativeScope nativeScope = BLangPrograms.populateNativeScope();

        BLangProgram programScope = new BLangProgram(globalScope, nativeScope, BLangProgram.Category.SERVICE_PROGRAM);

        BLangPackage bLangPackage = new BLangPackage(".", packageRepository, programScope);
        BLangPackage.PackageBuilder packageBuilder = new BLangPackage.PackageBuilder(bLangPackage);

        BLangModelBuilder modelBuilder = new BLangModelBuilder(packageBuilder, ".");

        String pkgPath = "ballerina.lang.messages";
        //String asPkgName = null;
        modelBuilder.addImportPackage(null, null, pkgPath, null);

        String pkgPath1 = "ballerina.net.http";
        //String asPkgName1 = null;
        modelBuilder.addImportPackage(null, null, pkgPath1, null);

        String pkgPath2 = "ballerina.data.sql";
        //String asPkgName = null;
        modelBuilder.addImportPackage(null, null, pkgPath2, null);

        String pkgPath3 = "ballerina.lang.datatables";
        //String asPkgName = null;
        modelBuilder.addImportPackage(null, null, pkgPath3, null);

        String pkgPath4 = "ballerina.lang.strings";
        modelBuilder.addImportPackage(null, null, pkgPath4, null);

      //  modelBuilder.startAnnotationAttachment(null);

        BLangModelBuilder.NameReference nameReference;

        String pkgName = "http";
        String name = "BasePath";
        nameReference = new BLangModelBuilder.NameReference(pkgName, name);
        modelBuilder.validateAndSetPackagePath(null, nameReference);

        String stringLiteral = "/" + dataservice.getName();
        modelBuilder.createStringLiteral(null, null, stringLiteral);

        modelBuilder.createLiteralTypeAttributeValue(null, null);

        String key = "value";
      //  modelBuilder.createAnnotationKeyValue(null, key);

        int attribuesAvailable = 1;
        modelBuilder.addAnnotationAttachment(null, null, nameReference, attribuesAvailable);

      //  modelBuilder.startServiceDef(null);

        Util.createConfigProperties(modelBuilder, dataservice);
        // done

        Util.createResources(modelBuilder, dataservice);

     //   modelBuilder.createService(null, dataservice.getName());
        BallerinaFile bFile = modelBuilder.build();
        BallerinaSourceGenerator sourceGenerator = new BallerinaSourceGenerator();
//        sourceGenerator.generate(bFile, "/home/madhawa/DSS-BAM/madhawa-dss1.bal");
        sourceGenerator.generate(bFile, outputFile);
        System.out.print("Done building AST!");
    }

}
