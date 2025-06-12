package social.godmode.logic.blocks;

import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import social.godmode.logic.CustomBlocks;
import social.godmode.logic.LogicBlock;

public class NORBlock extends LogicBlock {

    public NORBlock(BlockVec pos, Instance instance) {
        super("NOR", pos, instance, Block.AMETHYST_CLUSTER);
    }

    @Override
    public void revise() {
        // NOR logic: Output is true only if all inputs are false
        boolean anyPowered = getInputs().stream().anyMatch(LogicBlock::isPowered);
        setPowered(!anyPowered);
    }

    @Override
    public CustomBlocks getType() {
        return CustomBlocks.NOR;
    }

}
