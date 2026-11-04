package ab.common.integration.thaumcraft;

import ab.common.core.handler.NetworkHandler;
import ab.common.core.proxy.packet.S03PacketAspecolus;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.common.container.ContainerResearchTable;
import thaumcraft.common.tiles.crafting.TileResearchTable;

public class AspecolusResearchHandler {
    public static final int BONUS = 6;
    private static final String APPLIED_TAG = "advancedBotanyAspecolusApplied";
    private static final Map<World, Set<UUID>> ACTIVE_PLAYERS = new WeakHashMap<>();
    private final Map<EntityPlayerMP, ClientState> clientStates = new WeakHashMap<>();
    private final Map<TileResearchTable, WeakReference<ResearchTableData>> sessions = new WeakHashMap<>();

    static void markActive(EntityPlayerMP player) {
        ACTIVE_PLAYERS.computeIfAbsent(player.world, world -> new HashSet<>()).add(player.getUniqueID());
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        World world = event.world;
        if (world.isRemote || event.phase != TickEvent.Phase.END) return;
        long now = SubTilePureGladiolus.getServerTime(world);
        if (now % 20 == 0) {
            for (Entity entity : world.loadedEntityList) {
                if (entity instanceof EntityItem) {
                    EntityItem item = (EntityItem) entity;
                    if (SubTilePureGladiolus.clearExpiredCooldown(item.getItem(), now)) {
                        item.setItem(item.getItem());
                    }
                }
            }
        }
        Set<UUID> active = ACTIVE_PLAYERS.remove(world);
        for (EntityPlayer entity : world.playerEntities) {
            if (!(entity instanceof EntityPlayerMP)) continue;
            EntityPlayerMP player = (EntityPlayerMP) entity;
            if (!(player.openContainer instanceof ContainerResearchTable)) {
                clientStates.remove(player);
                continue;
            }
            boolean hasBonus = active != null && active.contains(player.getUniqueID());
            ContainerResearchTable container = (ContainerResearchTable) player.openContainer;
            ClientState previous = clientStates.get(player);
            if (previous == null || previous.windowId != container.windowId || previous.active != hasBonus
                    || player.ticksExisted - previous.sentAt >= 20) {
                NetworkHandler.NETWORK.sendTo(new S03PacketAspecolus(container.windowId, hasBonus), player);
                clientStates.put(player, new ClientState(container.windowId, hasBonus, player.ticksExisted));
            }
            updateTheory(container.tileEntity, player, hasBonus);
        }
    }

    private void updateTheory(TileResearchTable table, EntityPlayerMP player, boolean hasBonus) {
        ResearchTableData data = table.data;
        NBTTagCompound tag = table.getTileData();
        WeakReference<ResearchTableData> previous = sessions.get(table);
        if (previous != null && previous.get() != data && tag.hasKey(APPLIED_TAG)) {
            tag.removeTag(APPLIED_TAG);
            table.markDirty();
        }
        sessions.put(table, new WeakReference<>(data));
        if (data == null) {
            if (tag.hasKey(APPLIED_TAG)) {
                tag.removeTag(APPLIED_TAG);
                table.markDirty();
            }
        } else if (hasBonus && player.getName().equals(data.player) && !tag.getBoolean(APPLIED_TAG)) {
            data.inspirationStart += BONUS;
            data.inspiration += BONUS;
            tag.setBoolean(APPLIED_TAG, true);
            table.markDirty();
            table.syncTile(false);
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        EntityPlayer player = event.player;
        if (player.world.isRemote || event.phase != TickEvent.Phase.END || player.ticksExisted % 20 != 0) return;
        long now = SubTilePureGladiolus.getServerTime(player.world);
        boolean inventoryChanged = false;
        for (ItemStack stack : player.inventory.mainInventory) {
            inventoryChanged |= SubTilePureGladiolus.clearExpiredCooldown(stack, now);
        }
        for (ItemStack stack : player.inventory.offHandInventory) {
            inventoryChanged |= SubTilePureGladiolus.clearExpiredCooldown(stack, now);
        }
        if (inventoryChanged) player.inventory.markDirty();
        for (Slot slot : player.openContainer.inventorySlots) {
            if (SubTilePureGladiolus.clearExpiredCooldown(slot.getStack(), now)) slot.onSlotChanged();
        }
    }

    private static class ClientState {
        final int windowId;
        final boolean active;
        final int sentAt;

        ClientState(int windowId, boolean active, int sentAt) {
            this.windowId = windowId;
            this.active = active;
            this.sentAt = sentAt;
        }
    }
}
