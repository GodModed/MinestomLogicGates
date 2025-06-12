package social.godmode.logic;

import lombok.Getter;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import social.godmode.logic.blocks.*;

@Getter
public enum CustomBlocks {
    LAMP(Block.REDSTONE_LAMP, LampBlock.class),
    NOT(Block.STRUCTURE_BLOCK, NotBlock.class),
    AND(Block.COMPARATOR, AndBlock.class),
    OR(Block.AMETHYST_BLOCK, OrBlock.class),
    CLOCK(Block.DAYLIGHT_DETECTOR, ClockBlock.class),
    BEND(Block.BARRIER, BendBlock.class),
    SWITCH(Block.LEVER, SwitchBlock.class);

    private final Block block;
    private final Class<? extends LogicBlock> logicBlockClazz;

    CustomBlocks(Block block, Class<? extends LogicBlock> logicBlockClazz) {
        this.block = block;
        this.logicBlockClazz = logicBlockClazz;
    }

    public LogicBlock createLogicBlock(BlockVec position, Instance instance) {
        try {
            return logicBlockClazz.getConstructor(BlockVec.class, Instance.class).newInstance(position, instance);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create LogicBlock instance for " + this.name(), e);
        }
    }
}
