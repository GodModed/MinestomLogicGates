package social.godmode.logic;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.Instance;

import java.util.HashMap;
import java.util.Map;

public class LogicGateManager {

    private static final Map<Instance, LogicGateManager> managers = new HashMap<>();

    @Getter
    private final Instance instance;
    private final Map<BlockVec, LogicBlock> logicBlocks = new HashMap<>();

    private LogicGateManager(Instance instance) {
        this.instance = instance;
    }

    public static LogicGateManager forInstance(Instance instance) {
        return managers.computeIfAbsent(instance, LogicGateManager::new);
    }

    public static void unregisterInstance(Instance instance) {
        managers.remove(instance);
    }

    public void addLogicBlock(LogicBlock block) {
        if (block == null || logicBlocks.containsKey(block.getBlockPosition())) return;
        logicBlocks.put(block.getBlockPosition(), block);
    }

    public LogicBlock getLogicBlock(BlockVec position) {
        return logicBlocks.get(position);
    }

    public void removeLogicBlock(LogicBlock block) {
        logicBlocks.remove(block.getBlockPosition());
    }

}
