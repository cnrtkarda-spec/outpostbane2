package com.example.outpostbane;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;

/** Outpost durumlarini (aktif / temizlenmis) dunya kaydinda saklar. */
public final class Outposts {
	public static final byte UNPROCESSED = 0, ACTIVE = 1, CLEARED = 2;
	private static final ResourceLocation STORAGE = new ResourceLocation(OutpostBane.MOD_ID, "outposts");

	private Outposts() {}

	private static String key(ChunkPos p) { return p.x + "," + p.z; }

	public static byte getState(MinecraftServer server, ChunkPos outpost) {
		return server.getCommandStorage().get(STORAGE).getByte(key(outpost));
	}

	public static void setState(MinecraftServer server, ChunkPos outpost, byte state) {
		CompoundTag tag = server.getCommandStorage().get(STORAGE);
		tag.putByte(key(outpost), state);
		server.getCommandStorage().set(STORAGE, tag);
	}

	/** Verilen konumu kapsayan pillager outpost'unu dondurur, yoksa null. */
	public static StructureStart findOutpost(ServerLevel level, BlockPos pos) {
		Structure outpost = level.registryAccess().registryOrThrow(Registries.STRUCTURE).get(BuiltinStructures.PILLAGER_OUTPOST);
		if (outpost == null) return null;
		for (StructureStart start : level.structureManager().startsForStructure(SectionPos.of(pos), outpost)) {
			if (start.isValid() && start.getBoundingBox().isInside(pos)) return start;
		}
		return null;
	}

	/** Outpost'un orta sutununda (bu chunk icinde) en ustteki kati blogun ustunu secer. */
	public static BlockPos pickSpot(LevelChunk chunk, StructureStart start) {
		BoundingBox box = start.getBoundingBox();
		ChunkPos cp = chunk.getPos();
		int x = Mth.clamp(box.getCenter().getX(), cp.getMinBlockX(), cp.getMaxBlockX());
		int z = Mth.clamp(box.getCenter().getZ(), cp.getMinBlockZ(), cp.getMaxBlockZ());

		BlockPos.MutableBlockPos m = new BlockPos.MutableBlockPos(x, box.maxY(), z);
		while (m.getY() > box.minY() && chunk.getBlockState(m).isAir()) {
			m.move(Direction.DOWN);
		}
		int y = (m.getY() + 1 <= box.maxY()) ? m.getY() + 1 : m.getY();
		return new BlockPos(x, y, z);
	}
}
