package igentuman.blockbooster.client;

import igentuman.blockbooster.tile.BoosterBE;
import igentuman.blockbooster.setup.Registration;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import static java.lang.Boolean.TRUE;

public class BoosterRenderer implements BlockEntityRenderer<BoosterBE> {

    public BoosterRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(BoosterBE blockbooster, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {

        Boolean powered = blockbooster.getBlockState().getValue(BlockStateProperties.POWERED);
        if (TRUE != powered) {
            return;
        }

        float s = (System.currentTimeMillis() % 1000) / 1000.0f;
        if (s > 0.5f) {
            s = 1.0f - s;
        }
        float scale = 0.1f + s * 2;

        // Always remember to push the current transformation so that you can restore it later
        poseStack.pushPose();

        // Translate to the middle of the block and 1 unit higher
        poseStack.translate(0.5, 1.5, 0.5);

        // Use the orientation of the main camera to make sure the single quad that we are going to render always faces the camera
        Quaternion rotation = Minecraft.getInstance().gameRenderer.getMainCamera().rotation();
        poseStack.mulPose(rotation);

        poseStack.popPose();
    }

    public static void register() {
        BlockEntityRenderers.register(Registration.BLOCKBOOSTER_BE.get(), BoosterRenderer::new);
    }

}
