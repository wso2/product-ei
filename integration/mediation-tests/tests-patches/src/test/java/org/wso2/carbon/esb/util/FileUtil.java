/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.esb.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FileUtil {
	/**
	 * Checks whether the given text appears a given number of occurrences in
	 * the
	 * specified file.
	 *
	 * @param filePath
	 *            source File to be used for the search operation.
	 * @param expectedMsg
	 *            expected message
	 * @param expectedTimes
	 *            expected number of occurrences that the message should appear
	 *            in the
	 *            file
	 * @return <code>true</code> if the expected message appears a given number
	 *         of occurrences in the specified file, <code>false</code>
	 *         otherwise.
	 * @throws IOException
	 *             If any ERROR occurred while manipulating the files.
	 */
	public static boolean containsInFile(final String filePath, final String expectedMsg,
	                                     final int expectedTimes) throws IOException {
		int counter = 0;
		final BufferedReader br = new BufferedReader(new FileReader(new File(filePath)));
		try {

			String currentLine;
			while ((currentLine = br.readLine()) != null) {
				if (currentLine.contains(expectedMsg)) {
					counter++;

				}
			}
		} finally {
			br.close();
		}
		if (counter == expectedTimes) {
			return true;
		}
		return false;
	}

	public static boolean containsInFile(final String filePath, final String expectedMsg)
	                                                                                     throws IOException {
		final BufferedReader br = new BufferedReader(new FileReader(new File(filePath)));
		try {

			String currentLine;
			while ((currentLine = br.readLine()) != null) {
				if (currentLine.contains(expectedMsg)) {
					return true;

				}
			}
		} finally {
			br.close();
		}
		return false;
	}

}
