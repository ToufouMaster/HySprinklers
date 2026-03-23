package fr.toufoumaster.hytalevr;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import fr.toufoumaster.hytalevr.Blocks.SprinklerBlock;
import fr.toufoumaster.hytalevr.Blocks.SprinklerInitializer;
import fr.toufoumaster.hytalevr.Blocks.SprinklerSystem;

import javax.annotation.Nonnull;

public class HySprinklers extends JavaPlugin {

    private static HySprinklers instance;
    private ComponentType sprinklerBlockComponentType;

    public HySprinklers(@Nonnull JavaPluginInit init) {
        super(init);
        instance = this;
    }

    public static HySprinklers get() {
        return instance;
    }

    @Override
    protected void setup() {
        super.setup();

        this.sprinklerBlockComponentType = this.getChunkStoreRegistry().registerComponent(SprinklerBlock.class, "SprinklerBlock", SprinklerBlock.CODEC);
    }

    @Override
    protected void start() {
        this.getChunkStoreRegistry().registerSystem(new SprinklerSystem());
        this.getChunkStoreRegistry().registerSystem(new SprinklerInitializer());
    }

    public ComponentType getSprinklerBlockComponentType() {
        return this.sprinklerBlockComponentType;
    }
}