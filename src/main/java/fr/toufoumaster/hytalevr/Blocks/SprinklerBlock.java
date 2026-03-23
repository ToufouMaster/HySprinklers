package fr.toufoumaster.hytalevr.Blocks;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import fr.toufoumaster.hytalevr.HySprinklers;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SprinklerBlock  implements Component<ChunkStore> {

    private int tier;
    public static final BuilderCodec<SprinklerBlock> CODEC = BuilderCodec.builder(SprinklerBlock.class, SprinklerBlock::new)
            .append(new KeyedCodec<>("Tier", Codec.INTEGER), (data, value) -> data.tier = value, data -> data.tier).add()
            .build();

    public SprinklerBlock() {
        this.tier = 0;
    }

    public SprinklerBlock(SprinklerBlock block) {
        this.tier = block.tier;
    }

    public static ComponentType getComponentType() {
        return HySprinklers.get().getSprinklerBlockComponentType();
    }

    @Nullable
    public Component<ChunkStore> clone() {
        return new SprinklerBlock(this);
    }

    public int getTier() {
        return tier;
    }

    @Nonnull
    public String toString() {
        return "SprinklerBlock{Tier=" + this.getTier() + "}";
    }
}