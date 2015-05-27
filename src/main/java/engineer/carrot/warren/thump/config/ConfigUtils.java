package engineer.carrot.warren.thump.config;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import engineer.carrot.warren.thump.util.helper.LogHelper;

import javax.annotation.Nullable;
import java.io.*;

public class ConfigUtils {
    public static final String configLocation = "config.json";

    public static Gson createConfigGson() {
        return new GsonBuilder().setPrettyPrinting().serializeNulls().setVersion(0.1).create();
    }

    public static boolean doesConfigFileExist() {
        File configFile = new File(configLocation);
        return (configFile.exists() && !configFile.isDirectory());
    }

    public static boolean createDefaultConfig() {
        if (doesConfigFileExist()) {
            LogHelper.error("{} already exists, not overwriting", configLocation);
            return false;
        }

        String defaultConfig = createConfigGson().toJson(new Configuration(Lists.newArrayList(new ServerConfiguration())));
        if (defaultConfig.isEmpty()) {
            LogHelper.error("Failed to turn default Configuration in to a String");
            return false;
        }

        FileWriter writer;
        try {
            writer = new FileWriter(configLocation);
        } catch (IOException e) {
            LogHelper.error("Failed to write default Configuration: {}", e);

            return false;
        }

        try {
            writer.write(defaultConfig);
            writer.flush();
        } catch (IOException e) {
            LogHelper.error("Failed to write Configuration String to file", e);

            return false;
        }

        return true;
    }

    @Nullable
    public static Configuration readConfig() {
        if (!doesConfigFileExist()) {
            return null;
        }

        FileReader reader;
        try {
            reader = new FileReader(configLocation);
        } catch (FileNotFoundException e) {
            LogHelper.error("Couldn't find config file (despite previously thinking it existed?): {}", e);

            return null;
        }

        return new Gson().fromJson(reader, Configuration.class);
    }
}
