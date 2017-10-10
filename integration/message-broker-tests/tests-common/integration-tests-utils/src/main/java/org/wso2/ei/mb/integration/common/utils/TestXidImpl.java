/*
 * Copyright (c) 2017, WSO2 Inc. (http://wso2.com) All Rights Reserved.
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

package org.wso2.ei.mb.integration.common.utils;

import javax.transaction.xa.Xid;

public class TestXidImpl implements Xid {
    private int formatId;
    private byte gtrid[];
    private byte bqual[];

    public TestXidImpl(int formatId, byte gtrid[], byte bqual[]) {
        this.formatId = formatId;
        this.gtrid = gtrid;
        this.bqual = bqual;
    }

    public int getFormatId() {
        return formatId;
    }

    public byte[] getBranchQualifier() {
        return bqual;
    }

    public byte[] getGlobalTransactionId() {
        return gtrid;
    }
}