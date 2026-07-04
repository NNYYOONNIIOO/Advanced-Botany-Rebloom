package ab.common.block;

import ab.AdvancedBotany;
import ab.common.block.tile.TileBoardFate;
import ab.common.block.tile.TileGameBoard;
import ab.common.block.tile.TileInventory;
import ab.common.lib.register.AchievementRegister;
import ab.common.lib.register.RecipeListAB;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import vazkii.botania.api.lexicon.ILexiconable;
import vazkii.botania.api.lexicon.LexiconEntry;

import javax.annotation.Nonnull;

public class BlockBoardFate extends BlockContainer implements ILexiconable {

    public static final PropertyInteger VARIANT = PropertyInteger.create("variant", 0, 1);
    protected static final AxisAlignedBB BOARD_AABB = new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.1875, 1.0);

    public BlockBoardFate() {
        super(Material.IRON);
        this.setCreativeTab(AdvancedBotany.tabAB);
        this.setHardness(3.0f);
        this.setResistance(10.0f);
        this.setSoundType(SoundType.METAL);
        this.setTranslationKey(AdvancedBotany.modid + "." + "boardFate");
        this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, 0));
    }

    @Override
    @Nonnull
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return BOARD_AABB;
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        int meta = state.getBlock().getMetaFromState(state);
        drops.add(new ItemStack(this, 1, meta));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, VARIANT);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(VARIANT);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(VARIANT, meta);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public int damageDropped(IBlockState state) {
        return this.getMetaFromState(state);
    }

    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
        list.add(new ItemStack(this, 1, 0));
        list.add(new ItemStack(this, 1, 1));
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        int meta = state.getBlock().getMetaFromState(state);
        if (meta == 0) {
            TileEntity te = world.getTileEntity(pos);
            if (!(te instanceof TileGameBoard)) return false;
            TileGameBoard tile = (TileGameBoard) te;
            if (player.isSneaking() && !tile.hasGame()) {
                tile.isSingleGame = !tile.isSingleGame;
                if (!world.isRemote) {
                    world.playSound(null, player.posX, player.posY, player.posZ,
                            net.minecraft.util.SoundEvent.REGISTRY.getObject(new net.minecraft.util.ResourceLocation("botania", "ding")),
                            net.minecraft.util.SoundCategory.PLAYERS, 0.11f, 0.8f);
                }
                return true;
            }
            if (!tile.hasGame()) {
                tile.setPlayer(player);
            } else if (!tile.isSingleGame && tile.playersName[1].isEmpty() && !tile.playersName[0].equals(player.getName())) {
                tile.setPlayer(player);
            } else {
                return tile.dropDice(player);
            }
            return false;
        }

        // meta == 1: BoardFate
        if (!player.isSneaking()) {
            ItemStack heldItem = player.getHeldItem(hand);
            TileEntity te = world.getTileEntity(pos);
            if (!(te instanceof TileBoardFate)) return false;
            TileBoardFate tile = (TileBoardFate) te;
            if (!heldItem.isEmpty() && tile != null) {
                if (TileBoardFate.isDice(heldItem)) {
                    for (int i = 0; i < tile.getSizeInventory(); i++) {
                        ItemStack slotStack = tile.getStackInSlot(i);
                        if (!slotStack.isEmpty()) continue;
                        heldItem.shrink(1);
                        if (heldItem.getCount() == 0) {
                            player.inventory.setInventorySlotContents(player.inventory.currentItem, ItemStack.EMPTY);
                        }
                        if (!world.isRemote) {
                            ItemStack copy = heldItem.copy();
                            copy.setCount(1);
                            tile.setInventorySlotContents(i, copy);
                            tile.slotChance[i] = (byte) (world.rand.nextInt(6) + 1);
                            tile.requestUpdate = true;
                            world.playSound(null, pos.getX(), pos.getY(), pos.getZ(),
                                    net.minecraft.util.SoundEvent.REGISTRY.getObject(new net.minecraft.util.ResourceLocation("ab", "boardCube")),
                                    net.minecraft.util.SoundCategory.BLOCKS, 0.6f, 1.0f);
                        }
                        return true;
                    }
                }
            }
            return false;
        }

        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof TileBoardFate)) return false;
        return ((TileBoardFate) te).spawnRelic(player);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return meta == 0 ? new TileGameBoard() : new TileBoardFate();
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        int meta = state.getBlock().getMetaFromState(state);
        if (!world.isRemote && meta == 1) {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileInventory) {
                TileInventory inv = (TileInventory) te;
                for (int i = 0; i < inv.getSizeInventory(); i++) {
                    ItemStack stack = inv.getStackInSlot(i);
                    if (stack.isEmpty()) continue;
                    float f = world.rand.nextFloat() * 0.8f + 0.1f;
                    float f1 = world.rand.nextFloat() * 0.8f + 0.1f;
                    float f2 = world.rand.nextFloat() * 0.8f + 0.1f;
                    EntityItem entityitem = new EntityItem(world,
                            pos.getX() + f, pos.getY() + f1, pos.getZ() + f2,
                            stack.copy());
                    float f3 = 0.05f;
                    entityitem.motionX = world.rand.nextGaussian() * f3;
                    entityitem.motionY = world.rand.nextGaussian() * f3 + 0.2f;
                    entityitem.motionZ = world.rand.nextGaussian() * f3;
                    if (stack.hasTagCompound()) {
                        entityitem.getItem().setTagCompound(stack.getTagCompound().copy());
                    }
                    world.spawnEntity(entityitem);
                }
            }
        }
        super.breakBlock(world, pos, state);
    }

    @Override
    public LexiconEntry getEntry(World world, BlockPos pos, EntityPlayer player, ItemStack stack) {
        IBlockState state = world.getBlockState(pos);
        int meta = state.getBlock().getMetaFromState(state);
        return meta == 0 ? RecipeListAB.gameBoard : RecipeListAB.fateBoard;
    }
}
