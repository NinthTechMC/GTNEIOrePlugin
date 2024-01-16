package pistonmc.gtnop;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import pistonmc.gtnop.api.GTNEIOreAPI;
import pistonmc.gtnop.api.IOreWorldNamed;

/**
 * Handles the config file
 */
public class ConfigHandler {
    private List<IOreWorldNamed> configDims;
    private boolean changed;

    public ConfigHandler() {
        this.configDims = null;
        this.changed = false;
    }

    public List<IOreWorldNamed> getDimList() {
        if (this.configDims == null) {
            this.configDims = new ArrayList<IOreWorldNamed>();
            File configFile = getConfigFile();
            if (configFile.exists()) {
                try (BufferedReader reader = Files.newBufferedReader(configFile.toPath(), StandardCharsets.UTF_8)) {
                    GTNEIOreAPI api = GTNEIOreAPI.getInstance();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        int equalSignIndex = line.indexOf('=');
                        if (equalSignIndex == -1) {
                            continue;
                        }
                        String dimName = line.substring(0, equalSignIndex).trim();
                        String iconString = line.substring(equalSignIndex + 1).trim();
                        this.configDims.add(api.createOreWorld(dimName, iconString));
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return Collections.unmodifiableList(this.configDims);
    }

    public void addDim(IOreWorldNamed dim) {
        this.configDims.add(dim);
        this.changed = true;
    }

    public void save() {
        if (!this.changed) {
            return;
        }
        this.changed = false;
        StringBuilder sb = new StringBuilder();
        for (IOreWorldNamed dim : this.configDims) {
            sb.append(dim.getDimensionName()).append('=').append(dim.getIconString()).append(System.lineSeparator());
        }
        File configFile = getConfigFile();
        try {
            Files.write(configFile.toPath(), sb.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static File getConfigFile() {
        return new File("").toPath().resolve("config").resolve(ModInfo.MODID + ".cfg").toFile();
    }

}
