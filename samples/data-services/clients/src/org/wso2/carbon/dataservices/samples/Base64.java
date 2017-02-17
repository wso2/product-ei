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
package org.wso2.carbon.dataservices.samples;

import java.io.FileInputStream;
/**
 * This class provides base64 encoding/decoding functionality. 
 */
public class Base64 {

	private static byte[] lookup = {
	      65, 66, 67, 68, 69, 70, 71, 72, 73,
	      74, 75, 76, 77, 78, 79, 80, 81, 82,
	      83, 84, 85, 86, 87, 88, 89, 90,
	      97, 98, 99, 100, 101, 102, 103, 104, 105,
	      106, 107, 108, 109, 110, 111, 112, 113, 114,
	      115, 116, 117, 118, 119, 120, 121, 122,
	      48, 49, 50, 51, 52, 53, 54, 55, 56,
	      57, 43, 47, 61 };	
	
	private static byte[] rev_lookup = {
      	  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
    	  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
    	  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
    	  0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
    	  0, 0, 0, 62, 0, 0, 0, 63, 52, 53, 
    	  54, 55, 56, 57, 58, 59, 60, 61, 0, 0, 
    	  0, 64, 0, 0, 0, 0, 1, 2, 3, 4, 
    	  5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 
    	  15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 
    	  25, 0, 0, 0, 0, 0, 0, 26, 27, 28, 
    	  29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 
    	  39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 
    	  49, 50, 51 };
	
	public static byte[] encode(byte[] data) { 
		int ylen = data.length%3;
	    int xlen = data.length - ylen;	      
	    byte[] ret = new byte[(xlen/3)*4 + (ylen>0?4:0)];	      
	    int i = 0, j = 0;
	    
		for (;i<xlen;i+=3,j+=4) {
			ret[j] = lookup[((data[i] & 0xff) >>> 2)]; 
			ret[j+1] = lookup[(((data[i] & 3)<<4) | 
					          ((data[i+1] & 0xff)>>>4))];				
			ret[j+2] = lookup[(((data[i+1] & 0xf)<<2) | 
					           ((data[i+2] & 0xff)>>>6))];
			ret[j+3] = lookup[((data[i+2] & 0x3f))];
		}
			
		if (ylen == 1) {
			ret[j] = lookup[((data[i] & 0xff) >>> 2)];				
			ret[j+1] = lookup[(((data[i] & 3)<<4))];
			ret[j+2] = lookup[64]; // padding
			ret[j+3] = lookup[64]; // padding
		}
		else if (ylen == 2) {
			ret[j] = lookup[((data[i] & 0xff) >>> 2)]; 
			ret[j+1] = lookup[(((data[i] & 3)<<4) | ((data[i+1] & 0xff)>>>4))];
			ret[j+2] = lookup[(((data[i+1] & 0xf)<<2))];
			ret[j+3] = lookup[64]; // padding
		}
					
		return ret;
	}

	public static byte[] decode(byte[] data) {		
		int len = (data.length/4)*3;
		int xlen = 0;
		
		if (data[data.length-2] == '=') {
			len -= 2;
			xlen = 1;
		}
		else if (data[data.length-1] == '=') {
			len -= 1;
			xlen = 2;
		}	
		
		byte[] ret = new byte[len];  
		int i = 0, j = 0;		
		len -= len%3;
		
		for(;j<len;i+=4,j+=3) {
			ret[j] = (byte)(((rev_lookup[data[i] & 0xff])<<2) | 
					        ((rev_lookup[data[i+1] & 0xff]))>>>4);
			ret[j+1] = (byte)(((rev_lookup[data[i+1] & 0xff])<<4) | 
			                  ((rev_lookup[data[i+2] & 0xff]))>>>2);
			ret[j+2] = (byte)(((rev_lookup[data[i+2] & 0xff])<<6) | 
	                          ((rev_lookup[data[i+3] & 0xff])));
		}

		if (xlen > 0) {
			ret[j] = (byte)(((rev_lookup[data[i] & 0xff])<<2) | 
			        ((rev_lookup[data[i+1] & 0xff]))>>>4);
		}
		if (xlen > 1) {
			ret[j+1] = (byte)(((rev_lookup[data[i+1] & 0xff])<<4) | 
	                  ((rev_lookup[data[i+2] & 0xff]))>>>2);
		}
		
		return ret;
	}
	
	public static void main(String[] args) throws Exception {		
		String fn = "/home/laf/c.avi";
		byte[] buff = new byte[1024 * 50];
		int i;
		System.out.println("START");
		FileInputStream in = new FileInputStream(fn);
		long count = 0, c2 = 0, c3 = 0;
		while ((i = in.read(buff)) > 0) {
			String s1 = new String(Base64.encode(buff));
			System.out.println("i:" + i + ":" + s1.length() + ":" + s1.getBytes().length);
			byte[] b2 = Base64.decode(s1.getBytes());
			System.out.println("X:" + i + " Y:" + b2.length);
		}
		System.out.println("END:" + count + " : " + c2 + " : " + c3);
	}
	
	
}