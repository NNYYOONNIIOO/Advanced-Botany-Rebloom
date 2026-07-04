package ab.common.lib.register;

import ab.common.block.subtile.ABSubTileSignature;
import ab.common.block.subtile.SubTileAncientAlphirine;
import ab.common.block.subtile.SubTileArdentAzarcissus;
import ab.common.block.subtile.SubTileDictarius;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.subtile.SubTileEntity;
import vazkii.botania.api.subtile.signature.SubTileSignature;

public class FlowerRegister {
    public static void init() {
        FlowerRegister.registerFlower(SubTileAncientAlphirine.class, "ancientAlphirine");
        FlowerRegister.registerFlower(SubTileDictarius.class, "dictarius");
        FlowerRegister.registerFlower(SubTileArdentAzarcissus.class, "ardentAzarcissus");
    }

    static void registerFlower(Class<? extends SubTileEntity> subTile, String name) {
        BotaniaAPI.registerSubTile(name, subTile);
        BotaniaAPI.registerSubTileSignature(subTile, new ABSubTileSignature(name));
        BotaniaAPI.addSubTileToCreativeMenu(name);
    }
}
