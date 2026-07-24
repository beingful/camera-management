package camera.logging;

import camera.models.Identity;
import camera.utils.StringUtils;
import camera.validation.ValidationSupport;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public final class LoggerFactory {
    private static final Path LOG_DIRECTORY_PATH = Path.of("logs");
    private static final Map<String, Handler> fileHandlers = new HashMap<>();

    private LoggerFactory() {
    }

    public static ILogger systemLogger(Class<?> source) {
        ValidationSupport.validateRequired("Logger source", source);

        return new SystemLogger(createLogger(source.getName(), "system"));
    }

    public static ILogger cameraLogger(Class<?> source, Identity cameraIdentity) {
        ValidationSupport.validateRequired("Logger source", source);
        ValidationSupport.validateRequired("Camera identity", cameraIdentity);

        return new CameraLogger(
                createLogger(source.getName() + ".camera." + cameraIdentity.id, StringUtils.sanitize(cameraIdentity.name)),
                cameraIdentity);
    }

    private static synchronized Logger createLogger(String name, String logFileName) {
        Logger logger = Logger.getLogger(name);
        logger.setUseParentHandlers(false);
        logger.setLevel(Level.INFO);

        Handler handler = fileHandler(logFileName);
        boolean alreadyAttached = false;

        for (Handler attachedHandler : logger.getHandlers()) {
            if (attachedHandler == handler) {
                alreadyAttached = true;
                break;
            }
        }

        if (!alreadyAttached) {
            logger.addHandler(handler);
        }

        return logger;
    }

    private static Handler fileHandler(String logFileName) {
        Handler fileHandler = fileHandlers.get(logFileName);

        if (fileHandler == null) {
            try {
                Files.createDirectories(LOG_DIRECTORY_PATH);
                Path logFilePath = LOG_DIRECTORY_PATH.resolve(logFileName + ".log");
                FileHandler handler = new FileHandler(logFilePath.toString(), true);
                handler.setFormatter(new SimpleFormatter());
                handler.setLevel(Level.INFO);
                fileHandlers.put(logFileName, handler);
                fileHandler = handler;
            }
            catch (IOException exception) {
                throw new IllegalStateException("Could not initialize logger file handler.", exception);
            }
        }

        return fileHandler;
    }
}
