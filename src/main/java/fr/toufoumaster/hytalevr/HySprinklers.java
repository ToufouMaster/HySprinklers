package fr.toufoumaster.hytalevr;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import fr.toufoumaster.hytalevr.Blocks.SprinklerBlockState;

import javax.annotation.Nonnull;

public class HySprinklers extends JavaPlugin {

    public static HySprinklers instance;


    public HySprinklers(@Nonnull JavaPluginInit init) {
        super(init);
        instance = this;
    }

    @Override
    protected void setup() {
        super.setup();

        this.getBlockStateRegistry().registerBlockState(SprinklerBlockState.class, "Copper_Sprinkler", SprinklerBlockState.CODEC);
        this.getBlockStateRegistry().registerBlockState(SprinklerBlockState.class, "Iron_Sprinkler", SprinklerBlockState.CODEC);
        this.getBlockStateRegistry().registerBlockState(SprinklerBlockState.class, "Thorium_Sprinkler", SprinklerBlockState.CODEC);
        this.getBlockStateRegistry().registerBlockState(SprinklerBlockState.class, "Cobalt_Sprinkler", SprinklerBlockState.CODEC);
        this.getBlockStateRegistry().registerBlockState(SprinklerBlockState.class, "Adamantite_Sprinkler", SprinklerBlockState.CODEC);
        this.getBlockStateRegistry().registerBlockState(SprinklerBlockState.class, "Mithril_Sprinkler", SprinklerBlockState.CODEC);
        this.getBlockStateRegistry().registerBlockState(SprinklerBlockState.class, "Onyxium_Sprinkler", SprinklerBlockState.CODEC);
    }

}