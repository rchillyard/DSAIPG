package com.phasmidsoftware.dsaipg.util.files;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * @param <T>
 * @author Harshit Raj
 */
public class FileHandlerImpl_CSV<T extends FileData> implements FileHandler<T> {
    /**
     * @param colName  Coma separated names of columns
     * @param fileName name of file to be saved with path and extension
     * @param data     list of data where every item in list is a row
     */
    public boolean writecsv(String colName, String fileName, Collection<T> data) {
        File f = new File(fileName);

        try {
            //noinspection ResultOfMethodCallIgnored
            f.createNewFile();
            // try-with-resources: bw.close() used to be the last statement, so a failure in any
            // row.toFile() leaked the handle and left a truncated CSV. UTF-8 is explicit so the
            // file does not vary with the machine's default charset.
            try (BufferedWriter bw = Files.newBufferedWriter(f.toPath(), StandardCharsets.UTF_8)) {
                bw.write(colName);
                for (T row : data) {
                    bw.write(row.toFile());
                }
            }
        } catch (IOException e) {

            e.printStackTrace();
            return false;
        }
        return true;
    }


}
