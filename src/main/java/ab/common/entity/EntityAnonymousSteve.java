package ab.common.entity;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EntityAnonymousSteve extends EntityPlayer {
    public EntityAnonymousSteve(World world) {
        super(world, new GameProfile(null, "abSteveForRenderer"));
    }

    public boolean canUseCommand(int i, String s) {
        return false;
    }

    public BlockPos getBedLocation() {
        return null;
    }

    public int getBrightnessForRender() {
        return 0xF000F0;
    }

    public boolean isInvisible() {
        return true;
    }

    public void sendMessage(ITextComponent var1) {
    }

    @Override
    public boolean isSpectator() {
        return false;
    }

    @Override
    public boolean isCreative() {
        return false;
    }
}
