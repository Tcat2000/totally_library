package org.tcathebluecreper.totally_lib;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.tcathebluecreper.totally_lib.multiblock.ModMultiblocks;

@Mod(TotallyLibrary.MODID)
public class TotallyLibrary {
    public static final String MODID = "totally_lib";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final RegistrationManager regManager = new RegistrationManager(FMLJavaModLoadingContext.get().getModEventBus());

    public TotallyLibrary() {
        ModMultiblocks.init();
    }
}
