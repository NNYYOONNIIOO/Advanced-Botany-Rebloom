package ab.common.entity;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityAnonymousSteve extends AbstractClientPlayer {
    public EntityAnonymousSteve(World world) {
        super(world, new GameProfile(null, "abSteveForRenderer"));
    }

    public boolean canUseCommand(int i, String s) {
        return false;
    }

    public BlockPos getBedLocation() {
        return null;
    }

    @SideOnly(Side.CLIENT)
    public int getBrightnessForRender() {
        return 0xF000F0;
    }

    public boolean isInvisible() {
        return true;
    }

    public void sendMessage(ITextComponent var1) {
    }
}
