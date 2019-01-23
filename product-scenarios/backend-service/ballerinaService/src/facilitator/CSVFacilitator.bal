import ballerina/io;
import ballerina/log;

function readNext(io:CSVChannel csvChannel) returns string[] {
    match csvChannel.getNext() {
        string[] records => {
            return records;
        }
        error err => {
            throw err;
        } () => {
        error e = { message: "Record channel not initialized properly" };
        throw e;
    }
    }
}

function splitRequestRecord(string stringRecord) returns string[] {
    string[] csvRecord = stringRecord.split(",");
    return csvRecord;
}

function closeReadableCSVChannel(io:CSVChannel csvChannel) {
    match csvChannel.close() {
        error channelCloseError => {
            log:printError("Error occured while closing the channel: ", err = channelCloseError);
        } () => io:println("CSV channel closed successfully.");
    }
}

public function validateCSV(string csvData, string messageId) returns boolean {
    string presetFilePath = "./resources/csv/request/" + messageId + ".csv";
    io:CSVChannel csvChannel = io:openCsvFile(presetFilePath);
    string[] csvRecords = csvData.split("\n");
    boolean result;
    int i = 0;

    while (csvChannel.hasNext()) {
        match csvChannel.getNext() {
            string[] presetRecord => {
                string[] requestRecord = splitRequestRecord(csvRecords[i]);
                i++;
                int recordLength = lengthof requestRecord;
                if (recordLength == (lengthof presetRecord)) {
                    int j = 0;
                    while (j < recordLength) {
                        if (requestRecord[j] == (presetRecord[j])) {
                            result = true;
                            j++;
                        } else {
                            string finalElement = presetRecord[j];
                            if (requestRecord[j] == (finalElement.replace("\n", ""))) {
                                result = true;
                                j++;
                            } else {
                                return false;
                            }
                        }
                    }
                } else {
                    return false;
                }
            } () => {
            error e = { message: "Record channel not initialized properly" };
            throw e;
        }
            error err => {
                throw err;
            }
        }
    }
    closeReadableCSVChannel(csvChannel);
    return result;
}

public function getCSVResponse(string messageId) returns string {
    string responseFilePath = "./resources/csv/response/" + messageId + ".csv";
    io:CSVChannel csvChannel = io:openCsvFile(responseFilePath);
    string responseString = "";

    while (csvChannel.hasNext()) {
        match csvChannel.getNext() {
            string[] presetRecord => {

                foreach elem in presetRecord  {
                    responseString = responseString + elem + ",";
                }
            } () => {
            error e = { message: "Record channel not initialized properly" };
            throw e;
        } error err => {
            throw err;
        }
        }
        responseString = responseString.substring(0, (responseString.length() - 1));
        responseString = responseString + "\n";
    }
    return responseString;
}

public function getCSVContentValidationError() returns string{
    return "content mismatch in csv payload";
}

public function getCSVBadStringError() returns string{
    return "error in csv";
}
