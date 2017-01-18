package chat.willow.thump.helper.slf4j;

public class LoggerFactory {
    private static Logger logger;

    public static Logger getLogger(Class name) {
        if (logger == null) {
            logger = new LoggerImpl();
        }

        return logger;
    }
}
