package social.godmode.logic.blocks;

import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import social.godmode.logic.LogicBlock;

public class ANDBlock extends LogicBlock {

    public ANDBlock(BlockVec position, Instance instance) {
        super("AND", position, instance, Block.COMPARATOR);
    }

    @Override
    public void revise() {
        boolean allPowered = getInputs().stream().allMatch(LogicBlock::isPowered);
        setPowered(allPowered);
    }

}
