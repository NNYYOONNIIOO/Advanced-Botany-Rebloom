package ab.common.block;

import ab.AdvancedBotany;
import ab.common.block.tile.TileManaCrystalCube;
import ab.common.lib.register.BlockListAB;
import ab.common.lib.register.RecipeListAB;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import vazkii.botania.api.lexicon.ILexiconable;
import vazkii.botania.api.lexicon.LexiconEntry;
import vazkii.botania.common.item.ItemTwigWand;

import javax.annotation.Nonnull;

public class BlockManaCrystalCube extends BlockContainer implements ILexiconable {

    public static final PropertyBool STATIC = PropertyBool.create("static");

    private static final AxisAlignedBB CUBE_AABB = new AxisAlignedBB(0.1875, 0.0, 0.1875, 0.8125, 1.0, 0.8125);

    public BlockManaCrystalCube() {
        super(Material.IRON);
        this.setCreativeTab(AdvancedBotany.tabAB);
        this.setHardness(5.5f);
        this.setSoundType(SoundType.METAL);
        this.setTranslationKey(AdvancedBotany.modid + "." + "ABManaCrystalCube");
        this.setDefaultState(this.blockState.getBaseState().withProperty(STATIC, true));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, STATIC);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(STATIC) ? 0 : 1;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(STATIC, meta == 0);
    }

    @Override
    public int damageDropped(IBlockState state) {
        return 0;
    }

    @Override
    @Nonnull
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return CUBE_AABB;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        boolean isWand = !player.getHeldItemMainhand().isEmpty() && player.getHeldItemMainhand().getItem() instanceof ItemTwigWand;
        if (isWand) {
            return false;
        }
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileManaCrystalCube && !world.isRemote) {
            TileManaCrystalCube tile = (TileManaCrystalCube) te;
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            tile.writePacketNBT(nbttagcompound);
            int[] mana = tile.getManaAround();
            nbttagcompound.setInteger("knownMana", mana[0]);
            nbttagcompound.setInteger("knownMaxMana", mana[1]);
            if (player instanceof EntityPlayerMP) {
                ((EntityPlayerMP) player).connection.sendPacket(new SPacketUpdateTileEntity(tile.getPos(), tile.getBlockMetadata(), nbttagcompound));
            }
        }
        world.playSound(null, player.posX, player.posY, player.posZ,
                net.minecraft.util.SoundEvent.REGISTRY.getObject(new net.minecraft.util.ResourceLocation("botania", "ding")),
                net.minecraft.util.SoundCategory.PLAYERS, 0.11f, 1.0f);
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileManaCrystalCube();
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        return BlockListAB.blockManaCrystalCubeRI;
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
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public LexiconEntry getEntry(World world, BlockPos pos, EntityPlayer player, ItemStack lexicon) {
        return RecipeListAB.manaCrystalCube;
    }
}
