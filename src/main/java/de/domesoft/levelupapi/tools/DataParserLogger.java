package de.domesoft.levelupapi.tools;

import de.domesoft.levelupapi.dataparse.DataParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class DataParserLogger {
    @Autowired
    public DataParserLogger(Logger logger){
        logger = LogManager.getLogger(DataParser.class);
    }
}
