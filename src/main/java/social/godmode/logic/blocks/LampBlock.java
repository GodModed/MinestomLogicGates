package social.godmode.logic.blocks;

import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import social.godmode.logic.LogicBlock;

public class LampBlock extends LogicBlock {

    public LampBlock(BlockVec position, Instance instance) {
        super("Lamp", position, instance, Block.REDSTONE_LAMP);
    }

    @Override
    public void revise() {

        if (getInputs().isEmpty()) setPowered(false);
        setPowered(getInputs().getFirst().isPowered());

        if (isPowered()) {
            getInstance().setBlock(getPosition(), Block.REDSTONE_LAMP.withProperty("lit", "true"));
        } else {
            getInstance().setBlock(getPosition(), Block.REDSTONE_LAMP.withProperty("lit", "false"));
        }

        super.revise();
    }

    @Override
    public void addOutput(LogicBlock input) {
        throw new IllegalArgumentException("LampBlock cannot have outputs.");
    }

}
