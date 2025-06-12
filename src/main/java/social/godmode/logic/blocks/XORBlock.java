package social.godmode.logic.blocks;

import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import social.godmode.logic.CustomBlocks;
import social.godmode.logic.LogicBlock;

public class XORBlock extends LogicBlock {

    public XORBlock(BlockVec pos, Instance instance) {
        super("XOR", pos, instance, Block.BAMBOO_BLOCK);
    }

    @Override
    public void revise() {
        long poweredCount = getInputs().stream().filter(LogicBlock::isPowered).count();
        setPowered(poweredCount % 2 == 1);
    }

    @Override
    public CustomBlocks getType() {
        return CustomBlocks.XOR;
    }

}
