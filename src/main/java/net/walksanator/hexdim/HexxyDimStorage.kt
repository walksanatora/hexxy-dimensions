package net.walksanator.hexdim

import net.minecraft.block.Blocks
import net.minecraft.nbt.NbtCompound
import net.minecraft.particle.ParticleTypes
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.server.MinecraftServer
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.PersistentState
import net.minecraft.world.chunk.ChunkStatus
import net.walksanator.hexdim.util.ProcessingQueue
import net.walksanator.hexdim.util.Rectangle
import net.walksanator.hexdim.util.Room
import net.walksanator.hexdim.util.addRectangle
import kotlin.math.max

fun IntArray.chunked(size: Int): List<IntArray> {
    val result = mutableListOf<IntArray>()
    var index = 0

    while (index < this.size) {
        val chunk = this.copyOfRange(index, minOf(index + size, this.size))
        result.add(chunk)
        index += size
    }

    return result
}

class HexxyDimStorage : PersistentState() {
    val open = ArrayList<Room>()
    val all = ArrayList<Room>()
    val free = ArrayList<Int>()
    var world: ServerWorld? = null

    private val roomCarveQueue16 = ProcessingQueue<Room>({ room ->
        HexxyDimensions.logger.info("carving x16")
        carveRoom(room, world!!)
        room.isDone = true
        HexxyDimensions.logger.info("finished carving x16")
        markDirty()
    },1000)
    private val roomCarveQueue32 = ProcessingQueue<Room>({ room ->
        HexxyDimensions.logger.info("carving x32")
        carveRoom(room, world!!)
        room.isDone = true
        HexxyDimensions.logger.info("finished carving x32")
        markDirty()
    },1000)
    private val roomCarveQueue64 = ProcessingQueue<Room>({ room ->
        HexxyDimensions.logger.info("carving x64")
        carveRoom(room, world!!)
        room.isDone = true
        HexxyDimensions.logger.info("finished carving x64")
        markDirty()
    },1000)
    private val roomCarveQueue128 = ProcessingQueue<Room>({ room ->
        HexxyDimensions.logger.info("carving 128")
        carveRoom(room, world!!)
        room.isDone = true
        HexxyDimensions.logger.info("finished carving x128")
        markDirty()
    },1000)

    fun enqueRoomCarving(room: Room) {
        val area = room.getH() * room.getW() * room.height
        when (area) {
            in 0..4096 -> roomCarveQueue16.enqueue(room)
            in 4097..32768 -> roomCarveQueue32.enqueue(room)
            in 32769..262144 -> roomCarveQueue64.enqueue(room)
            else -> roomCarveQueue128.enqueue(room)
        }
    }

    fun restartQueueJobs() {
        roomCarveQueue16.restart()
        roomCarveQueue32.restart()
        roomCarveQueue64.restart()
        roomCarveQueue128.restart()
    }

    fun enqueRoomCarvings(rooms: Collection<Room>) {
        rooms.forEach { enqueRoomCarving(it) }
    }

    fun getCarveQueueIdxs(): List<List<Int>> {
        return listOf(
            roomCarveQueue16.queue().map { room -> all.indexOf(room) },
            roomCarveQueue32.queue().map { room -> all.indexOf(room) },
            roomCarveQueue64.queue().map { room -> all.indexOf(room) },
            roomCarveQueue128.queue().map { room -> all.indexOf(room) }
        )
    }

    override fun writeNbt(nbt: NbtCompound): NbtCompound {
        val rectangles = IntArray(all.size * Room.argc)
        for ((idx, rect) in all.withIndex()) {
            rect.toIntArray().copyInto(rectangles, idx * Room.argc)
        }
        nbt.putIntArray("rects", rectangles)
        nbt.putIntArray("free", free)
        return nbt
    }

    fun mallocRoom(size: Pair<Int, Int>, height: Int): Room? {
        val size2 = Pair(size.first + X_PADDING, size.second + Y_PADDING)
        val posRect = addRectangle(size2, height, this, Pair(0, 0), Pair(X_PADDING, Y_PADDING))
        markDirty()
        return if (posRect) {
            all.last()
        } else {
            null
        }
    }

    private fun carveRoom(room: Room, world: ServerWorld) {
        for (pos in BlockPos.iterate(
            BlockPos(room.getX(), 0, room.getY()),
            BlockPos(
                room.getX() + max(room.getW() - 1, 0),
                max(room.height - 1, 0),
                room.getY() + max(room.getH() - 1, 0)
            )
        )) {
            val chunkPos = world.getChunk(pos).pos
            val zero = 0.toDouble()
            world.spawnParticles(ParticleTypes.SMOKE,
                pos.x.toDouble()+0.5,
                pos.y.toDouble()+0.5,
                pos.z.toDouble()+0.5,
                10,
                zero,zero,zero,zero
            )
            if (world.getBlockState(pos).block == Blocks.AIR) {continue}
            world.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.FULL, true)
            world.setBlockState(pos, Blocks.AIR.defaultState)
        }
    }

    fun insertRoom(room: Room) {
        all.add(room)
        open.add(room)
        if (!room.isDone) {
            enqueRoomCarving(room)
        }
    }

    fun closeRoomsBulk(rect: List<Rectangle>) {
        val toClose: MutableList<Room> = mutableListOf()
        for (x in open) {
            for (y in rect) {
                if (x.rect == y) toClose.add(x)
            }
        }
        for (room in toClose) {
            open.remove(room)
        }
    }

    fun freeRoom(index: Int) {
        free.add(index)
        all[index].isDone = false
        enqueRoomCarving(all[index]) // we gotta clear this room
    }

    companion object {
        const val X_PADDING = 64
        const val Y_PADDING = 64
        fun createFromNBT(nbt: NbtCompound): HexxyDimStorage {
            val storage = HexxyDimStorage()
            val rectangles = nbt.getIntArray("rects")
            for (i in rectangles.chunked(Room.argc)) {
                storage.all.add(
                    Room(i)
                )
            }
            storage.open.addAll(storage.all)
            nbt.getIntArray("free").toCollection(storage.free)

            storage.enqueRoomCarvings(
                storage.all.filter { room -> !room.isDone }
            )

            return storage
        }

        fun getServerState(server: MinecraftServer): HexxyDimStorage {
            // (Note: arbitrary choice to use 'World.OVERWORLD' instead of 'World.END' or 'World.NETHER'.  Any work)
            val world = server.getWorld(RegistryKey.of(RegistryKeys.WORLD, Identifier("hexdim", "hexdim")))!!
            val persistentStateManager = world.persistentStateManager

            // The first time the following 'getOrCreate' function is called, it creates a brand new 'StateSaverAndLoader' and
            // stores it inside the 'PersistentStateManager'. The subsequent calls to 'getOrCreate' pass in the saved
            // 'StateSaverAndLoader' NBT on disk to our function 'StateSaverAndLoader::createFromNbt'.
            val state: HexxyDimStorage =
                persistentStateManager.getOrCreate({ nbt -> createFromNBT(nbt) }, { HexxyDimStorage() }, "hexdim")

            state.world = world

            // If state is not marked dirty, when Minecraft closes, 'writeNbt' won't be called and therefore nothing will be saved.
            // Technically it's 'cleaner' if you only mark state as dirty when there was actually a change, but the vast majority
            // of mod writers are just going to be confused when their data isn't being saved, and so it's best just to 'markDirty' for them.
            // Besides, it's literally just setting a bool to true, and the only time there's a 'cost' is when the file is written to disk when
            // there were no actual change to any of the mods state (INCREDIBLY RARE).
            state.markDirty()
            return state
        }
    }
}