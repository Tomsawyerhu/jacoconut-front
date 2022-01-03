package extern.utils;

import extern.storage.Storage;
import org.apache.log4j.Logger;

public class Calculator {
    private static final Logger logger = Logger.getLogger(Calculator.class);
    public static void calculateStatementCoverage(){
        int lines= Storage.lines.get();
        int executedLines=Storage.executeLines.get();
        logger.info("coverage rate:"+(double)(executedLines)/(double)lines);
    }
}
