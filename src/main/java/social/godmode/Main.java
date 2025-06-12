package social.godmode;
import lombok.extern.slf4j.Slf4j;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.*;
import net.minestom.server.event.server.ServerTickMonitorEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.registry.DynamicRegistry;
import net.minestom.server.world.DimensionType;
import social.godmode.logic.*;
import social.godmode.logic.blocks.SwitchBlock;
import social.godmode.logic.schematic.IRSchematic;
import social.godmode.logic.schematic.Schematic;
import social.godmode.logic.schematic.SchematicCommand;
import social.godmode.logic.schematic.SchematicManager;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;


@Slf4j
public class Main {

    private static final String DIMENSION_ID = "dimension:bright";
    private static InstanceContainer lobbyInstance;
    private static final SchematicManager schematicManager = new SchematicManager();

    public static void main(String[] args) {
        MinecraftServer server = MinecraftServer.init();

        InstanceContainer instance = MinecraftServer.getInstanceManager().createInstanceContainer();
        instance.setGenerator(unit -> {
            unit.modifier().fillHeight(0, 1, Block.QUARTZ_BLOCK);
        });

        initializeDimensionAndInstances();
        initializeEventHandlers();
        msptBossBar();

        MinecraftServer.getSchedulerManager().buildShutdownTask(schematicManager::saveAllSchematics);
        MinecraftServer.getCommandManager().register(new SchematicCommand());

        server.start("0.0.0.0", 25565);
    }

    private static void initializeDimensionAndInstances() {
        DimensionType type = DimensionType.builder()
                .ambientLight(1)
                .fixedTime(18000L)
                .build();
        DynamicRegistry.Key<DimensionType> dimensionKey = MinecraftServer.getDimensionTypeRegistry().register(DIMENSION_ID, type);
        lobbyInstance = MinecraftServer.getInstanceManager().createInstanceContainer(dimensionKey);
        lobbyInstance.setGenerator(chunk -> chunk.modifier().fillHeight(0, 1, Block.QUARTZ_BRICKS));
    }

    private static void addItem(Player player, Material material, String name) {
        ItemStack itemStack = ItemStack.builder(material)
                .customName(Component.text(name))
                .glowing()
                .build();
        player.getInventory().addItemStack(itemStack);
    }

    private static void addItems(Player player) {
        addItem(player, Material.STICK, "Wire tool");
        addItem(player, Material.BLAZE_ROD, "Remove tool");
        addItem(player, Material.BUCKET, "Copy tool");
        addItem(player, Material.RAW_COPPER_BLOCK, "Paste tool");

        for (CustomBlocks customBlocks : CustomBlocks.values()) {
            addItem(player, customBlocks.getBlock().registry().material(), customBlocks.name());
        }

    }

    private static void initializeEventHandlers() {

        GlobalEventHandler handler = MinecraftServer.getGlobalEventHandler();
        handler.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            final Player player = event.getPlayer();
            event.setSpawningInstance(lobbyInstance);
            player.setRespawnPoint(new Pos(0, 1, 0));
        });

        handler.addListener(PlayerSpawnEvent.class, event -> {
            final Player player = event.getPlayer();
            addItems(player);
        });

        handler.addListener(PlayerBlockPlaceEvent.class, event -> {
            event.setCancelled(true);
            Block block = event.getBlock();
            BlockVec position = event.getBlockPosition();

            Material material = block.registry().material();
            if (material == null) {
                event.getPlayer().sendMessage(Component.text("You placed an unknown block: " + block.name()));
                return;
            }

            for (CustomBlocks customBlock : CustomBlocks.values()) {
                if (customBlock.getBlock().registry().material() == material) {
                    LogicBlock logicBlock = customBlock.createLogicBlock(position, event.getInstance());
                    event.getPlayer().sendMessage(Component.text("Placed " + logicBlock.getName()));
                    return;
                }
            }

            if (material == Material.RAW_COPPER_BLOCK) {

                Schematic schematic = schematicManager.getPasteSchematic(event.getPlayer());
                if (schematic == null) return;

                schematic.paste(event.getBlockPosition(), event.getInstance());

            }

        });

        Map<Player, LogicBlock> wireToolSelection = new HashMap<>();

        handler.addListener(PlayerBlockInteractEvent.class, event -> {

            LogicGateManager manager = LogicGateManager.forInstance(event.getInstance());
            LogicBlock logicBlock = manager.getLogicBlock(event.getBlockPosition());
            if (logicBlock == null) return;

            if (event.getPlayer().getItemInHand(event.getHand()).material() == Material.STICK) {
                if (wireToolSelection.get(event.getPlayer()) == null) {
                    wireToolSelection.put(event.getPlayer(), logicBlock);
                    event.getPlayer().sendMessage(Component.text("Selected " + logicBlock.getName() + ". Click another block to link them."));
                    return;
                }
                LogicBlock selectedBlock = wireToolSelection.get(event.getPlayer());
                if (selectedBlock != logicBlock) {
                    LogicBlock.link(selectedBlock, logicBlock);
                    event.getPlayer().sendMessage(Component.text("Linked " + selectedBlock.getName() + " to " + logicBlock.getName()));
                } else {
                    event.getPlayer().sendMessage(Component.text("Unselected " + selectedBlock.getName()));
                }
                wireToolSelection.remove(event.getPlayer());

            } else if (event.getPlayer().getItemInHand(event.getHand()).material() == Material.BLAZE_ROD) {
                logicBlock.remove();
                event.getPlayer().sendMessage(Component.text("Removed " + logicBlock.getName()));
            } else if (event.getPlayer().getItemInHand(event.getHand()).material() == Material.BUCKET) {
                schematicManager.getSchematic(event.getPlayer()).addBlock(logicBlock);
                event.getPlayer().sendMessage(Component.text("Added " + logicBlock.getName()));
            }
        });

        handler.addListener(PlayerPickBlockEvent.class, event -> {
            LogicGateManager manager = LogicGateManager.forInstance(event.getInstance());
            LogicBlock logicBlock = manager.getLogicBlock(event.getBlockPosition());
            if (logicBlock == null) return;

            if (!(logicBlock instanceof SwitchBlock)) return;

            logicBlock.setPowered(!logicBlock.isPowered());

        });

        handler.addListener(PlayerBlockBreakEvent.class, event -> {
           event.setCancelled(true);
        });
    }

    private static void msptBossBar() {
        BossBar bossBar = BossBar.bossBar(Component.empty(), 1f, BossBar.Color.GREEN, BossBar.Overlay.PROGRESS);
        DecimalFormat dec = new DecimalFormat("0.00");
        MinecraftServer.getGlobalEventHandler().addListener(ServerTickMonitorEvent.class, e -> {
            double tickTime = Math.floor(e.getTickMonitor().getTickTime() * 100.0) / 100.0;
            bossBar.name(
                    Component.text()
                            .append(Component.text("MSPT: " + dec.format(tickTime)))
            );
            bossBar.progress(Math.min((float)tickTime / (float)MinecraftServer.TICK_MS, 1f));

            if (tickTime > MinecraftServer.TICK_MS) {
                bossBar.color(BossBar.Color.RED);
            } else {
                bossBar.color(BossBar.Color.GREEN);
            }
        });
        MinecraftServer.getGlobalEventHandler().addListener(PlayerSpawnEvent.class, e -> {
            e.getPlayer().showBossBar(bossBar);
        });
    }
}