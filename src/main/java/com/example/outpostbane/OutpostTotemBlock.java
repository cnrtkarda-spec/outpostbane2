package com.example.outpostbane;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.StructureStart;

public class OutpostTotemBlock extends Block {
	public OutpostTotemBlock(Properties properties) {
		super(properties);
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
		if (!state.is(newState.getBlock()) && level instanceof ServerLevel serverLevel) {
			StructureStart start = Outposts.findOutpost(serverLevel, pos);
			if (start != null) {
				Outposts.setState(serverLevel.getServer(), start.getChunkPos(), Outposts.CLEARED);
				serverLevel.playSound(null, pos, SoundEvents.WITHER_BREAK_BLOCK, SoundSource.BLOCKS, 0.6f, 1.2f);
				serverLevel.players().stream()
					.filter(p -> p.distanceToSqr(pos.getCenter()) < 64 * 64)
					.forEach(p -> p.displayClientMessage(
						Component.literal("Outpost Totem yok edildi! Bu outpost'ta artik pillager dogmayacak."), true));
			}
		}
		super.onRemove(state, level, pos, newState, moved);
	}
}
