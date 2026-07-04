package ab.client.core.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

import java.util.ArrayList;
import java.util.List;

public class PlayerItemUsingSound {
    public EntityPlayer player;
    public ISound sound;
    public int maxTick = 0;
    public int tick = 0;
    public boolean isDeadingSound = false;
    public boolean hasCheckUse;

    public PlayerItemUsingSound(EntityPlayer player, ISound sound, int maxTick, boolean hasCheckUse) {
        this.player = player;
        this.sound = sound;
        this.maxTick = maxTick;
        this.hasCheckUse = hasCheckUse;
    }

    public static class ClientSoundHandler {
        public static Minecraft mc = Minecraft.getMinecraft();
        public static List<PlayerItemUsingSound> soundsList = new ArrayList<PlayerItemUsingSound>();

        public static void playSound(EntityPlayer player, String name, float volume, float pitch, int maxTick, boolean hasCheckUse) {
            if (!soundsList.isEmpty()) {
                for (int i = 0; i < soundsList.size(); ++i) {
                    PlayerItemUsingSound playerSound = soundsList.get(i);
                    if (playerSound.player.getName().equals(player.getName())) {
                        if (playerSound.maxTick != 0 && playerSound.maxTick <= playerSound.tick && !playerSound.isDeadingSound) {
                            playerSound.isDeadingSound = true;
                            ClientSoundHandler.setSound(player, name, volume, pitch, maxTick, hasCheckUse);
                            return;
                        }
                        return;
                    }
                    ClientSoundHandler.setSound(player, name, volume, pitch, maxTick, hasCheckUse);
                }
            } else {
                ClientSoundHandler.setSound(player, name, volume, pitch, maxTick, hasCheckUse);
            }
        }

        public static void playSound(EntityPlayer player, String name, float volume, float pitch, int maxTick) {
            ClientSoundHandler.playSound(player, name, volume, pitch, maxTick, true);
        }

        public static void playSound(EntityPlayer player, String name, float volume, float pitch) {
            ClientSoundHandler.playSound(player, name, volume, pitch, 0);
        }

        protected static void setSound(EntityPlayer player, String name, float volume, float pitch, int maxTick, boolean hasCheckUse) {
            SoundEvent soundEvent = SoundEvent.REGISTRY.getObject(new ResourceLocation(name));
            PositionedSoundRecord posSound = new PositionedSoundRecord(soundEvent, SoundCategory.PLAYERS, volume, pitch, (float) player.posX, (float) player.posY, (float) player.posZ);
            soundsList.add(new PlayerItemUsingSound(player, posSound, maxTick, hasCheckUse));
            mc.getSoundHandler().playSound(posSound);
        }

        public static void tick() {
            if (!soundsList.isEmpty()) {
                for (int i = 0; i < soundsList.size(); ++i) {
                    PlayerItemUsingSound playerSound = soundsList.get(i);
                    ++playerSound.tick;
                    if (!mc.getSoundHandler().isSoundPlaying(playerSound.sound)) {
                        soundsList.remove(playerSound);
                        continue;
                    }
                    if (!playerSound.hasCheckUse || playerSound.player.isHandActive()) continue;
                    if (mc.getSoundHandler().isSoundPlaying(playerSound.sound)) {
                        mc.getSoundHandler().stopSound(playerSound.sound);
                    }
                    soundsList.remove(playerSound);
                }
            }
        }
    }
}
