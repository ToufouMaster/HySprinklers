package fr.toufoumaster.hytalevr.Blocks;

import com.hypixel.hytale.builtin.adventure.farming.states.TilledSoilBlock;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class SprinklerSystem extends EntityTickingSystem<ChunkStore> {
    float seconds = 0;

    public void tick(float dt, int index, @Nonnull ArchetypeChunk archetypeChunk, @Nonnull Store store, @Nonnull CommandBuffer commandBuffer) {
        seconds += dt;
        if (seconds < 3) return;
        seconds -= 3;
        SprinklerBlock block = (SprinklerBlock) archetypeChunk.getComponent(index, SprinklerBlock.getComponentType());
        assert block != null;

        BlockModule.BlockStateInfo blockInfo = (BlockModule.BlockStateInfo)archetypeChunk.getComponent(index, BlockModule.BlockStateInfo.getComponentType());
        assert blockInfo != null;

        int blockIndex = blockInfo.getIndex();
        Ref<ChunkStore> chunkRef = blockInfo.getChunkRef();
        WorldChunk worldChunk = (WorldChunk)commandBuffer.getComponent(chunkRef, WorldChunk.getComponentType());
        assert worldChunk != null;
        int localX = ChunkUtil.xFromBlockInColumn(blockIndex);
        int localY = ChunkUtil.yFromBlockInColumn(blockIndex);
        int localZ = ChunkUtil.zFromBlockInColumn(blockIndex);
        int worldX = ChunkUtil.worldCoordFromLocalCoord(worldChunk.getX(), localX);
        int worldZ = ChunkUtil.worldCoordFromLocalCoord(worldChunk.getZ(), localZ);

        World world = worldChunk.getWorld();

        ArrayList<Vector3i> blockPositions = new ArrayList<>();
        Vector3i blockPos = new Vector3i(worldX, localY-1, worldZ);

        int sprinklerTier = block.getTier();
        if (sprinklerTier == 0) {
            blockPositions.add(new Vector3i(blockPos.getX() - 1, blockPos.getY(), blockPos.getZ()));
            blockPositions.add(new Vector3i(blockPos.getX() + 1, blockPos.getY(), blockPos.getZ()));
            blockPositions.add(new Vector3i(blockPos.getX(), blockPos.getY(), blockPos.getZ() - 1));
            blockPositions.add(new Vector3i(blockPos.getX(), blockPos.getY(), blockPos.getZ() + 1));
        } else {
            for (int i = -sprinklerTier; i <= sprinklerTier; i++) {
                for (int j = -sprinklerTier; j <= sprinklerTier; j++) {
                    blockPositions.add(new Vector3i(blockPos.getX() + i, blockPos.getY(), blockPos.getZ() + j));
                }
            }
        }

        WorldTimeResource worldTimeResource = world.getEntityStore().getStore().getResource(WorldTimeResource.getResourceType());


        world.execute(() -> {
            for (Vector3i pos : blockPositions) {
                WorldChunk chunk = world.getChunk(ChunkUtil.indexChunkFromBlock(pos.getX(), pos.getZ()));
                assert chunk != null;
                Ref<ChunkStore> tilledSoilRef = chunk.getBlockComponentEntity(pos.getX(), pos.getY(), pos.getZ());

                if (tilledSoilRef != null) {
                    Store<ChunkStore> chunkStore = world.getChunkStore().getStore();
                    TilledSoilBlock soil = chunkStore.getComponent(tilledSoilRef, TilledSoilBlock.getComponentType());
                    if (soil != null) {
                        Instant wateredUntil = worldTimeResource.getGameTime().plus(300, ChronoUnit.SECONDS);
                        soil.setWateredUntil(wateredUntil);
                        chunk.setTicking(pos.getX(), pos.getY(), pos.getZ(), true);
                        BlockChunk blockChunk = chunk.getBlockChunk();
                        if (blockChunk == null) continue;
                        BlockSection blockSection = blockChunk.getSectionAtBlockY(pos.getY());
                        if (blockSection == null) continue;
                        blockSection.scheduleTick(ChunkUtil.indexBlock(pos.getX(), pos.getY(), pos.getZ()), wateredUntil);
                    }
                }
            }
        });
    }

    @Nullable
    public Query getQuery() {
        return Query.and(SprinklerBlock.getComponentType());
    }
}