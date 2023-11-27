package net.walksanator.hexdim

import net.minecraft.block.Blocks
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.server.MinecraftServer
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.PersistentState
import net.minecraft.world.chunk.ChunkStatus
import net.walksanator.hexdim.util.Rectangle
import net.walksanator.hexdim.util.Room
import net.walksanator.hexdim.util.addRectangle
import kotlin.math.max


class HexxyDimStorage : PersistentState() {
    val open = ArrayList<Room>()
    val all = ArrayList<Room>()
    val free = ArrayList<Int>()
    var world: ServerWorld? = null
    override fun writeNbt(nbt: NbtCompound): NbtCompound {
        val rectangles = IntArray(all.size * 6)
        for ((idx, rect) in all.withIndex()) {
            rect.toIntArray().copyInto(rectangles,idx*6)
        }
        nbt.putIntArray("rects",rectangles)
        nbt.putIntArray("free",free)
        return nbt
    }

    fun mallocRoom(size: Pair<Int, Int>, height: Int): Room? {
        val size2 = Pair(size.first + xPad, size.second + yPad)
        val posRect = addRectangle(size2, height, this, Pair(0, 0), Pair(xPad, yPad))
        markDirty()
        return if (posRect) {
            val room = all.last()
            if (world != null) {
                carveRoom(room,world!!)
            } else {
                HexxyDimensions.logger.error("WARNING!!! room was malloc'd without any world being sent to the storage. THIS IS A BUG")
            }
            room
        } else {
            null
        }
    }

    fun carveRoom(room: Room, world: ServerWorld) {
        for (pos in BlockPos.iterate(
            BlockPos(room.getX(),0,room.getY()),
            BlockPos(room.getX() + max(room.getW()-1,0), max(room.height-1,0), room.getY() + max(room.getH()-1,0))
        )) {
            val chunkPos = world.getChunk(pos).pos
            world.getChunk(chunkPos.x,chunkPos.z, ChunkStatus.FULL,true)
            world.setBlockState(pos, Blocks.AIR.defaultState)
        }
    }

    fun insertRoom(room: Room) {
        all.add(room)
        open.add(room)
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
    }

    companion object {
        const val xPad = 64
        const val yPad = 64
        fun createFromNBT(nbt: NbtCompound): HexxyDimStorage {
            val storage = HexxyDimStorage()
            val rectangles = nbt.getIntArray("rects")
            for (i in 0 ..< (rectangles.size/6)) {
                storage.all.add(
                    Room(
                        Rectangle(
                            rectangles[(i*6)],
                            rectangles[(i*6)+1],
                            rectangles[(i*6)+2],
                            rectangles[(i*6)+3]
                        ),
                        rectangles[(i*6)+4],
                        rectangles[(i*6)+5]
                    )
                )
            }
            storage.open.addAll(storage.all)
            nbt.getIntArray("free").toCollection(storage.free)
            return storage
        }
        fun getServerState(server: MinecraftServer): HexxyDimStorage {
            // (Note: arbitrary choice to use 'World.OVERWORLD' instead of 'World.END' or 'World.NETHER'.  Any work)
            val world = server.getWorld(RegistryKey.of(RegistryKeys.WORLD, Identifier("hexdim","hexdim")))!!
            val persistentStateManager = world.persistentStateManager

            // The first time the following 'getOrCreate' function is called, it creates a brand new 'StateSaverAndLoader' and
            // stores it inside the 'PersistentStateManager'. The subsequent calls to 'getOrCreate' pass in the saved
            // 'StateSaverAndLoader' NBT on disk to our function 'StateSaverAndLoader::createFromNbt'.
            val state: HexxyDimStorage = persistentStateManager.getOrCreate({ nbt -> createFromNBT(nbt) }, { HexxyDimStorage() }, "hexdim")

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