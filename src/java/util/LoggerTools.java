package util;

import org.apache.log4j.Logger;

public class LoggerTools {

    private static Logger logger = Logger.getLogger(LoggerTools.class);



    public static void info(String info){
        logger.info(info);
    }

    public void info(String info,Exception e){
        logger.info(info,e);
    }

    public  void debug(String debug){
        logger.debug(debug);
    }


}
