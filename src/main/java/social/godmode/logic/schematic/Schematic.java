package social.godmode.logic.schematic;

import lombok.Data;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.instance.Instance;
import social.godmode.logic.CustomBlocks;
import social.godmode.logic.LogicBlock;
import social.godmode.logic.LogicGateManager;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Schematic implements Serializable {

    List<LogicBlockData> blocks = new ArrayList<>();

    public record LogicBlockData(int offsetX, int offsetY, int offsetZ, CustomBlocks type, List<Integer> indices) implements Serializable {}

    public Schematic(IRSchematic intermediateSchematic) {
        for (IRSchematic.IRLogicBlockData blockData : intermediateSchematic.blocks) {
            BlockVec offset = blockData.getOffset();
            blocks.add(
                    new LogicBlockData(
                        offset.blockX(),
                        offset.blockY(),
                        offset.blockZ(),
                        blockData.getType(),
                        blockData.getIndices()
                    )
            );

        }
    }

    public void paste(BlockVec pos, Instance instance) {

        LogicGateManager manager = LogicGateManager.forInstance(instance);

        for (LogicBlockData blockInfo : blocks) {
            BlockVec newPos = pos.add(blockInfo.offsetX(), blockInfo.offsetY(), blockInfo.offsetZ());
            blockInfo.type().createLogicBlock(newPos, instance);
        }

        for (LogicBlockData blockInfo : blocks) {
            for (int index : blockInfo.indices()) {
                BlockVec targetPos = pos.add(blocks.get(index).offsetX(), blocks.get(index).offsetY(), blocks.get(index).offsetZ());
                LogicBlock targetBlock = manager.getLogicBlock(targetPos);

                BlockVec sourcePos = pos.add(blockInfo.offsetX(), blockInfo.offsetY(), blockInfo.offsetZ());
                LogicBlock sourceBlock = manager.getLogicBlock(sourcePos);

                LogicBlock.link(sourceBlock, targetBlock);
            }
        }
    }

    public void saveToFile(File file) {
        try (ObjectOutputStream objectStream = new ObjectOutputStream(new FileOutputStream(file))) {
            objectStream.writeObject(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Schematic loadFromFile(File file) {
        try (ObjectInputStream objectStream = new ObjectInputStream(new FileInputStream(file))) {
            return (Schematic) objectStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
