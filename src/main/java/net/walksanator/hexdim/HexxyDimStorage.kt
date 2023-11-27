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


class HexxyDimStorage : PersistentState() {
    val open = ArrayList<Rectangle>()
    val all = ArrayList<Rectangle>()
    val free = ArrayList<Int>()
    var world: ServerWorld? = null
    override fun writeNbt(nbt: NbtCompound): NbtCompound {
        val rectangles = IntArray(all.size * 5)
        for ((idx, rect) in all.withIndex()) {
            rectangles[(idx*5) + 0] = rect.x
            rectangles[(idx*5) + 1] = rect.y
            rectangles[(idx*5) + 2] = rect.w
            rectangles[(idx*5) + 3] = rect.h
            rectangles[(idx*5) + 4] = rect.height
        }
        nbt.putIntArray("rects",rectangles)
        nbt.putIntArray("free",free)
        return nbt
    }

    fun mallocRoom(size: Pair<Int, Int>, height: Int): Rectangle? {
        val xPad = 64
        val yPad = 64
        val size2 = Pair(size.first + xPad, size.second + yPad)
        val posRect = addRectangle(size2, height, this, Pair(0, 0), Pair(xPad, yPad))
        markDirty()
        return if (posRect) {
            val rect = all.last()
            if (world != null) {
                for (pos in BlockPos.iterate(
                        BlockPos(rect.x+(xPad/2),0,rect.y+(yPad/2)),
                        BlockPos(rect.x + (rect.w-xPad), rect.height, rect.y + (rect.h-yPad))
                )) {
                    val cpos = world!!.getChunk(pos).pos
                    world!!.getChunk(cpos.x,cpos.z, ChunkStatus.FULL,true)
                    world!!.setBlockState(pos, Blocks.AIR.defaultState)
                }
            } else {
                HexxyDimensions.logger.error("WARNING!!! room was malloc'd without any world being sent to the storage. THIS IS A BUG")
            }
            rect
        } else {
            null
        }
    }

    fun freeRoom(index: Int) {
        free.add(index)
    }

    companion object {
        fun createFromNBT(nbt: NbtCompound): HexxyDimStorage {
            val storage = HexxyDimStorage()
            val rectangles = nbt.getIntArray("rects")
            for (i in 0 ..< (rectangles.size/5)) {
                storage.all.add(
                    Rectangle(
                        rectangles[(i*5)],
                        rectangles[(i*5)+1],
                        rectangles[(i*5)+2],
                        rectangles[(i*5)+3],
                        rectangles[(i*5)+4]
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