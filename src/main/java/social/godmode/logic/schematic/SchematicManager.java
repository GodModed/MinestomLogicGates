package social.godmode.logic.schematic;

import net.minestom.server.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SchematicManager {

    public static SchematicManager INSTANCE;

    public static File SCHEMATIC_DIR = new File("schematics");
    public Map<String, Schematic> schematics = new HashMap<>();
    public Map<Player, IRSchematic> pendingSchematics = new HashMap<>();
    public Map<Player, Schematic> pasteSchematics = new HashMap<>();

    public SchematicManager() {
        INSTANCE = this;
        loadAllSchematics();
    }

    public Schematic getSchematic(String name) {
        return schematics.get(name);
    }

    public IRSchematic getSchematic(Player player) {
        return pendingSchematics.computeIfAbsent(player, p -> new IRSchematic());
    }

    public void setPasteSchematic(Player player, Schematic schematic) {
        pasteSchematics.put(player, schematic);
    }

    public Schematic getPasteSchematic(Player player) {
        return pasteSchematics.get(player);
    }

    public void clearSchematic(Player player) {
        pendingSchematics.remove(player);
    }

    public void addSchematic(String name, Schematic schematic) {
        schematics.put(name, schematic);
    }

    public void removeSchematic(String name) {
        schematics.remove(name);
    }

    public void saveAllSchematics() {

        if (!SCHEMATIC_DIR.exists()) {
            SCHEMATIC_DIR.mkdirs();
        }

        for (Map.Entry<String, Schematic> entry : schematics.entrySet()) {
            String name = entry.getKey();
            Schematic schematic = entry.getValue();
            File schematicFile = new File(SCHEMATIC_DIR, name + ".schematic");
            schematic.saveToFile(schematicFile);
            System.out.println("Saved schematic: " + name);
        }
    }

    public void loadAllSchematics() {

        if (!SCHEMATIC_DIR.exists() || !SCHEMATIC_DIR.isDirectory()) {
            return;
        }

        File[] files = SCHEMATIC_DIR.listFiles((dir, name) -> name.endsWith(".schematic"));
        if (files == null) {
            return;
        }

        for (File file : files) {
            Schematic schematic = Schematic.loadFromFile(file);
            schematics.put(file.getName().replace(".schematic", ""), schematic);
            System.out.println("Loaded schematic: " + file.getName());
        }

    }

}
