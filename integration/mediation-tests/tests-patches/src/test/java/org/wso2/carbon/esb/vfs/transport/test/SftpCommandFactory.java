package org.wso2.carbon.esb.vfs.transport.test;

/**
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

import org.apache.sshd.common.SshException;
import org.apache.sshd.server.command.Command;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;
import org.apache.sshd.server.scp.ScpCommandFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The command factory for the SSH server.
 */
public class SftpCommandFactory extends ScpCommandFactory {

    public static final Pattern NETCAT_COMMAND = Pattern.compile("nc -q 0 localhost (\\d+)");

    /**
     * Creates a pipe thread that connects an input to an output
     *
     * @param name     The name of the thread (for debugging purposes)
     * @param in       The input stream
     * @param out      The output stream
     * @param callback An object whose method ExitCallback will be called when the pipe is
     *                 broken. The integer argument is 0 if everything went well.
     */
    private static void connect(final String name, final InputStream in, final OutputStream out, final ExitCallback
            callback) {
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                int code = 0;
                try {
                    final byte buffer[] = new byte[1024];
                    int len;
                    while ((len = in.read(buffer, 0, buffer.length)) != -1) {
                        out.write(buffer, 0, len);
                        out.flush();
                    }
                } catch (final SshException ex) {
                    // Nothing to do, this occurs when the connection
                    // is closed on the remote side
                } catch (final IOException ex) {
                    if (!ex.getMessage().equals("Pipe closed")) {
                        code = -1;
                    }
                }
                if (callback != null) {
                    callback.onExit(code);
                }
            }
        }, name);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * CreateCommand method Handles these commands.
     * id -u</code>
     * id -G</code>
     * nc -q 0 localhost port
     *
     * @param command The command which needs to be executed
     */
    @Override
    public Command createCommand(final String command) {
        return new Command() {
            public ExitCallback callback = null;
            public OutputStream out = null;
            public OutputStream err = null;
            public InputStream in = null;

            @Override
            public void setInputStream(final InputStream in) {
                this.in = in;
            }

            @Override
            public void setOutputStream(final OutputStream out) {
                this.out = out;
            }

            @Override
            public void setErrorStream(final OutputStream err) {
                this.err = err;
            }

            @Override
            public void setExitCallback(final ExitCallback callback) {
                this.callback = callback;

            }

            @Override
            public void start(final Environment env) throws IOException {
                int code = 0;
                if (command.equals("id -G") || command.equals("id -u")) {
                    new PrintStream(out).println(1001);
                } else if (NETCAT_COMMAND.matcher(command).matches()) {
                    final Matcher matcher = NETCAT_COMMAND.matcher(command);
                    matcher.matches();
                    final int port = Integer.parseInt(matcher.group(1));

                    final Socket socket = new Socket((String) null, port);

                    if (out != null) {
                        connect("from nc", socket.getInputStream(), out, null);
                    }

                    if (in != null) {
                        connect("to nc", in, socket.getOutputStream(), callback);
                    }

                    return;

                } else {
                    if (err != null) {
                        new PrintStream(err).format("Unknown command %s%n", command);
                    }
                    code = -1;
                }

                if (out != null) {
                    out.flush();
                }
                if (err != null) {
                    err.flush();
                }
                callback.onExit(code);
            }

            @Override
            public void destroy() {
            }
        };
    }
}
