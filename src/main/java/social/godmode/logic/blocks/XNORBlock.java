package social.godmode.logic.blocks;

import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import social.godmode.logic.LogicBlock;

public class XNORBlock extends LogicBlock {

    public XNORBlock(BlockVec pos, Instance instance) {
        super("XNOR", pos, instance, Block.BONE_BLOCK);
    }

    @Override
    public void revise() {
        long poweredCount = getInputs().stream().filter(LogicBlock::isPowered).count();
        setPowered(poweredCount % 2 == 0);
    }

}
