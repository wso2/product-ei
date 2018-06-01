/*
 * Copyright 2005-2007 WSO2, Inc. (http://wso2.com)
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

package org.wso2.carbon.core.services.echo;

import java.util.Arrays;

/**
 * Simple bean used in the echo service
 */
public class SimpleBean {
    private String[] a_r;
    private String[] b_r;
    private int c;

    public String[] getA_r() {
        return Arrays.copyOf(a_r, a_r.length);
    }

    public void setA_r(String[] a_r) {
        this.a_r = Arrays.copyOf(a_r, a_r.length);
    }

    public String[] getB_r() {
        return Arrays.copyOf(b_r, b_r.length);
    }

    public void setB_r(String[] b_r) {
        this.b_r = Arrays.copyOf(b_r, b_r.length);
    }

    public int getC() {
        return c;
    }

    public void setC(int c) {
        this.c = c;
    }
}
