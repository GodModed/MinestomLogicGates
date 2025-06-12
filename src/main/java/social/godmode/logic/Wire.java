package social.godmode.logic;

import lombok.Getter;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.BlockDisplayMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;

import java.util.ArrayList;

@Getter
public class Wire extends Entity {

    public static double SIZE = 0.3;
    public static ArrayList<Wire> wires = new ArrayList<>();

    private BlockVec from;
    private BlockVec to;

    public Wire(BlockVec from, BlockVec to, Instance instance) {
        super(EntityType.BLOCK_DISPLAY);
        setNoGravity(true);
        hasPhysics = false;
        this.from = from;
        this.to = to;
        Point fromMiddle = from.add(0.5, 0.5, 0.5);
        Point toMiddle = to.add(0.5, 0.5, 0.5);
        setInstance(instance, fromMiddle);

        double distance = fromMiddle.distance(toMiddle);

        editEntityMeta(BlockDisplayMeta.class, meta -> {
            meta.setBlockState(Block.MAGENTA_GLAZED_TERRACOTTA);
            meta.setScale(new Vec(SIZE, SIZE, distance));
            meta.setTranslation(
                    new Vec(
                            -SIZE / 2
                    )
            );
        });

        lookAt(toMiddle);

        wires.add(this);
    }

    public Wire(Pos from, Pos to, Instance instance) {
        this(new BlockVec(from), new BlockVec(to), instance);
    }

    @Override
    public void remove() {
        super.remove();
        wires.remove(this);
    }
}
