package social.godmode.logic.blocks;

import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import social.godmode.logic.LogicBlock;

public class AndBlock extends LogicBlock {

    public AndBlock(BlockVec position, Instance instance) {
        super("And", position, instance, Block.COMPARATOR);
    }

    @Override
    public void revise() {
        boolean allPowered = getInputs().stream().allMatch(LogicBlock::isPowered);
        setPowered(allPowered);
        super.revise();
    }

}
