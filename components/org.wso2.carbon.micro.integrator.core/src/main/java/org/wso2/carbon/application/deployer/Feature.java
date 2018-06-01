/*
*  Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package org.wso2.carbon.application.deployer;


import org.eclipse.equinox.p2.metadata.Version;
import org.eclipse.equinox.p2.metadata.VersionRange;

public class Feature {
    String id;
    String version;
    VersionRange versionRange;

    public static final String ID = "id";
    public static final String VERSION = "version";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public VersionRange getVersionRange() {
        return versionRange;
    }

    /**
     * Converts a string version range into an OSGi VersionRange object.
     * Ex : [3.1.0, 4.0.0)
     *
     * @param versionRange - string version range
     */
    public void setVersionRange(String versionRange) {
        boolean startIncluded = true;
        boolean endIncluded = true;

        versionRange = versionRange.trim();
        if (versionRange.startsWith("(")) {
            startIncluded = false;
        }
        // remove the first charactor
        versionRange = versionRange.substring(1);

        if (versionRange.endsWith(")")) {
            endIncluded = false;
        }
        // remove the last charactor
        versionRange = versionRange.substring(0, versionRange.length() - 1);

        String startVersion = versionRange.substring(0, versionRange.indexOf(','));
        // end version starts after ", " therefore, versionRange.indexOf(',') + 2
        String endVersion = versionRange.substring(versionRange.indexOf(',') + 2,
                versionRange.length());
        this.versionRange = new VersionRange(Version.create(startVersion), startIncluded,
                Version.create(endVersion), endIncluded);
    }
}
