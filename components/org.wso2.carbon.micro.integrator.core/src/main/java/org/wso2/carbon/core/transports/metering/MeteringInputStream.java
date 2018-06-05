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

import javax.servlet.ServletInputStream;
import java.io.IOException;

/**
 * MeteringInputStream class wraps InputStream and meter the number of 
 * byte transfered which can be retrieved using the getReadSize() 
 * method.
 */
public class MeteringInputStream extends ServletInputStream {
	public ServletInputStream wrappedInputStream;
	long readSize = 0;

	public MeteringInputStream(ServletInputStream wrappedInputStream) {
		this.wrappedInputStream = wrappedInputStream;
	}

	public int available() throws IOException {
		return wrappedInputStream.available();
	}

	public void close() throws IOException {
		wrappedInputStream.close();
	}

	public int readLine(byte[] b, int off, int len) throws IOException {
		int read = wrappedInputStream.readLine(b, off, len);
		readSize += read;
		return read;
	}

	public synchronized void mark(int readlimit) {
		wrappedInputStream.mark(readlimit);
	}

	public boolean markSupported() {
		return wrappedInputStream.markSupported();
	}

	public int read(byte[] b, int off, int len) throws IOException {
		int read = wrappedInputStream.read(b, off, len);
		readSize += read;
		return read;
	}

	public int read(byte[] b) throws IOException {
		int read = wrappedInputStream.read(b);
		readSize += read;
		return read;
	}

	public synchronized void reset() throws IOException {
		wrappedInputStream.reset();
	}

	public long skip(long n) throws IOException {
		return wrappedInputStream.skip(n);
	}

	public int read() throws IOException {
		readSize ++;
		return wrappedInputStream.read();
	}

	/**
	 * Get the read size
	 * @return the size of the read content
	 */
	public long getReadSize() {
		return readSize;
	}
}
