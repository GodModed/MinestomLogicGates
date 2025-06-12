package social.godmode.logic.blocks;

import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import social.godmode.logic.LogicBlock;

public class BendBlock extends LogicBlock {

    public BendBlock(BlockVec position, Instance instance) {
        super("Bend", position, instance, Block.BARRIER);
    }

    @Override
    public void revise() {
        setPowered(getInputs().stream().anyMatch(LogicBlock::isPowered));
        super.revise();
    }

}
