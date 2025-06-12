package social.godmode.logic.schematic;

import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.command.builder.arguments.ArgumentLiteral;
import net.minestom.server.command.builder.arguments.ArgumentStringArray;
import net.minestom.server.command.builder.arguments.ArgumentWord;
import net.minestom.server.command.builder.condition.Conditions;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.entity.Player;

public class SchematicCommand extends Command {

    public SchematicCommand() {
        super("schematic");
        addSubcommand(new ClearCommand());
        addSubcommand(new SaveCommand());
        addSubcommand(new LoadCommand());


        setCondition(Conditions::playerOnly);

    }

    private static class ClearCommand extends Command {

        public ClearCommand() {
            super("clear");

            setDefaultExecutor((sender, context) -> {
                Player player = (Player) sender;
                SchematicManager schematicManager = SchematicManager.INSTANCE;
                schematicManager.clearSchematic(player);

                schematicManager.pasteSchematics.remove(player);

                sender.sendMessage("Cleared your current schematic.");
            });
        }

    }

    private static class SaveCommand extends Command {

        public SaveCommand() {
            super("save");

            Argument<String> schematicName = new ArgumentWord("name");

            addSyntax((sender, context) -> {
                Player player = (Player) sender;
                SchematicManager schematicManager = SchematicManager.INSTANCE;
                IRSchematic schematic = schematicManager.getSchematic(player);

                String name = context.get(schematicName);

                if (name == null || name.isEmpty()) {
                    sender.sendMessage("Please provide a name for the schematic.");
                    return;
                }

                if (schematic.blocks.isEmpty()) {
                    sender.sendMessage("Your schematic is empty. Add blocks before saving.");
                    return;
                }

                Schematic newSchematic = new Schematic(schematic);
                schematicManager.addSchematic(name, newSchematic);

                schematicManager.saveAllSchematics();

            }, schematicName);

        }

    }

    private static class LoadCommand extends Command {

        public LoadCommand() {
            super("load");

            Argument<String> schematicName = new ArgumentWord("name")
                    .setSuggestionCallback(
                            (sender, context, suggestion) -> {
                                SchematicManager schematicManager = SchematicManager.INSTANCE;
                                for (String name : schematicManager.schematics.keySet()) {
                                    suggestion.addEntry(new SuggestionEntry(name));
                                }
                            }
                    );

            addSyntax((sender, context) -> {
                Player player = (Player) sender;
                SchematicManager schematicManager = SchematicManager.INSTANCE;

                String name = context.get(schematicName);
                Schematic schematic = schematicManager.getSchematic(name);

                if (schematic == null) {
                    sender.sendMessage("Schematic not found: " + name);
                    return;
                }

                schematicManager.setPasteSchematic(player, schematic);
                sender.sendMessage("Loaded schematic: " + name);
            }, schematicName);

        }
    }

}
