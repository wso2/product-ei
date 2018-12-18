package org.wso2.carbon.esb.scenario.test.common.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Util class containing file related operations
 */
public class FileUtils {

    private static final String characterSet = "UTF-8";

    /**
     * Reads a file into a single String
     *
     * @param file file object to be read
     * @return a String representing contents of the file
     * @throws IOException in case of issue reaching, interpreting file
     */
    public static String readFile(File file) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
        return new String(encoded, characterSet);
    }

    /**
     * Reads a file into a single String
     *
     * @param filePath absolute path of the file
     * @return a String representing contents of the file
     * @throws IOException in case of issue reaching, interpreting file
     */
    public static String readFile(String filePath) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(filePath));
        return new String(encoded, characterSet);
    }
}
