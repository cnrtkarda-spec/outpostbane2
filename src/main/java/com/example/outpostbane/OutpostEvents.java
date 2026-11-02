package com.example.outpostbane;

import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class OutpostEvents {

	/** Outpost'un bulundugu chunk yuklenince totemi yerlestirir (eski dunyalardaki outpost'lara da). */
	@SubscribeEvent
	public void onChunkLoad(ChunkEvent.Load event) {
		if (!(event.getLevel() instanceof ServerLevel level) || !(event.getChunk() instanceof LevelChunk chunk)) return;

		for (Map.Entry<Structure, StructureStart> e : chunk.getAllStarts().entrySet()) {
			if (e.getKey().type() != StructureType.PILLAGER_OUTPOST) continue;
			StructureStart start = e.getValue();
			if (!start.isValid()) continue;

			var id = start.getChunkPos();
			if (Outposts.getState(level.getServer(), id) != Outposts.UNPROCESSED) continue;

			BlockPos spot = Outposts.pickSpot(chunk, start);
			chunk.setBlockState(spot, OutpostBane.TOTEM.get().defaultBlockState(), false);
			Outposts.setState(level.getServer(), id, Outposts.ACTIVE);
		}
	}

	/** Totemi kirilmis outpost'ta dogal pillager dogusunu iptal eder. */
	@SubscribeEvent
	public void onJoin(EntityJoinLevelEvent event) {
		if (!(event.getLevel() instanceof ServerLevel level)) return;
		if (!(event.getEntity() instanceof Pillager pillager)) return;
		if (event.loadedFromDisk() || pillager.isPersistenceRequired()) return;

		StructureStart start = Outposts.findOutpost(level, pillager.blockPosition());
		if (start != null && Outposts.getState(level.getServer(), start.getChunkPos()) == Outposts.CLEARED) {
			event.setCanceled(true);
		}
	}
}
