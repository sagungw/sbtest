package io.wijaya.external.stockbit_02.consumer;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class SynchronizedFileWriter {

    private String filePath;
    
    public SynchronizedFileWriter(String filePath) {
        this.filePath = filePath;
    }

    public synchronized void writeLine(String s) throws Exception {
        BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true));
        bw.write(s);
        bw.newLine();
        bw.flush();
    }

}
