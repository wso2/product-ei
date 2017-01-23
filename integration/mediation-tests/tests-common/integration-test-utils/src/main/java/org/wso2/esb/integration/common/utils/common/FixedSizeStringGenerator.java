/*
 * Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.esb.integration.common.utils.common;

/**
 * 
 * This class can be used to create strings of predefined size of MB or KB
 * These strings can be used to create SOAP messages of approximate sizes
 */

public class FixedSizeStringGenerator {

	/**
	 * 
	 * @param size size in KB
	 * @return
	 */
	public static String generateMessageKB(double size) {
		StringBuilder sb = new StringBuilder();
		String waitMessage = "Testing message";
		int length = waitMessage.length();
		double bytes = size * 1024;
		sb.append(" WSO2 ");
		for (int i = 0; i < bytes; i += length) {
			sb.append(waitMessage );
		}
		return sb.toString();
	}

	/**
	 * 
	 * @param size size in MB
	 * @return
	 */
	public static String generateMessageMB(double size) {
		return generateMessageKB(size * 1024);
	}
	
	/**
	 * 
	 * @param size size in KB
	 * @param repeatingMessage this will be repeated in the output String
	 * @return
	 */
	public static String generateCustomMessageKB(double size,String repeatingMessage) {
		StringBuilder sb = new StringBuilder();
		String waitMessage = repeatingMessage;
		int length = waitMessage.length();
		double bytes = size * 1024;
		sb.append(" WSO2 ");
		for (int i = 0; i < bytes; i += length) {
			sb.append(waitMessage );
		}
		return sb.toString();
	}
	
	/**
	 * 
	 * @param size size in MB
	 * @param repeatingMessage this will be repeated in the output String
	 * @return
	 */
	public static String generateCustomMessageMB(double size,String repeatingMessage) {
		return generateCustomMessageKB(size * 1024,repeatingMessage);
	}

}
