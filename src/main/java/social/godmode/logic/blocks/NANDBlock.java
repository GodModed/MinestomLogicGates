package social.godmode.logic.blocks;

import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import social.godmode.logic.LogicBlock;

public class NANDBlock extends LogicBlock {

    public NANDBlock(BlockVec pos, Instance instance) {
        super("NAND", pos, instance, Block.REPEATER);
    }

    @Override
    public void revise() {
        boolean allPowered = getInputs().stream().allMatch(LogicBlock::isPowered);
        setPowered(!allPowered);
    }

}
