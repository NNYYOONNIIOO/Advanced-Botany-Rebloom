package ab.common.lib.register;

import ab.common.block.subtile.ABSubTileSignature;
import ab.common.block.subtile.SubTileAncientAlphirine;
import ab.common.block.subtile.SubTileArdentAzarcissus;
import ab.common.block.subtile.SubTileDictarius;
import ab.common.integration.thaumcraft.SubTileAspecolus;
import ab.common.integration.thaumcraft.SubTilePureGladiolus;
import net.minecraftforge.fml.common.Loader;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.subtile.SubTileEntity;
import vazkii.botania.api.subtile.signature.SubTileSignature;

public class FlowerRegister {
    public static void init() {
        FlowerRegister.registerFlower(SubTileAncientAlphirine.class, "ancientAlphirine");
        FlowerRegister.registerFlower(SubTileDictarius.class, "dictarius");
        FlowerRegister.registerFlower(SubTileArdentAzarcissus.class, "ardentAzarcissus");
        if (Loader.isModLoaded("thaumcraft")) {
            FlowerRegister.registerFlower(SubTileAspecolus.class, "aspecolus");
            FlowerRegister.registerFlower(SubTilePureGladiolus.class, "pureGladiolus");
        }
    }

    static void registerFlower(Class<? extends SubTileEntity> subTile, String name) {
        BotaniaAPI.registerSubTile(name, subTile);
        BotaniaAPI.registerSubTileSignature(subTile, new ABSubTileSignature(name));
        BotaniaAPI.addSubTileToCreativeMenu(name);
    }
}
