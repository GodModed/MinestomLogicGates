package social.godmode.logic.blocks;

import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import social.godmode.logic.CustomBlocks;
import social.godmode.logic.LogicBlock;

public class LampBlock extends LogicBlock {

    public LampBlock(BlockVec position, Instance instance) {
        super("Lamp", position, instance, Block.REDSTONE_LAMP);
    }

    @Override
    public void revise() {

        if (getInputs().isEmpty()) {
            setPowered(false);
            return;
        }
        setPowered(getInputs().getFirst().isPowered());

        if (isPowered()) {
            setBlock(Block.REDSTONE_LAMP.withProperty("lit", "true"));
        } else {
            setBlock(Block.REDSTONE_LAMP.withProperty("lit", "false"));
        }

    }

    @Override
    public void addOutput(LogicBlock input) {
        throw new IllegalArgumentException("LampBlock cannot have outputs.");
    }

    @Override
    public CustomBlocks getType() {
        return CustomBlocks.LAMP;
    }

}
