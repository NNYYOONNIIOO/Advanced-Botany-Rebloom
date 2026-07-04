package ab.common.item.equipment;

import ab.api.AdvancedBotanyAPI;
import ab.common.core.handler.ConfigABHandler;
import ab.common.item.ItemMod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import vazkii.botania.api.mana.IManaUsingItem;
import vazkii.botania.api.mana.ManaItemHandler;

public class ItemNebulaRod extends ItemMod implements IManaUsingItem {

    public ItemNebulaRod() {
        super("nebulaRod");
        this.setMaxStackSize(1);
        this.setNoRepair();
        this.setMaxDamage(100);
    }

    @Override
    public boolean usesMana(ItemStack stack) {
        return true;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (stack.getMetadata() == 0 && checkWorld(world.provider.getDimensionType().getName())) {
            player.setActiveHand(hand);
        }
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    private boolean checkWorld(String name) {
        for (String str : ConfigABHandler.lockWorldNameNebulaRod) {
            if (str.equals(name)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BOW;
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity player, int par4, boolean par5) {
        if (!world.isRemote && player instanceof EntityPlayer && player.ticksExisted % ConfigABHandler.nebulaWandCooldownTick == 0 && stack.getMetadata() > 0 && ManaItemHandler.requestManaExactForTool(stack, (EntityPlayer) player, ConfigABHandler.nebulaRodManaCost, true)) {
            stack.setItemDamage(stack.getMetadata() - 1);
        }
        if (player instanceof EntityPlayer) {
            EntityPlayer p = (EntityPlayer) player;
            if (p.isHandActive() && p.getActiveItemStack() == stack) {
                int count = p.getItemInUseCount();
                onUsingTick(stack, p, count);
            }
        }
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return AdvancedBotanyAPI.rarityNebula;
    }

    public void onUsingTick(ItemStack stack, EntityPlayer p, int time) {
        time = getMaxItemUseDuration(stack) - time;
        if (p.world.isRemote && time > 0) {
            float progress = (float) Math.min(time, 110) / 110.0f;
            float distance = (1.0f - progress) * 5.0f;

            Vec3d look = p.getLookVec();
            double centerX = p.posX + look.x * distance;
            double centerY = p.posY + p.getEyeHeight() - 0.3 + look.y * distance;
            double centerZ = p.posZ + look.z * distance;

            float ringRadius = 1.5f * (1.0f - progress * 0.5f);
            int particleCount = 8 + (int)(progress * 12);

            Vec3d up = new Vec3d(0, 1, 0);
            if (Math.abs(look.y) > 0.9) up = new Vec3d(1, 0, 0);
            Vec3d right = look.crossProduct(up).normalize();
            Vec3d forward = right.crossProduct(look).normalize();

            for (int i = 0; i < particleCount; i++) {
                float angle = (float) (Math.random() * Math.PI * 2.0);
                double px = centerX + (right.x * Math.cos(angle) + forward.x * Math.sin(angle)) * ringRadius;
                double py = centerY + (right.y * Math.cos(angle) + forward.y * Math.sin(angle)) * ringRadius;
                double pz = centerZ + (right.z * Math.cos(angle) + forward.z * Math.sin(angle)) * ringRadius;

                float r = 0.4f + (float)(Math.random() * 0.3f);
                float g = 0.0f + (float)(Math.random() * 0.2f);
                float b = 0.8f + (float)(Math.random() * 0.2f);
                float size = 0.15f + progress * 0.2f;

                vazkii.botania.common.Botania.proxy.wispFX(px, py, pz, r, g, b, size,
                    (float)(Math.random() - 0.5) * 0.05f,
                    (float)(Math.random() - 0.5) * 0.05f,
                    (float)(Math.random() - 0.5) * 0.05f);
            }
        }
        if (time > 110 && !p.isSneaking()) {
            if (!p.world.isRemote) {
                BlockPos topBlock = getTopBlock(p.world, p);
                if (topBlock == null) {
                    p.stopActiveHand();
                    p.sendMessage(new TextComponentTranslation("ab.nebulaRod.notTeleporting").setStyle(new net.minecraft.util.text.Style().setColor(TextFormatting.DARK_PURPLE)));
                    return;
                }
                EnderTeleportEvent event = new EnderTeleportEvent((EntityLivingBase) p, topBlock.getX() + 0.5, topBlock.getY() + 0.5, topBlock.getZ() + 0.5, 0.0f);
                MinecraftForge.EVENT_BUS.post(event);
                if (event.isCanceled()) {
                    p.stopActiveHand();
                    p.sendMessage(new TextComponentTranslation("ab.nebulaRod.notTeleportingEvent").setStyle(new net.minecraft.util.text.Style().setColor(TextFormatting.DARK_PURPLE)));
                    return;
                }
                ((EntityPlayerMP) p).connection.setPlayerLocation(topBlock.getX() + 0.5, topBlock.getY() + 0.5, topBlock.getZ() + 0.5, p.rotationYaw, p.rotationPitch);
                p.world.playSound(null, p.posX, p.posY, p.posZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.PLAYERS, 1.2f, 1.2f);
            }
            if (!p.capabilities.isCreativeMode) {
                stack.setItemDamage(100);
            }
            p.stopActiveHand();
        }
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public boolean isFull3D() {
        return true;
    }

    public BlockPos getTopBlock(World world, EntityPlayer player) {
        Vec3d eyePos = new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ);
        Vec3d lookVec = player.getLookVec();
        Vec3d endPos = eyePos.add(lookVec.scale(256.0));

        // Ray trace to find the block the player is looking at
        RayTraceResult result = world.rayTraceBlocks(eyePos, endPos, false, true, false);
        if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK) {
            BlockPos hitPos = result.getBlockPos();
            // Try to find a valid standing position near the hit block
            for (int y = hitPos.getY() + 2; y >= hitPos.getY() - 2; y--) {
                BlockPos testPos = new BlockPos(hitPos.getX(), y, hitPos.getZ());
                if (isSafeStandPosition(world, testPos)) {
                    return testPos;
                }
            }
            return hitPos.up();
        }

        // No block hit - teleport to the farthest valid position in look direction
        int limitXZ = ConfigABHandler.limitXZCoords;
        for (int nextPos = 256; nextPos > 0 && nextPos > 8; --nextPos) {
            int nPosX = MathHelper.floor(player.posX + lookVec.x * nextPos);
            int nPosZ = MathHelper.floor(player.posZ + lookVec.z * nextPos);
            nPosX = Math.min(Math.max(nPosX, -(limitXZ - 1)), limitXZ - 1);
            nPosZ = Math.min(Math.max(nPosZ, -(limitXZ - 1)), limitXZ - 1);
            Chunk chunk = world.getChunk(nPosX >> 4, nPosZ >> 4);
            int x = nPosX & 0xF;
            int z = nPosZ & 0xF;
            for (int k = chunk.getTopFilledSegment() + 15; k > 0; --k) {
                net.minecraft.block.state.IBlockState state = chunk.getBlockState(x, k, z);
                net.minecraft.block.state.IBlockState stateAbove1 = chunk.getBlockState(x, k + 1, z);
                net.minecraft.block.state.IBlockState stateAbove2 = chunk.getBlockState(x, k + 2, z);
                boolean hasTopAir = stateAbove1.getMaterial() == net.minecraft.block.material.Material.AIR && stateAbove2.getMaterial() == net.minecraft.block.material.Material.AIR;
                if (state.getBlock().isAir(state, world, new BlockPos(nPosX, k, nPosZ)) || state.getBlock() == Blocks.BEDROCK || !hasTopAir)
                    continue;
                return new BlockPos(nPosX, k + 1, nPosZ);
            }
        }
        return null;
    }

    private boolean isSafeStandPosition(World world, BlockPos pos) {
        net.minecraft.block.state.IBlockState state = world.getBlockState(pos);
        net.minecraft.block.state.IBlockState stateAbove = world.getBlockState(pos.up());
        net.minecraft.block.state.IBlockState stateBelow = world.getBlockState(pos.down());
        return state.getMaterial() == net.minecraft.block.material.Material.AIR
            && stateAbove.getMaterial() == net.minecraft.block.material.Material.AIR
            && stateBelow.getMaterial() != net.minecraft.block.material.Material.AIR
            && stateBelow.getBlock() != Blocks.BEDROCK;
    }
}
