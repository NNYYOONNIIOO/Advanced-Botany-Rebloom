package ab.common.item.equipment;

import ab.AdvancedBotany;
import ab.common.lib.register.BlockListAB;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.mana.IManaUsingItem;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.common.Botania;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.item.ModItems;

public class ItemTerraHoe extends ItemHoe implements IManaUsingItem {

    public ItemTerraHoe() {
        super(BotaniaAPI.terrasteelToolMaterial);
        this.setCreativeTab(AdvancedBotany.tabAB);
        this.setTranslationKey(AdvancedBotany.modid + "." + "terraHoe");
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        if (!player.canPlayerEdit(pos.offset(facing), facing, stack)) {
            return EnumActionResult.FAIL;
        }
        UseHoeEvent event = new UseHoeEvent(player, stack, world, pos);
        if (MinecraftForge.EVENT_BUS.post(event)) {
            return EnumActionResult.FAIL;
        }
        if (event.getResult() == net.minecraftforge.fml.common.eventhandler.Event.Result.ALLOW) {
            stack.damageItem(1, (EntityLivingBase) player);
            return EnumActionResult.SUCCESS;
        }
        Block block = world.getBlockState(pos).getBlock();
        if (facing != EnumFacing.DOWN && world.isAirBlock(pos.up()) && (block == Blocks.GRASS || block == Blocks.DIRT)) {
            Block block1 = BlockListAB.blockTerraFarmland;
            world.playSound(null, pos, block1.getSoundType().getBreakSound(), SoundCategory.BLOCKS, (block1.getSoundType().getVolume() + 1.0f) / 2.0f, block1.getSoundType().getPitch() * 0.8f);
            if (world.isRemote) {
                float velMul = 0.025f;
                for (int i = 0; i < 48; ++i) {
                    double px = (Math.random() - 0.5) * 3.0;
                    double py = Math.random() - 0.5 + 1.0;
                    double pz = (Math.random() - 0.5) * 3.0;
                    Botania.proxy.wispFX(pos.getX() + 0.5 + px, pos.getY() + 0.5 + py, pos.getZ() + 0.5 + pz, 0.0f, 0.4f, 0.0f, (float) Math.random() * 0.15f + 0.15f, (float) (-px) * velMul, (float) (-py) * velMul, (float) (-pz) * velMul);
                }
                return EnumActionResult.SUCCESS;
            }
            world.setBlockState(pos, block1.getDefaultState());
            stack.damageItem(1, (EntityLivingBase) player);
            return EnumActionResult.SUCCESS;
        }
        if (player.isSneaking() && facing != EnumFacing.DOWN && world.isAirBlock(pos.up()) && block == ModBlocks.enchantedSoil) {
            if (world.isRemote) {
                return EnumActionResult.SUCCESS;
            }
            world.setBlockState(pos, Blocks.DIRT.getDefaultState());
            stack.damageItem(1, (EntityLivingBase) player);
            EntityItem entity = new EntityItem(world, pos.getX() + 0.5f, pos.getY() + 1, pos.getZ() + 0.5f, new ItemStack(ModItems.overgrowthSeed));
            world.spawnEntity(entity);
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.PASS;
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity player, int par4, boolean par5) {
        if (!world.isRemote && player instanceof EntityPlayer && stack.getMetadata() > 0 && ManaItemHandler.requestManaExactForTool(stack, (EntityPlayer) player, 1760, true)) {
            stack.setItemDamage(stack.getMetadata() - 1);
        }
    }

    @Override
    public boolean usesMana(ItemStack stack) {
        return true;
    }
}
