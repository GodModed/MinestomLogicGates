package social.godmode.logic.blocks;

import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import social.godmode.logic.LogicBlock;

public class NotBlock extends LogicBlock {

    public NotBlock(BlockVec position, Instance instance) {
        super("Not", position, instance, Block.STRUCTURE_BLOCK);
    }

    @Override
    public void revise() {
        if (getInputs().isEmpty()) {
            setPowered(false);
        } else {
            setPowered(!getInputs().getFirst().isPowered());
        }

        super.revise();
    }

}
