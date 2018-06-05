/*
 * Copyright 2004,2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.carbon.core.transports.metering;

import javax.servlet.ServletOutputStream;
import java.io.IOException;

/**
 * MeteringOutputStream class wraps OutputStream and meter the number of 
 * byte transfered which can be retrieved using the geWrittenSize() 
 * method.
 */
public class MeteringOutputStream extends ServletOutputStream {
	ServletOutputStream wrappedOutputStream;
	long writtenSize = 0;

	public MeteringOutputStream(ServletOutputStream wrappedOutputStream) {
		this.wrappedOutputStream = wrappedOutputStream;
	}

	// we are not tracking the output from print methods, as they are not called
	// from xml writers
	public void print(boolean arg0) throws IOException {
		wrappedOutputStream.print(arg0);
	}

	public void print(char c) throws IOException {
		wrappedOutputStream.print(c);
	}

	public void print(double d) throws IOException {
		wrappedOutputStream.print(d);
	}

	public void print(float f) throws IOException {
		wrappedOutputStream.print(f);
	}

	public void print(int i) throws IOException {
		wrappedOutputStream.print(i);
	}

	public void print(long l) throws IOException {
		wrappedOutputStream.print(l);
	}

	public void print(String arg0) throws IOException {
		wrappedOutputStream.print(arg0);
	}

	public void println() throws IOException {
		wrappedOutputStream.println();
	}

	public void println(boolean b) throws IOException {
		wrappedOutputStream.println(b);
	}

	public void println(char c) throws IOException {
		wrappedOutputStream.println(c);
	}

	public void println(double d) throws IOException {
		wrappedOutputStream.println(d);
	}

	public void println(float f) throws IOException {
		wrappedOutputStream.println(f);
	}

	public void println(int i) throws IOException {
		wrappedOutputStream.println(i);
	}

	public void println(long l) throws IOException {
		wrappedOutputStream.println(l);
	}

	public void println(String s) throws IOException {
		wrappedOutputStream.println(s);
	}

	public void close() throws IOException {
		wrappedOutputStream.close();
	}

	public void flush() throws IOException {
		wrappedOutputStream.flush();
	}

	public void write(byte[] b, int off, int len) throws IOException {
		wrappedOutputStream.write(b, off, len);
		writtenSize += len - off;
	}

	public void write(byte[] b) throws IOException {
		wrappedOutputStream.write(b);
		writtenSize += b.length;
	}

	public void write(int b) throws IOException {
		wrappedOutputStream.write(b);
		writtenSize ++;
	}

	/**
	 * Get the written size
	 * @return the size of the written content
	 */
	public long geWrittenSize() {
		return writtenSize;
	}
}
