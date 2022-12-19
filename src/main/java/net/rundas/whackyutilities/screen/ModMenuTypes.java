package net.rundas.whackyutilities.screen;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.rundas.whackyutilities.WhackyUtilities;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.CONTAINERS, WhackyUtilities.MOD_ID);

    public static final RegistryObject<MenuType<CrucibleMenu>> CRUCIBLE_MENU =
            registerMenuType(CrucibleMenu::new, "crucible_menu");
    public static final RegistryObject<MenuType<PoweredCrucibleMenu>> POWERED_CRUCIBLE_MENU =
            registerMenuType(PoweredCrucibleMenu::new, "powered_crucible_menu");
    public static final RegistryObject<MenuType<AutoHammerMenu>> AUTO_HAMMER_MENU =
            registerMenuType(AutoHammerMenu::new, "auto_hammer_menu");

    private static <T extends AbstractContainerMenu>RegistryObject<MenuType<T>> registerMenuType(IContainerFactory<T> factory,
                                                                                                 String name) {
        return MENUS.register(name, () -> IForgeMenuType.create(factory));
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}