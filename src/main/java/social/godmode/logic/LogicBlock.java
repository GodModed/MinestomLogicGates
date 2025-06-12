package social.godmode.logic;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.entity.metadata.display.BlockDisplayMeta;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.timer.TaskSchedule;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class LogicBlock extends Entity {
    private final String name;
    private final List<LogicBlock> inputs = new ArrayList<>();
    private final List<LogicBlock> outputs = new ArrayList<>();
    private final List<Wire> connectedWires = new ArrayList<>();
    private final BlockVec blockPosition;
    private boolean powered = false;
    private final LogicGateManager manager;
    private final Entity blockDisplay;

    public LogicBlock(String name, BlockVec position, Instance instance, Block block) {
        super(EntityType.TEXT_DISPLAY);
        this.hasPhysics = false;
        setNoGravity(true);
        this.blockPosition = position;
        Pos pos = position.asVec().asPosition().add(0.5, 1.01, 0.5);
        pos = pos.withPitch(-90);

        editEntityMeta(TextDisplayMeta.class, meta -> {
            meta.setText(Component.text(name));
            meta.setSeeThrough(true);
            meta.setBillboardRenderConstraints(AbstractDisplayMeta.BillboardConstraints.VERTICAL);
        });

        Pos finalPos = pos;
        setInstance(instance).thenRun(() -> {
            teleport(finalPos);
        });
        instance.setBlock(position, Block.BARRIER);

        this.blockDisplay = new Entity(EntityType.BLOCK_DISPLAY);
        blockDisplay.setNoGravity(true);

        blockDisplay.editEntityMeta(BlockDisplayMeta.class, meta -> {
            meta.setBlockState(block);
            meta.setScale(new Vec(1, 0.1, 1));
            meta.setTransformationInterpolationDuration(5);
            meta.setTransformationInterpolationStartDelta(0);
        });

        blockDisplay.setInstance(instance, blockPosition);

        MinecraftServer.getSchedulerManager().scheduleTask(() -> {
            blockDisplay.editEntityMeta(BlockDisplayMeta.class, meta -> {
                meta.setScale(new Vec(1, 1, 1));
            });
            return TaskSchedule.stop();
        }, TaskSchedule.tick(2));

        this.name = name;

        this.manager = LogicGateManager.forInstance(instance);
        this.manager.addLogicBlock(this);
        revise();
    }

    public void addInput(LogicBlock input) {
        if (inputs.contains(input)) return;

        inputs.add(input);
        revise();
    }

    public void addOutput(LogicBlock output) {
        if (outputs.contains(output)) return;

        outputs.add(output);

    }

    public void addWire(Wire wire) {
        if (connectedWires.contains(wire)) return;

        connectedWires.add(wire);

        wire.revise();
    }

    public void removeWire(Wire wire) {
        connectedWires.remove(wire);
    }

    public void setPowered(boolean powered) {
        if (this.powered == powered) return;
        this.powered = powered;

        for (LogicBlock output : outputs) {
            output.revise();
        }

        for (Wire wire : connectedWires) {
            wire.revise();
        }

    }

    public void revise() {}

    public void setBlock(Block block) {
        blockDisplay.editEntityMeta(BlockDisplayMeta.class, meta -> {
            meta.setBlockState(block);
        });
    }

    @Override
    public void remove() {

        setPowered(false);

        for (LogicBlock input : inputs) {
            input.outputs.remove(this);
        }

        for (LogicBlock output : outputs) {
            output.inputs.remove(this);
        }

        for (int i = connectedWires.size() - 1; i >= 0; i--) {
            Wire wire = connectedWires.get(i);
            LogicBlock otherBlock = wire.other(this);
            otherBlock.removeWire(wire);
            removeWire(wire);
            wire.remove();
        }

        manager.removeLogicBlock(this);

        blockDisplay.remove();
        instance.setBlock(blockPosition, Block.AIR);
        super.remove();

    }

    public abstract CustomBlocks getType();

    public static void link(LogicBlock input, LogicBlock output) {
        input.addOutput(output);
        output.addInput(input);

        Wire wire = new Wire(input, output, input.getInstance());
        input.addWire(wire);
        output.addWire(wire);

    }

}
