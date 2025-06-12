package social.godmode.logic;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.AbstractDisplayMeta;
import net.minestom.server.entity.metadata.display.TextDisplayMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;

import java.util.ArrayList;

@Getter
public abstract class LogicBlock extends Entity {
    private final String name;
    private final ArrayList<LogicBlock> inputs = new ArrayList<>();
    private final ArrayList<LogicBlock> outputs = new ArrayList<>();
    private final Pos position;
    private boolean powered = false;

    private static final ArrayList<LogicBlock> allBlocks = new ArrayList<>();



    public LogicBlock(String name, BlockVec position, Instance instance, Block block) {
        super(EntityType.TEXT_DISPLAY);
        this.hasPhysics = false;
        setNoGravity(true);
        this.position = position.asVec().asPosition();
        Pos pos = position.asVec().asPosition().add(0.5, 0.1, 0.5);
        pos = pos.withPitch(-90);

        editEntityMeta(TextDisplayMeta.class, meta -> {
            meta.setText(Component.text(name));
            meta.setSeeThrough(true);
            meta.setBillboardRenderConstraints(AbstractDisplayMeta.BillboardConstraints.VERTICAL);
        });

        setInstance(instance, pos);
        instance.setBlock(position, block);
        this.name = name;

        allBlocks.add(this);
    }

    public void addInput(LogicBlock input) {
        if (inputs.contains(input)) return;

        inputs.add(input);
    }

    public void addOutput(LogicBlock output) {
        if (outputs.contains(output)) return;

        outputs.add(output);
    }

    public void setPowered(boolean powered) {
        if (this.powered == powered) return;
        this.powered = powered;
        revise();
    }

    public void revise() {
        for (LogicBlock output : outputs) {
            output.revise();
        }

    }

    @Override
    public void remove() {

        setPowered(false);

        instance.setBlock(position, Block.AIR);

        for (LogicBlock input : inputs) {
            input.outputs.remove(this);
        }

        for (LogicBlock output : outputs) {
            output.inputs.remove(this);
        }

        allBlocks.remove(this);

        // remove wires connected to this block
        BlockVec pos = new BlockVec(getPosition());

        for (int i = Wire.wires.size() - 1; i >= 0; i--) {
            Wire wire = Wire.wires.get(i);
            if (wire.getFrom().equals(pos) || wire.getTo().equals(pos)) {
                wire.remove();
            }
        }

        super.remove();
    }

    public static void link(LogicBlock input, LogicBlock output) {
        input.addOutput(output);
        output.addInput(input);
    }

    public static LogicBlock getLogicBlock(BlockVec pos) {
        for (LogicBlock block : allBlocks) {
            if (block.getPosition().equals(pos.asVec().asPosition())) {
                return block;
            }
        }
        return null;
    }

}
