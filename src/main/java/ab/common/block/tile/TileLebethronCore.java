package ab.common.block.tile;

import ab.api.IRenderHud;
import ab.common.lib.register.BlockListAB;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.api.lexicon.multiblock.Multiblock;
import vazkii.botania.api.lexicon.multiblock.MultiblockSet;
import vazkii.botania.common.Botania;
import vazkii.botania.common.block.tile.TileMod;

public class TileLebethronCore extends TileMod implements IRenderHud, net.minecraft.util.ITickable {
    protected int tick;
    protected Block block;
    protected int meta;
    protected boolean validTree;

    public static MultiblockSet makeMultiblockSet() {
        Multiblock mb = new Multiblock();
        mb.addComponent(new BlockPos(0, 1, 0), BlockListAB.blockLebethron.getStateFromMeta(4));
        mb.addComponent(new BlockPos(0, 0, 0), BlockListAB.blockLebethron.getStateFromMeta(0));
        mb.addComponent(new BlockPos(0, 2, 0), BlockListAB.blockLebethron.getStateFromMeta(0));
        mb.addComponent(new BlockPos(0, 3, 0), BlockListAB.blockLebethron.getStateFromMeta(0));
        mb.addComponent(new BlockPos(0, 4, 0), BlockListAB.blockLebethron.getStateFromMeta(0));
        mb.addComponent(new BlockPos(0, 5, 0), BlockListAB.blockLebethron.getStateFromMeta(0));
        mb.setRenderOffset(new BlockPos(0, 1, 0));
        return mb.makeSet();
    }

    @Override
    public void update() {
        if (!this.getWorld().isRemote) {
            if (this.tick <= 0) {
                this.updateStructure();
                if (this.validTree && this.getBlock() != null) {
                    this.spawnLeaves();
                    this.tick = 40;
                }
            } else {
                this.tick--;
            }
        } else if (this.getWorld().rand.nextBoolean()) {
            Botania.proxy.sparkleFX(this.getPos().getX() + Math.random(), this.getPos().getY() + Math.random(), this.getPos().getZ() + Math.random(), 0.5f, 1.0f, 0.5f, (float) Math.random() * 2.0f, 2);
        }
    }

    public boolean getValidTree() {
        return this.validTree;
    }

    public void updateStructure() {
        boolean oldValidTree = this.validTree;
        this.validTree = this.hasValidTree();
        if (oldValidTree != this.validTree) {
            VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this.getWorld(), this.getPos());
        }
    }

    @Override
    public void writePacketNBT(NBTTagCompound nbtt) {
        nbtt.setInteger("blockID", Block.getIdFromBlock(this.block));
        nbtt.setInteger("blockMeta", this.meta);
        nbtt.setInteger("tick", this.tick);
        nbtt.setBoolean("validTree", this.validTree);
    }

    @Override
    public void readPacketNBT(NBTTagCompound nbtt) {
        this.block = Block.getBlockById(nbtt.getInteger("blockID"));
        this.meta = nbtt.getInteger("blockMeta");
        this.tick = nbtt.getInteger("tick");
        this.validTree = nbtt.getBoolean("validTree");
    }

    public boolean setBlock(EntityPlayer player, Block block, int meta) {
        if (this.block == null || this.block == Blocks.AIR) {
            this.block = block;
            this.meta = meta;
            return true;
        }
        if (Block.isEqualTo(this.block, block) && this.meta == meta) {
            return false;
        }
        if (!this.getWorld().isRemote) {
            Vec3d vec3 = player.getLookVec();
            EntityItem entityitem = new EntityItem(this.getWorld(), player.posX + vec3.x, player.posY + 1.2, player.posZ + vec3.z, new ItemStack(this.block, 1, this.meta));
            this.getWorld().spawnEntity(entityitem);
        }
        this.block = block;
        this.meta = meta;
        return true;
    }

    public Block getBlock() {
        if (this.block != null && this.block != Blocks.AIR) {
            return this.block;
        }
        return null;
    }

    public int getMeta() {
        return this.meta;
    }

    public boolean hasValidTree() {
        BlockPos pos = this.getPos();
        if (!this.checkBlock(this.getWorld().getBlockState(pos.down()).getBlock(), this.block != null ? 0 : 0)) {
            if (!this.checkBlock(this.getWorld().getBlockState(pos.down()).getBlock(), this.getWorld().getBlockState(pos.down()).getBlock().getMetaFromState(this.getWorld().getBlockState(pos.down()))))
                return false;
        }
        for (int i = 1; i <= 4; i++) {
            if (!this.checkBlock(this.getWorld().getBlockState(pos.up(i)).getBlock(), this.getWorld().getBlockState(pos.up(i)).getBlock().getMetaFromState(this.getWorld().getBlockState(pos.up(i)))))
                return false;
        }
        return true;
    }

    boolean checkBlock(Block block, int meta) {
        return block == BlockListAB.blockLebethron && meta == 0;
    }

    private void spawnLeaves() {
        BlockPos pos = this.getPos();
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                for (int y = 0; y < 5; y++) {
                    this.setBlock(this.getWorld(), pos.getX() + x, pos.getY() + y + 2, pos.getZ() + z);
                }
            }
        }
        this.setBlock(this.getWorld(), pos.getX() + 1, pos.getY() + 1, pos.getZ());
        this.setBlock(this.getWorld(), pos.getX() - 1, pos.getY() + 1, pos.getZ());
        this.setBlock(this.getWorld(), pos.getX(), pos.getY() + 1, pos.getZ() + 1);
        this.setBlock(this.getWorld(), pos.getX(), pos.getY() + 1, pos.getZ() - 1);
        this.setBlock(this.getWorld(), pos.getX() + 1, pos.getY() + 7, pos.getZ());
        this.setBlock(this.getWorld(), pos.getX() - 1, pos.getY() + 7, pos.getZ());
        this.setBlock(this.getWorld(), pos.getX(), pos.getY() + 7, pos.getZ() + 1);
        this.setBlock(this.getWorld(), pos.getX(), pos.getY() + 7, pos.getZ() - 1);
        for (int i = 0; i <= 3; i++) {
            this.setBlock(this.getWorld(), pos.getX(), pos.getY() + 6 + i, pos.getZ());
        }
        this.setBlock(this.getWorld(), pos.getX(), pos.getY() + 2, pos.getZ() - 2);
        for (int i = -1; i <= 1; i++) {
            this.setBlock(this.getWorld(), pos.getX() + i, pos.getY() + 3, pos.getZ() - 2);
            this.setBlock(this.getWorld(), pos.getX() + i, pos.getY() + 4, pos.getZ() - 2);
        }
        this.setBlock(this.getWorld(), pos.getX(), pos.getY() + 5, pos.getZ() - 2);
        this.setBlock(this.getWorld(), pos.getX(), pos.getY() + 2, pos.getZ() + 2);
        for (int i = -1; i <= 1; i++) {
            this.setBlock(this.getWorld(), pos.getX() + i, pos.getY() + 3, pos.getZ() + 2);
            this.setBlock(this.getWorld(), pos.getX() + i, pos.getY() + 4, pos.getZ() + 2);
        }
        this.setBlock(this.getWorld(), pos.getX(), pos.getY() + 5, pos.getZ() + 2);
        this.setBlock(this.getWorld(), pos.getX() + 2, pos.getY() + 2, pos.getZ());
        for (int i = -1; i <= 1; i++) {
            this.setBlock(this.getWorld(), pos.getX() + 2, pos.getY() + 3, pos.getZ() + i);
            this.setBlock(this.getWorld(), pos.getX() + 2, pos.getY() + 4, pos.getZ() + i);
        }
        this.setBlock(this.getWorld(), pos.getX() + 2, pos.getY() + 5, pos.getZ());
        this.setBlock(this.getWorld(), pos.getX() - 2, pos.getY() + 2, pos.getZ());
        for (int i = -1; i <= 1; i++) {
            this.setBlock(this.getWorld(), pos.getX() - 2, pos.getY() + 3, pos.getZ() + i);
            this.setBlock(this.getWorld(), pos.getX() - 2, pos.getY() + 4, pos.getZ() + i);
        }
        this.setBlock(this.getWorld(), pos.getX() - 2, pos.getY() + 5, pos.getZ());
    }

    private void setBlock(World world, int x, int y, int z) {
        if (world.rand.nextInt(10) <= 8) {
            return;
        }
        BlockPos blockPos = new BlockPos(x, y, z);
        if (world.getBlockState(blockPos).getMaterial() == Material.AIR && y < 256) {
            world.setBlockState(blockPos, this.block.getStateFromMeta(this.meta), 3);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderHud() {
        if (!this.validTree) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution res = new ScaledResolution(mc);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        ItemStack stack = this.getBlock() != null ? new ItemStack(this.block, 1, this.meta) : new ItemStack(Blocks.LEAVES);
        int x = res.getScaledWidth() / 2 - 7;
        int y = res.getScaledHeight() / 2 + 12;
        Gui.drawRect(x - 2, y - 2, x + 18, y + 18, 0x44000000);
        Gui.drawRect(x, y, x + 16, y + 16, 0x44000000);
        RenderHelper.enableGUIStandardItemLighting();
        GL11.glEnable(32826);
        mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, y);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(2929);
        boolean unicode = mc.fontRenderer.getUnicodeFlag();
        mc.fontRenderer.setUnicodeFlag(true);
        if (this.getBlock() != null) {
            mc.fontRenderer.drawString("\u2713", x + 10, y + 8, 774669);
            mc.fontRenderer.drawString("\u2713", x + 10, y + 8, 19456);
        } else {
            mc.fontRenderer.drawString("\u2717", x + 10, y + 8, 13764621);
            mc.fontRenderer.drawString("\u2717", x + 10, y + 8, 0x4C0000);
        }
        mc.fontRenderer.setUnicodeFlag(unicode);
        GL11.glEnable(2929);
        GL11.glDisable(3042);
    }
}
