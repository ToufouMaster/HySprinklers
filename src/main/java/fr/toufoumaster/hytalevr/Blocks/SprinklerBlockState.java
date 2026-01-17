package fr.toufoumaster.hytalevr.Blocks;

import com.hypixel.hytale.builtin.adventure.farming.states.TilledSoilBlock;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
import com.hypixel.hytale.server.core.universe.world.chunk.state.TickableBlockState;
import com.hypixel.hytale.server.core.universe.world.meta.BlockState;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class SprinklerBlockState extends BlockState implements TickableBlockState {

    private int tick = 0;

    public static final Codec<SprinklerBlockState> CODEC = BuilderCodec.builder(SprinklerBlockState.class, SprinklerBlockState::new).append(
            new KeyedCodec<>("Tick", Codec.INTEGER), (state, o) -> state.tick = o, state -> state.tick
    ).add().build();

    public static int getTierById(String id) {
        return switch (id) {
            case "Copper_Sprinkler" -> 0;
            case "Iron_Sprinkler" -> 1;
            case "Thorium_Sprinkler" -> 2;
            case "Cobalt_Sprinkler" -> 3;
            case "Adamantite_Sprinkler" -> 4;
            case "Mithril_Sprinkler" -> 5;
            case "Onyxium_Sprinkler" -> 6;
            default -> 0;
        };
    }

    @Override
    public void tick(float var1, int var2, ArchetypeChunk<ChunkStore> archetypeChunk, Store<ChunkStore> store, CommandBuffer<ChunkStore> commandBuffer) {
        tick++;
        if (tick % 60 != 1) return; // once every 60 ticks
        ChunkStore entityStore = store.getExternalData();
        World world = entityStore.getWorld();


        BlockType blockType = this.getBlockType();
        if (blockType == null || blockType.getCustomModelAnimation() == null) return; // small hack to know if state is on

        WorldTimeResource worldTimeResource = world.getEntityStore().getStore().getResource(WorldTimeResource.getResourceType());
        int sprinklerTier = getTierById(blockType.getId());
        ArrayList<Vector3i> blockPositions = new ArrayList<>();
        Vector3i blockPos = new Vector3i(this.getBlockX(), this.getBlockY()-1, this.getBlockZ());
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

        for (Vector3i pos : blockPositions) {
            WorldChunk worldChunk = world.getChunk(ChunkUtil.indexChunkFromBlock(pos.getX(), pos.getZ()));
            assert worldChunk != null;
            Ref<ChunkStore> tilledSoilRef = worldChunk.getBlockComponentEntity(pos.getX(), pos.getY(), pos.getZ());

            if (tilledSoilRef != null) {
                Store<ChunkStore> chunkStore = world.getChunkStore().getStore();
                TilledSoilBlock soil = chunkStore.getComponent(tilledSoilRef, TilledSoilBlock.getComponentType());
                if (soil != null) {
                    Instant wateredUntil = worldTimeResource.getGameTime().plus(300, ChronoUnit.SECONDS);
                    soil.setWateredUntil(wateredUntil);
                    worldChunk.setTicking(pos.getX(), pos.getY(), pos.getZ(), true);
                    BlockChunk blockChunk = worldChunk.getBlockChunk();
                    if (blockChunk == null) continue;
                    BlockSection blockSection = blockChunk.getSectionAtBlockY(pos.getY());
                    if (blockSection == null) continue;
                    blockSection.scheduleTick(ChunkUtil.indexBlock(pos.getX(), pos.getY(), pos.getZ()), wateredUntil);
                }
            }
        }
    }
}