package igentuman.blockbooster.block;

import igentuman.blockbooster.config.CommonConfig;
import igentuman.blockbooster.container.BoosterT2Container;
import igentuman.blockbooster.tile.TileBoosterT2;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;

public class BlockBoosterT2 extends Block implements EntityBlock, IBoosterBlock {

    private static final VoxelShape RENDER_SHAPE = Shapes.box(0.1, 0.1, 0.1, 0.9, 0.9, 0.9);

    public BlockBoosterT2() {
        super(Properties.of()
                .sound(SoundType.METAL)
                .strength(2.0f)
                .lightLevel(state -> state.getValue(BlockStateProperties.POWERED) ? 14 : 0)
                .noOcclusion()
                .requiresCorrectToolForDrops()
        );
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter reader, BlockPos pos) {
        return RENDER_SHAPE;
    }

    // Note: appendHoverText doesn't override Block method in NeoForge 1.21
    // This method is called through Item.appendHoverText instead
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter reader, List<Component> list, TooltipFlag flags) {
        list.add(Component.translatable("hint.booster_t2_descr").withStyle(ChatFormatting.BLUE));
        list.add(Component.translatable("hint.booster_boost_rate", CommonConfig.GENERAL.t2_boost_rate.get()).withStyle(ChatFormatting.BLUE));
        list.add(Component.translatable("hint.booster_fe_tick", CommonConfig.GENERAL.t2_fe_per_tick.get()).withStyle(ChatFormatting.BLUE));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new TileBoosterT2(blockPos, blockState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return (lvl, pos, blockState, t) -> {
                if (t instanceof TileBoosterT2 tile) {
                    tile.tickClient();
                }
            };
        }
        return (lvl, pos, blockState, t)-> {
            if (t instanceof TileBoosterT2 tile) {
                tile.tickServer();
            }
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.POWERED);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        return state != null ? state.setValue(BlockStateProperties.POWERED, false) : defaultBlockState().setValue(BlockStateProperties.POWERED, false);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof TileBoosterT2) {
                if (player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.openMenu(new MenuProvider() {
                        @Override
                        public Component getDisplayName() {
                            return Component.empty();
                        }

                        @Override
                        public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity) {
                            return new BoosterT2Container(windowId, playerInventory, pos);
                        }
                    }, be.getBlockPos());
                }
            } else {
                throw new IllegalStateException("Our named block entity is missing!");
            }
        }
        return InteractionResult.SUCCESS;
    }
}
