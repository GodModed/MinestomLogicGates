package social.godmode.logic.blocks;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;
import social.godmode.logic.CustomBlocks;
import social.godmode.logic.LogicBlock;

public class ClockBlock extends LogicBlock {

    private Task task;

    public ClockBlock(BlockVec position, Instance instance, double frequency) {
        super("Clock", position, instance, Block.DAYLIGHT_DETECTOR.withProperty("inverted", "true"));

        this.task = MinecraftServer.getSchedulerManager().submitTask(() -> {
            setPowered(!isPowered());
            return TaskSchedule.millis((long) (1000 / frequency));
        });
    }

    public ClockBlock(BlockVec position, Instance instance) {
        this(position, instance, 10); // Default frequency of 1 Hz
    }

    @Override
    public void addInput(LogicBlock input) {
        throw new IllegalArgumentException("ClockBlock cannot have inputs.");
    }

    @Override
    public void remove() {
        if (task != null) {
            task.cancel();
            task = null;
        }
        super.remove();
    }

    @Override
    public void setPowered(boolean powered) {

        super.setPowered(powered);

        if (isPowered()) {
            setBlock(Block.DAYLIGHT_DETECTOR.withProperty("inverted", "false"));
        } else {
            setBlock(Block.DAYLIGHT_DETECTOR.withProperty("inverted", "true"));
        }

    }

    @Override
    public CustomBlocks getType() {
        return CustomBlocks.CLOCK;
    }

}
