package ab.common.lib.register;

import ab.common.block.BlockABSpreader;
import ab.common.block.BlockABStorage;
import ab.common.block.BlockBoardFate;
import ab.common.block.BlockEngineerHopper;
import ab.common.block.BlockFreyrLiana;
import ab.common.block.BlockLebethronWood;
import ab.common.block.BlockLuminousFreyrLiana;
import ab.common.block.BlockManaCharger;
import ab.common.block.BlockManaContainer;
import ab.common.block.BlockManaCrystalCube;
import ab.common.block.BlockNidavellirForge;
import ab.common.block.BlockTerraFarmland;
import ab.common.block.tile.TileABSpreader;
import ab.common.block.tile.TileBoardFate;
import ab.common.block.tile.TileEngineerHopper;
import ab.common.block.tile.TileGameBoard;
import ab.common.block.tile.TileLebethronCore;
import ab.common.block.tile.TileManaCharger;
import ab.common.block.tile.TileManaContainer;
import ab.common.block.tile.TileManaCrystalCube;
import ab.common.block.tile.TileNidavellirForge;
import ab.AdvancedBotany;
import ab.common.core.handler.ConfigABHandler;
import ab.common.item.block.ItemBlockBase;
import ab.common.item.block.ItemBlockBoard;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BlockListAB {
    public static Block blockABSpreader;
    public static Block blockABPlate;
    public static Block blockLebethron;
    public static Block blockManaContainer;
    public static Block blockABStorage;
    public static Block blockManaCrystalCube;
    public static Block blockTerraFarmland;
    public static Block blockManaCharger;
    public static Block blockEngineerHopper;
    public static Block blockFreyrLiana;
    public static Block blockLuminousFreyrLiana;
    public static Block blockBoardFate;
    public static int blockABSpreaderRI = -1;
    public static int blockManaContainerRI = -1;
    public static int blockManaCrystalCubeRI = -1;
    public static int blockManaChargerRI = -1;
    public static int blockEngineerHopperRI = -1;
    public static int blockABPlateRI = -1;

    public static void init() {
        BlockListAB.initializeBlocks();
        BlockListAB.registerBlocks();
        BlockListAB.registerTileEntities();
    }

    private static void initializeBlocks() {
        blockABSpreader = new BlockABSpreader();
        blockABPlate = new BlockNidavellirForge();
        blockLebethron = new BlockLebethronWood();
        blockManaContainer = new BlockManaContainer();
        blockABStorage = new BlockABStorage();
        blockManaCrystalCube = new BlockManaCrystalCube();
        blockTerraFarmland = new BlockTerraFarmland();
        blockManaCharger = new BlockManaCharger();
        blockEngineerHopper = new BlockEngineerHopper();
        blockLuminousFreyrLiana = new BlockLuminousFreyrLiana();
        blockFreyrLiana = new BlockFreyrLiana();
        blockBoardFate = new BlockBoardFate();
    }

    private static void registerBlocks() {
        registerBlock(blockABSpreader, "blockABSpreader");
        registerBlock(blockABPlate, "blockABPlate");
        registerBlock(blockManaContainer, new ItemBlockBase(blockManaContainer), "blockManaContainer");
        registerBlock(blockLebethron, new ItemBlockBase(blockLebethron), "blockLebethron");
        registerBlock(blockABStorage, new ItemBlockBase(blockABStorage), "blockABStorage");
        registerBlock(blockManaCrystalCube, "blockManaCrystalCube");
        registerBlock(blockTerraFarmland, "blockTerraFarmland");
        registerBlock(blockManaCharger, "blockManaCharger");
        registerBlock(blockEngineerHopper, "blockEngineerHopper");
        registerBlock(blockFreyrLiana, "blockFreyrLiana");
        registerBlock(blockLuminousFreyrLiana, "blockLuminousFreyrLiana");
        registerBlock(blockBoardFate, new ItemBlockBoard(blockBoardFate), "blockBoardFate");
    }

    private static void registerBlock(Block block, String name) {
        registerBlock(block, new ItemBlock(block), name);
    }

    private static void registerBlock(Block block, ItemBlock item, String name) {
        if (block.getRegistryName() == null) {
            block.setRegistryName(name);
        }
        ForgeRegistries.BLOCKS.register(block);
        if (item.getRegistryName() == null) {
            item.setRegistryName(name);
        }
        ForgeRegistries.ITEMS.register(item);
    }

    private static void registerTileEntities() {
        String modid = AdvancedBotany.modid;
        GameRegistry.registerTileEntity(TileABSpreader.class, new ResourceLocation(modid, "tileABSpreader"));
        GameRegistry.registerTileEntity(TileNidavellirForge.class, new ResourceLocation(modid, "tileAgglomerationPlate"));
        GameRegistry.registerTileEntity(TileLebethronCore.class, new ResourceLocation(modid, "tileLebethronCore"));
        GameRegistry.registerTileEntity(TileManaContainer.class, new ResourceLocation(modid, "tileManaContainer"));
        GameRegistry.registerTileEntity(TileManaCrystalCube.class, new ResourceLocation(modid, "tileManaCrystalCube"));
        GameRegistry.registerTileEntity(TileEngineerHopper.class, new ResourceLocation(modid, "tileEngineerHopper"));
        if (ConfigABHandler.hasManaCharger) {
            GameRegistry.registerTileEntity(TileManaCharger.class, new ResourceLocation(modid, "tileManaCharger"));
        }
        GameRegistry.registerTileEntity(TileBoardFate.class, new ResourceLocation(modid, "tileBoardFate"));
        GameRegistry.registerTileEntity(TileGameBoard.class, new ResourceLocation(modid, "tileGameBoard"));
    }
}
