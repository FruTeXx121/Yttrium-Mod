package com.yttrium;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod("yttrium") // literal string avoids IDE warning
public class YttriumMod {
    public static final String MODID = "yttrium"; // still available for logging and references
    public static final Logger LOGGER = LogUtils.getLogger();

    public YttriumMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);

        // Register config spec
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Yttrium Reforged: common setup initialized");
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        LOGGER.info("Yttrium Reforged: client setup initialized");
        try {
            RenderTransformGPU.init();
            PhysicsGPU.init();
            EntityTransformGPU.init();
            LightingGPU.init();
        } catch (Exception e) {
            LOGGER.error("Failed to initialize GPU shaders", e);
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Yttrium Reforged: server starting");
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        try {
            if (MatrixGPU.isSupported()) {
                MatrixGPU.runAll();
            } else {
                LOGGER.warn("OpenGL 4.3 not supported — falling back to CPU.");
                MatrixGPU.runFallback();
            }
        } catch (Exception e) {
            LOGGER.error("Yttrium GPU execution failed", e);
        }
    }
}
