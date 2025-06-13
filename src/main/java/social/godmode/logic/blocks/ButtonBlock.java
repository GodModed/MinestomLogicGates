package social.godmode.logic.blocks;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.timer.TaskSchedule;
import social.godmode.logic.CustomBlocks;
import social.godmode.logic.LogicBlock;

public class ButtonBlock extends LogicBlock {

    public ButtonBlock(BlockVec position, Instance instance) {
        super("Button", position, instance, Block.STONE_BUTTON);
    }

    @Override
    public void setPowered(boolean powered) {
        super.setPowered(powered);
        if (isPowered()) {
            MinecraftServer.getSchedulerManager().scheduleTask(() -> {
                setPowered(false);
                return TaskSchedule.stop();
            }, TaskSchedule.tick(2));
        }
    }

    @Override
    public CustomBlocks getType() {
        return CustomBlocks.BUTTON;
    }

}
