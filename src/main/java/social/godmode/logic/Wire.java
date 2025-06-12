package social.godmode.logic;

import lombok.Getter;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.display.BlockDisplayMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.timer.TaskSchedule;

import java.util.ArrayList;

@Getter
public class Wire extends Entity {

    public static double SIZE = 0.3;

    private final LogicBlock from;
    private final LogicBlock to;

    public Wire(LogicBlock from, LogicBlock to, Instance instance) {
        super(EntityType.BLOCK_DISPLAY);
        setNoGravity(true);
        hasPhysics = false;
        this.from = from;
        this.to = to;
        Point fromMiddle = from.getBlockPosition().add(0.5);
        Point toMiddle = to.getBlockPosition().add(0.5);

        double distance = fromMiddle.distance(toMiddle);

        editEntityMeta(BlockDisplayMeta.class, meta -> {
            meta.setBlockState(Block.BLACK_WOOL);
            meta.setScale(new Vec(SIZE, SIZE, 0.1));
            meta.setTranslation(
                    new Vec(
                            -SIZE / 2
                    )
            );
            meta.setTransformationInterpolationDuration(5);
            meta.setTransformationInterpolationStartDelta(0);

        });

        setInstance(instance).thenRun(() -> {
            teleport(new Pos(
                    fromMiddle.x(),
                    fromMiddle.y(),
                    fromMiddle.z()
            ));
            lookAt(toMiddle);
        });

        MinecraftServer.getSchedulerManager().scheduleTask(() -> {
            editEntityMeta(BlockDisplayMeta.class, meta -> {
                meta.setScale(new Vec(SIZE, SIZE, distance));
            });
            return TaskSchedule.stop();
        }, TaskSchedule.tick(2));

    }

    public LogicBlock other(LogicBlock block) {
        if (block == from) {
            return to;
        } else if (block == to) {
            return from;
        }

        else throw new IllegalArgumentException("Block is not part of this wire");
    }

    public void revise() {
        if (from.isPowered()) {
            editEntityMeta(BlockDisplayMeta.class, meta -> {
                meta.setBlockState(Block.RED_WOOL);
            });
        } else {
            editEntityMeta(BlockDisplayMeta.class, meta -> {
                meta.setBlockState(Block.BLACK_WOOL);
            });
        }
    }

}
