package social.godmode.logic.blocks;

import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import social.godmode.logic.LogicBlock;

public class NOTBlock extends LogicBlock {

    public NOTBlock(BlockVec position, Instance instance) {
        super("NOT", position, instance, Block.STRUCTURE_BLOCK);
    }

    @Override
    public void revise() {
        if (getInputs().isEmpty()) {
            setPowered(true);
        } else {
            setPowered(!getInputs().getFirst().isPowered());
        }

    }

}
