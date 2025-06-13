package social.godmode.logic;

import lombok.Getter;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import social.godmode.logic.blocks.*;

import java.util.function.BiFunction;

@Getter
public enum CustomBlocks {
    LAMP(Block.REDSTONE_LAMP, LampBlock::new),
    NOT(Block.STRUCTURE_BLOCK, NOTBlock::new),
    AND(Block.COMPARATOR, ANDBlock::new),
    OR(Block.AMETHYST_BLOCK, ORBlock::new),
    CLOCK(Block.DAYLIGHT_DETECTOR, ClockBlock::new),
    BEND(Block.STRUCTURE_VOID, BendBlock::new),
    SWITCH(Block.LEVER, SwitchBlock::new),
    NOR(Block.AMETHYST_CLUSTER, NORBlock::new),
    NAND(Block.REPEATER, NANDBlock::new),
    XOR(Block.BAMBOO_BLOCK, XORBlock::new),
    XNOR(Block.BONE_BLOCK, XNORBlock::new),
    BUTTON(Block.STONE_BUTTON, ButtonBlock::new);

    private final Block block;
    private final BiFunction<BlockVec, Instance, LogicBlock> logicBlockConstructor;

    CustomBlocks(Block block, BiFunction<BlockVec, Instance, LogicBlock> logicBlockConstructor) {
        this.block = block;
        this.logicBlockConstructor = logicBlockConstructor;
    }

    public LogicBlock createLogicBlock(BlockVec position, Instance instance) {
        return logicBlockConstructor.apply(position, instance);
    }
}
