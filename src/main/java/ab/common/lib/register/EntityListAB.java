package ab.common.lib.register;

import ab.AdvancedBotany;
import ab.common.entity.EntityAdvancedSpark;
import ab.common.entity.EntityAlphirinePortal;
import ab.common.entity.EntityManaVine;
import ab.common.entity.EntityNebulaBlaze;
import ab.common.entity.EntitySeed;
import ab.common.entity.EntitySword;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class EntityListAB {
    public static void init() {
        int id = 0;
        EntityRegistry.registerModEntity(new ResourceLocation("advanced_botany", "advancedSpark"), EntityAdvancedSpark.class, "advancedSpark", id++, AdvancedBotany.instance, 64, 10, false);
        EntityRegistry.registerModEntity(new ResourceLocation("advanced_botany", "nebulaBlaze"), EntityNebulaBlaze.class, "nebulaBlaze", id++, AdvancedBotany.instance, 64, 10, true);
        EntityRegistry.registerModEntity(new ResourceLocation("advanced_botany", "manaVineBall"), EntityManaVine.class, "manaVineBall", id++, AdvancedBotany.instance, 64, 10, true);
        EntityRegistry.registerModEntity(new ResourceLocation("advanced_botany", "alphirinePortal"), EntityAlphirinePortal.class, "alphirinePortal", id++, AdvancedBotany.instance, 64, 10, false);
        EntityRegistry.registerModEntity(new ResourceLocation("advanced_botany", "entitySword"), EntitySword.class, "entitySword", id++, AdvancedBotany.instance, 64, 10, true);
        EntityRegistry.registerModEntity(new ResourceLocation("advanced_botany", "entitySeed"), EntitySeed.class, "entitySeed", id++, AdvancedBotany.instance, 64, 10, true);
    }
}
