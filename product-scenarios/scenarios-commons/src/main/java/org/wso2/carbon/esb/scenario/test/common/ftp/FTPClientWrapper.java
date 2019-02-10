/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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

package org.wso2.carbon.esb.scenario.test.common.ftp;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * FTP client for ftp operations.
 */
public class FTPClientWrapper {
    private String hostname;
    private String userName;

    private String password;
    private int port;
    private FTPClient ftpClient;

    private static final Log log = LogFactory.getLog(FTPClientWrapper.class);

    /**
     * Constructor to FTPClient.
     *
     * @param hostname host name or IP address of the FTP server
     * @param username username of an FTP account on the FTP server to connect to
     * @param password password corresponds to the username
     * @param port ftp port
     */
    public FTPClientWrapper(String hostname, String username, String password, int port) {
        this.hostname = hostname;
        this.userName = username;
        this.password = password;
        this.port = port;
        this.ftpClient = new FTPClient();
    }

    /**
     * Function to connect to an FTP Server.
     *
     * @throws IOException if connecting to FTP server fails
     */
    public void connectToFtp() throws IOException {
        try {
            ftpClient.connect(hostname, port);
            ftpClient.login(userName, password);
        } catch (IOException ex) {
            throw new IOException("Error occurred while connecting to FTP server", ex);
        }
    }

    /**
     * Function to upload a file to an FTP Server.
     *
     * @param sourceFilePath source file path
     * @param targetFilePath target path of the remote file
     * @throws IOException if file upload fails
     */
    public void ftpFileUpload(String sourceFilePath, String targetFilePath) throws IOException {
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
        File localFile = new File(sourceFilePath);

        try (InputStream inputStream = new FileInputStream(localFile)) {
            log.info("Start uploading file " + sourceFilePath + " to " + targetFilePath);

            try {
                if (ftpClient.storeFile(targetFilePath, inputStream)) {
                    log.info("File has been uploaded to " + targetFilePath + " successfully.");
                }
            } catch (IOException ex) {
                throw new IOException("Error occurred while uploading file from " + sourceFilePath + " to " +
                                      targetFilePath, ex);
            }
        } catch (FileNotFoundException ex) {
            throw new IOException("File Not Found in " + sourceFilePath, ex);
        }
    }

    /**
     * Function to download a file from an FTP Server.
     *
     * @param sourceFilePath source file path
     * @param targetFilePath target path
     * @throws IOException if file download fails
     */
    public void ftpFileDownload(String sourceFilePath, File targetFilePath) throws IOException{
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

        try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(targetFilePath))) {
            log.info("Start downloading file " + sourceFilePath + " to " + targetFilePath);

            try {
                if (ftpClient.retrieveFile(sourceFilePath, outputStream)) {
                    log.info("File has been uploaded to " + targetFilePath + " successfully.");
                }
            } catch (IOException ex) {
                throw new IOException("Error occurred while downloading file from " + sourceFilePath + " to " +
                                      targetFilePath, ex);
            }
        } catch (FileNotFoundException ex) {
            throw new IOException("File Not Found in " + sourceFilePath, ex);
        }
    }

    /**
     * Function to list the set of directories on an FTP server.
     *
     * @param filePath file path to list directories
     * @return list of directories
     * @throws IOException if listing set of directories fails
     */
    public List<FTPFile> listDirectories(String filePath) throws IOException {
        FTPFile[] directoryList;
        try {
            directoryList = ftpClient.listDirectories(filePath);
        } catch (IOException ex){
            throw new IOException("Error occurred while listing directories", ex);
        }
        return Arrays.asList(directoryList);
    }

    /**
     * Function to list files of a directory on an FTP server.
     *
     * @param filePath file path to list files
     * @return list of files
     * @throws IOException if listing set of files fails
     */
    public List<FTPFile> listFiles(String filePath) throws IOException {
        List<FTPFile> files = new ArrayList<>();
        try{
            FTPFile[] filesAndDirectories = ftpClient.listFiles(filePath);

            for (FTPFile file : filesAndDirectories) {
                if (!file.isDirectory()) {
                    files.add(file);
                }
            }
        } catch (IOException ex){
            throw new IOException("Error occurred while listing files", ex);
        }
        return files;
    }

    /**
     * Function to list directories & files from a directory on an FTP server.
     *
     * @param filePath file path to list directories & files
     * @return list of directories & files
     * @throws IOException if listing set of directories & files fails
     */
    public List<FTPFile> listFilesAndDirectories(String filePath) throws IOException {
        FTPFile[] fileDirectoryList;
        try {
            fileDirectoryList = ftpClient.listFiles(filePath);
        } catch (IOException ex) {
            throw new IOException("Error occurred while listing files and directories", ex);
        }
        return Arrays.asList(fileDirectoryList);
    }

    /**
     * Function to create new directories in an FTP server
     *
     * @param dirPath directory path
     * @throws IOException if changing directory or creating new directory fails
     */
    public void makeDirectories(String dirPath) throws IOException {
        dirPath = dirPath.startsWith("/") ? dirPath.substring(1) : dirPath;
        String[] pathElements = dirPath.split("/");
        if (pathElements.length > 0) {
            for (String singleDir : pathElements) {
                boolean dirExisted = ftpClient.changeWorkingDirectory(singleDir);
                if (!dirExisted) {
                    boolean dirCreated = ftpClient.makeDirectory(singleDir);
                    if (dirCreated) {
                        log.debug("Created new directory : " + singleDir);
                        ftpClient.changeWorkingDirectory(singleDir);
                    } else {
                        log.error("Could not create directory : " + singleDir);
                        break;
                    }
                }
            }
            ftpClient.changeWorkingDirectory("/");
        }
    }

    /**
     * Function to check whether a directory exists in the file system in an FTP server
     *
     * @param dirPath absolute directory path
     * @return boolean whether directory exists in file system
     * @throws IOException if changing working directory fails
     */
    public boolean checkDirectoryExists(String dirPath) throws IOException {
        ftpClient.changeWorkingDirectory(dirPath);
        int returnCode = ftpClient.getReplyCode();
        if (returnCode == 550) {
            return false;
        }
        return true;
    }

    /**
     * Function to check whether a file exists in the file system in an FTP Server
     *
     * @param filePath if a file exists on file system in an FTP server
     * @return boolean whether a file exists in an FTP server
     * @throws IOException if file retrieving fails
     */
    public boolean checkFileExists(String filePath) throws IOException {
        try {
            return listFiles(filePath).size() == 1;
        } catch (IOException ex) {
            throw new IOException("Error occurred while retrieving files from " + filePath, ex);
        }
    }

    /**
     * Function to read a file in an FTP server
     *
     * @param filePath absolute file path
     * @return String value of the file content
     * @throws IOException if retrieving file stream fails
     */
    public String readFile(String filePath) throws IOException {
        String fileContent;
        try (InputStream inputStream = ftpClient.retrieveFileStream(filePath)){
            fileContent = IOUtils.toString(inputStream, "UTF-8");
        } catch (IOException ex) {
            throw new IOException("Error occurred while retrieving file stream " + filePath, ex);
        }
        return fileContent;
    }

    /**
     * Function to delete a file from an FTP server.
     *
     * @param filePath file path to delete
     * @return boolean to indicate whether the file is deleted
     * @throws IOException if deleting the file fails
     */
    public boolean deleteFile(String filePath) throws IOException {
        boolean deleted;
        try {
            deleted = ftpClient.deleteFile(filePath);
            if (deleted) {
                log.info("The file was deleted successfully.");
            } else {
                log.error("Could not delete the file.");
            }
        } catch (IOException ex) {
            throw new IOException("Error occurred while deleting file " + filePath, ex);
        }
        return deleted;
    }

    /**
     * Function to disconnect from an FTP Server.
     *
     * @throws IOException if disconnecting from FTT server fails
     */
    public void disconnectFromFtp() throws IOException {
        try {
            if (ftpClient.isConnected()) {
                ftpClient.logout();
                ftpClient.disconnect();
            }
        } catch (IOException ex) {
            throw new IOException("Error occurred while disconnecting from FTP server", ex);
        }
    }
}
