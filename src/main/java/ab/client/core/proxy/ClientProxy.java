package ab.client.core.proxy;

import ab.client.core.handler.BlackHaloRenderHandler;
import ab.client.core.handler.BoundRenderHandler;
import ab.client.core.handler.ClientHandler;
import ab.client.core.handler.HudRenderHandler;
import ab.client.core.handler.InventoryToggleHandler;
import ab.client.core.handler.ModelRegisterHandler;
import ab.client.core.handler.PlayerRendererHandler;
import ab.client.core.handler.PocketWardrobeRenderHandler;
import ab.client.core.handler.TalismanChestRenderHandler;
import ab.client.render.entity.EntityNullRender;
import ab.client.render.entity.RenderEntityAdvancedSpark;
import ab.client.render.entity.RenderEntityAlphirinePortal;
import ab.client.render.item.RenderItemTESRBlock;
import ab.client.render.tile.RenderTileABSpreader;
import ab.client.render.tile.RenderTileBoardFate;
import ab.client.render.tile.RenderTileEngineerHopper;
import ab.client.render.tile.RenderTileGameBoard;
import ab.client.render.tile.RenderTileManaCharger;
import ab.client.render.tile.RenderTileManaContainer;
import ab.client.render.tile.RenderTileManaCrystalCube;
import ab.client.render.tile.RenderTileNidavellirForge;
import ab.common.block.tile.TileABSpreader;
import ab.common.block.tile.TileBoardFate;
import ab.common.block.tile.TileEngineerHopper;
import ab.common.block.tile.TileGameBoard;
import ab.common.block.tile.TileManaCharger;
import ab.common.block.tile.TileManaContainer;
import ab.common.block.tile.TileManaCrystalCube;
import ab.common.block.tile.TileNidavellirForge;
import ab.common.core.handler.ConfigABHandler;
import ab.common.core.proxy.CommonProxy;
import ab.common.entity.EntityAdvancedSpark;
import ab.common.entity.EntityAlphirinePortal;
import ab.common.entity.EntityManaVine;
import ab.common.entity.EntityNebulaBlaze;
import ab.common.entity.EntitySeed;
import ab.common.entity.EntitySword;
import ab.common.block.subtile.SubTileAncientAlphirine;
import ab.common.block.subtile.SubTileArdentAzarcissus;
import ab.common.block.subtile.SubTileDictarius;
import ab.common.lib.register.BlockListAB;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import vazkii.botania.api.BotaniaAPIClient;

public class ClientProxy extends CommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        BotaniaAPIClient.registerSubtileModel(SubTileAncientAlphirine.class,
                new ModelResourceLocation("advanced_botany:ancientalphirine", "normal"),
                new ModelResourceLocation("advanced_botany:ancientalphirine", "inventory"));
        BotaniaAPIClient.registerSubtileModel(SubTileDictarius.class,
                new ModelResourceLocation("advanced_botany:dictarius", "normal"),
                new ModelResourceLocation("advanced_botany:dictarius", "inventory"));
        BotaniaAPIClient.registerSubtileModel(SubTileArdentAzarcissus.class,
                new ModelResourceLocation("advanced_botany:ardentazarcissus", "normal"),
                new ModelResourceLocation("advanced_botany:ardentazarcissus", "inventory"));

        // Register ClientHandler in preInit so TextureStitchEvent.Pre is caught
        ClientHandler clientHandler = new ClientHandler();
        FMLCommonHandler.instance().bus().register(clientHandler);
        MinecraftForge.EVENT_BUS.register(clientHandler);

        // Entity renderers must be registered in preInit, before RenderManager loads them
        RenderingRegistry.registerEntityRenderingHandler(EntityAdvancedSpark.class, new RenderEntityAdvancedSpark.Factory());
        RenderingRegistry.registerEntityRenderingHandler(EntityNebulaBlaze.class, new EntityNullRender.Factory());
        RenderingRegistry.registerEntityRenderingHandler(EntityManaVine.class, new EntityNullRender.Factory());
        RenderingRegistry.registerEntityRenderingHandler(EntityAlphirinePortal.class, new RenderEntityAlphirinePortal.Factory());
        RenderingRegistry.registerEntityRenderingHandler(EntitySword.class, new EntityNullRender.Factory());
        RenderingRegistry.registerEntityRenderingHandler(EntitySeed.class, new EntityNullRender.Factory());
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
        MinecraftForge.EVENT_BUS.register(new HudRenderHandler());
        MinecraftForge.EVENT_BUS.register(new PlayerRendererHandler());
        MinecraftForge.EVENT_BUS.register(new BoundRenderHandler());
        MinecraftForge.EVENT_BUS.register(new TalismanChestRenderHandler());
        MinecraftForge.EVENT_BUS.register(new PocketWardrobeRenderHandler());
        MinecraftForge.EVENT_BUS.register(new BlackHaloRenderHandler());
        MinecraftForge.EVENT_BUS.register(new InventoryToggleHandler());
        ClientRegistry.bindTileEntitySpecialRenderer(TileNidavellirForge.class, new RenderTileNidavellirForge());
        ClientRegistry.bindTileEntitySpecialRenderer(TileABSpreader.class, new RenderTileABSpreader());
        ClientRegistry.bindTileEntitySpecialRenderer(TileManaContainer.class, new RenderTileManaContainer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileManaCrystalCube.class, new RenderTileManaCrystalCube());
        if (ConfigABHandler.hasManaCharger) {
            ClientRegistry.bindTileEntitySpecialRenderer(TileManaCharger.class, new RenderTileManaCharger());
        }
        ClientRegistry.bindTileEntitySpecialRenderer(TileEngineerHopper.class, new RenderTileEngineerHopper());
        ClientRegistry.bindTileEntitySpecialRenderer(TileBoardFate.class, new RenderTileBoardFate());
        ClientRegistry.bindTileEntitySpecialRenderer(TileGameBoard.class, new RenderTileGameBoard());

        // Set TEISR for TESR blocks so builtin/entity item models render correctly
        RenderItemTESRBlock tesrRenderer = new RenderItemTESRBlock();
        Item.getItemFromBlock(BlockListAB.blockABSpreader).setTileEntityItemStackRenderer(tesrRenderer);
        Item.getItemFromBlock(BlockListAB.blockABPlate).setTileEntityItemStackRenderer(tesrRenderer);
        Item.getItemFromBlock(BlockListAB.blockManaContainer).setTileEntityItemStackRenderer(tesrRenderer);
        Item.getItemFromBlock(BlockListAB.blockEngineerHopper).setTileEntityItemStackRenderer(tesrRenderer);
        if (ConfigABHandler.hasManaCharger) {
            Item.getItemFromBlock(BlockListAB.blockManaCharger).setTileEntityItemStackRenderer(tesrRenderer);
        }
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
    }
}
