package social.godmode.logic.schematic;

import lombok.Data;
import lombok.NonNull;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import social.godmode.logic.CustomBlocks;
import social.godmode.logic.LogicBlock;
import social.godmode.logic.LogicGateManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IRSchematic {

    // TOOD: make schematic manager

    @Data
    public static class IRLogicBlockData {
        List<Integer> indices = new ArrayList<>();

        @NonNull
        CustomBlocks type;
        @NonNull
        BlockVec offset;
        @NonNull
        LogicBlock block;
    }

    List<IRLogicBlockData> blocks = new ArrayList<>();

    BlockVec origin;

    public void addBlock(LogicBlock block) {

        if (blocks.isEmpty()) {
            origin = block.getBlockPosition();
            blocks.add(new IRLogicBlockData(block.getType(), block.getBlockPosition().sub(origin), block));
            for (LogicBlock outputs : block.getOutputs()) {
                addBlock(outputs);
            }
            calculateIndices();
            return;
        }

        if (blocks.stream().anyMatch(b -> b.getType().equals(block.getType()) && b.getOffset().equals(block.getBlockPosition().sub(origin)))) {
            return;
        }

        blocks.add(new IRLogicBlockData(block.getType(), block.getBlockPosition().sub(origin), block));
        for (LogicBlock outputs : block.getOutputs()) {
            addBlock(outputs);
        }

        calculateIndices();
    }

    public void calculateIndices() {
        for (IRLogicBlockData blockInfo : blocks) {
            blockInfo.getIndices().clear();
            for (LogicBlock block : blockInfo.getBlock().getOutputs()) {
                int index = blocks.indexOf(blocks.stream()
                        .filter(b -> b.getType().equals(block.getType()) && b.getOffset().equals(block.getBlockPosition().sub(origin)))
                        .findFirst()
                        .orElse(null));
                if (index != -1) {
                    blockInfo.getIndices().add(index);
                }
            }
        }

        System.out.println("Calculated indices for schematic:");
        for (IRLogicBlockData blockInfo : blocks) {
            System.out.println("Block: " + blockInfo.getType() + ", Offset: " + blockInfo.getOffset() + ", Indices: " + blockInfo.getIndices());
        }

        Player player = SchematicManager.INSTANCE.pendingSchematics.entrySet()
                .stream()
                .filter(entry -> entry.getValue().equals(this))
                .findFirst()
                .map(Map.Entry::getKey)
                .orElse(null);

        if (player == null) return;
        SchematicManager.INSTANCE.setPasteSchematic(player, new Schematic(this));
    }

}
