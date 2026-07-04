package ab.common.item.relic;

import ab.client.core.handler.ItemsRemainingRender;
import ab.common.core.handler.ConfigABHandler;
import ab.common.core.handler.NetworkHandler;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.common.Botania;
import vazkii.botania.common.core.helper.ItemNBTHelper;

import java.awt.*;
import java.util.List;

public class ItemSphereNavigation extends ItemModRelic {

    public static final int rangeSearch = 16;
    public static final int maxCooldown = 158;

    public ItemSphereNavigation() {
        super("sphereNavigation");
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return false;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        Block block = getFindBlock(stack);
        int meta = getFindMeta(stack);
        ItemStack rStack = new ItemStack(block, 1, meta);
        return super.getItemStackDisplayName(stack) + (rStack.isEmpty() || rStack.getItem() == null ? "" : TextFormatting.RESET + " (" + TextFormatting.GREEN + rStack.getDisplayName() + TextFormatting.RESET + ")");
    }

    @Override
    public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flag) {
        super.addInformation(stack, world, list, flag);
        addStringToTooltip(I18n.format(stack.getMetadata() == 0 ? "botaniamisc.active" : "botaniamisc.inactive"), list);
    }

    private void addStringToTooltip(String s, List<String> tooltip) {
        tooltip.add(s.replaceAll("&", "\u00a7"));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (getFindBlock(stack) != null && player.isSneaking()) {
            int dmg = stack.getMetadata();
            stack.setItemDamage(~dmg & 1);
            world.playSound(null, player.posX, player.posY, player.posZ, net.minecraft.init.SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, net.minecraft.util.SoundCategory.PLAYERS, 0.3f, 0.1f);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int pos, boolean equipped) {
        super.onUpdate(stack, world, entity, pos, equipped);
        if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            if (!world.isRemote && stack.getMetadata() == 0 && getFindBlock(stack) != null && canWork(stack) && ManaItemHandler.requestManaExactForTool(stack, player, ConfigABHandler.sphereNavigationManaCost, true)) {
                setMaxTick(stack);
                NetworkHandler.sendPacketToFindBlocks((EntityPlayerMP) player, getFindBlock(stack), getFindMeta(stack));
            }
        }
    }

    public static void findBlocks(World world, Block findBlock, int findMeta, EntityPlayer player) {
        if (world.isRemote) {
            ItemStack renderStack = null;
            int maxFindedBlocks = 32;
            int findedBlocks = 0;
            block0:
            for (int y = -32; y < 16; ++y) {
                for (int x = -16; x < 16; ++x) {
                    if (world.rand.nextInt(maxFindedBlocks) >= maxFindedBlocks - findedBlocks || world.rand.nextBoolean()) continue;
                    for (int z = -16; z < 16; ++z) {
                        if (world.rand.nextInt(maxFindedBlocks) >= maxFindedBlocks - findedBlocks || world.rand.nextBoolean()) continue;
                        if (findedBlocks >= maxFindedBlocks) break block0;
                        int posX = MathHelper.floor(player.posX) + x;
                        int posY = MathHelper.floor(player.posY) + y;
                        int posZ = MathHelper.floor(player.posZ) + z;
                        BlockPos pos = new BlockPos(posX, posY, posZ);
                        if (posY < 0) continue;
                        net.minecraft.block.state.IBlockState state = world.getBlockState(pos);
                        Block block = state.getBlock();
                        if (block != findBlock) continue;
                        int meta = block.getMetaFromState(state);
                        if (meta != findMeta) continue;
                        if (renderStack == null) {
                            renderStack = getRenderStackForBlock(world, block, meta, posX, posY, posZ);
                        }
                        ++findedBlocks;
                        float maxAge = 2.7f + 0.5f * (float) Math.random();
                        Botania.proxy.setWispFXDepthTest(false);
                        Botania.proxy.setWispFXDistanceLimit(false);
                        float far = 120.0f - (float) (Math.abs(x) + Math.min(16, Math.abs(y)) + Math.abs(z)) / 64.0f * 120.0f;
                        if (far <= 70.0f) {
                            far *= 0.1f;
                        }
                        Color color = new Color(Color.HSBtoRGB(far / 360.0f, 0.9f + (float) (Math.random() * 0.1), 1.0f));
                        for (int i = 0; i < 11; ++i) {
                            Botania.proxy.wispFX((float) posX + 0.5f + (Math.random() - 0.5), (float) posY + 0.5f + (Math.random() - 0.5), (float) posZ + 0.5f + (Math.random() - 0.5), (float) color.getRed() / 100.0f, (float) color.getGreen() / 100.0f, (float) color.getBlue() / 100.0f, 0.3f + (float) (Math.random() * 0.25), 0.0f, maxAge);
                        }
                        Botania.proxy.setWispFXDistanceLimit(true);
                        Botania.proxy.setWispFXDepthTest(true);
                    }
                }
            }
            if (renderStack != null) {
                ItemsRemainingRender.set(renderStack, I18n.format("ab.sphereNavigation.founded") + " " + findedBlocks);
            }
        }
    }

    public static ItemStack getRenderStackForBlock(World world, Block block, int meta, int x, int y, int z) {
        Item item;
        if (block != null && (item = Item.getItemFromBlock(block)) != null) {
            return new ItemStack(block, 1, item.getHasSubtypes() ? meta : 0);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        if (player.isSneaking()) {
            net.minecraft.block.state.IBlockState state = world.getBlockState(pos);
            Block block = state.getBlock();
            int meta = block.getMetaFromState(state);
            ItemStack findStack = getRenderStackForBlock(world, block, meta, pos.getX(), pos.getY(), pos.getZ());
            if (!findStack.isEmpty()) {
                setFindBlock(stack, block, meta);
                if (world.isRemote) {
                    ItemsRemainingRender.set(findStack, findStack.getDisplayName());
                }
                return EnumActionResult.SUCCESS;
            }
        }
        return EnumActionResult.PASS;
    }

    public boolean canWork(ItemStack stack) {
        int tick = ItemNBTHelper.getInt(stack, "cooldown", 0);
        if (tick == 0) {
            return true;
        }
        if (tick > 0) {
            ItemNBTHelper.setInt(stack, "cooldown", tick - 1);
        }
        return false;
    }

    public void setMaxTick(ItemStack stack) {
        ItemNBTHelper.setInt(stack, "cooldown", 158);
    }

    public static void setFindBlock(ItemStack stack, Block block, int meta) {
        ItemNBTHelper.setInt(stack, "findBlockID", Block.getIdFromBlock(block));
        ItemNBTHelper.setInt(stack, "findBlockMeta", meta);
    }

    public static Block getFindBlock(ItemStack stack) {
        int blockID = ItemNBTHelper.getInt(stack, "findBlockID", 0);
        if (blockID == 0) {
            return null;
        }
        return Block.getBlockById(blockID);
    }

    public static int getFindMeta(ItemStack stack) {
        return ItemNBTHelper.getInt(stack, "findBlockMeta", -1);
    }
}
