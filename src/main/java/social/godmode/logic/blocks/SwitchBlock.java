package social.godmode.logic.blocks;

import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import social.godmode.logic.LogicBlock;

public class SwitchBlock extends LogicBlock {

    public SwitchBlock(BlockVec position, Instance instance) {
        super("Switch", position, instance, Block.LEVER);
    }

    @Override
    public void setPowered(boolean powered) {

        super.setPowered(powered);

        if (isPowered()) {
            getInstance().setBlock(getPosition(), Block.LEVER.withProperty("powered", "true"));
        } else {
            getInstance().setBlock(getPosition(), Block.LEVER.withProperty("powered", "false"));
        }

    }

    @Override
    public void addInput(LogicBlock input) {
        throw new IllegalArgumentException("SwitchBlock does not support inputs.");
    }

}
