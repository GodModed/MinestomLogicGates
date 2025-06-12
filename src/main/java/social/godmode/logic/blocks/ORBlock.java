package social.godmode.logic.blocks;

import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import social.godmode.logic.LogicBlock;

public class OrBlock extends LogicBlock {

    public OrBlock(BlockVec position, Instance instance) {
        super("Or", position, instance, Block.AMETHYST_BLOCK);
    }

    @Override
    public void revise() {
        boolean anyPowered = getInputs().stream().anyMatch(LogicBlock::isPowered);
        setPowered(anyPowered);

    }

}
