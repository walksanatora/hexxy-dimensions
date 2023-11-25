package net.walksanator.hexdim

import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.server.MinecraftServer
import net.minecraft.util.Identifier
import net.minecraft.world.PersistentState


class HexxyDimStorage : PersistentState() {
    val open = ArrayList<Rectangle>()
    val all = ArrayList<Rectangle>()
    override fun writeNbt(nbt: NbtCompound): NbtCompound {
        val rectangles = IntArray(all.size * 4)
        for ((idx, rect) in all.withIndex()) {
            rectangles[idx] = rect.x
            rectangles[idx+1] = rect.y
            rectangles[idx+2] = rect.w
            rectangles[idx+3] = rect.h
        }
        nbt.putIntArray("rects",rectangles)
        return nbt
    }

    fun getServerState(server: MinecraftServer): HexxyDimStorage {
        // (Note: arbitrary choice to use 'World.OVERWORLD' instead of 'World.END' or 'World.NETHER'.  Any work)
        val persistentStateManager = server.getWorld(RegistryKey.of(RegistryKeys.WORLD, Identifier("hexdim","hexdim")))!!.persistentStateManager

        // The first time the following 'getOrCreate' function is called, it creates a brand new 'StateSaverAndLoader' and
        // stores it inside the 'PersistentStateManager'. The subsequent calls to 'getOrCreate' pass in the saved
        // 'StateSaverAndLoader' NBT on disk to our function 'StateSaverAndLoader::createFromNbt'.
        val state: HexxyDimStorage = persistentStateManager.getOrCreate({ nbt -> createFromNBT(nbt) }, { HexxyDimStorage() }, "hexdim")

        // If state is not marked dirty, when Minecraft closes, 'writeNbt' won't be called and therefore nothing will be saved.
        // Technically it's 'cleaner' if you only mark state as dirty when there was actually a change, but the vast majority
        // of mod writers are just going to be confused when their data isn't being saved, and so it's best just to 'markDirty' for them.
        // Besides, it's literally just setting a bool to true, and the only time there's a 'cost' is when the file is written to disk when
        // there were no actual change to any of the mods state (INCREDIBLY RARE).
        state.markDirty()
        return state
    }


    companion object {
        fun createFromNBT(nbt: NbtCompound): HexxyDimStorage {
            val storage = HexxyDimStorage()
            val rectangles = nbt.getIntArray("rects")
            for (i in 0..(rectangles.size /4)) {
                storage.all.add(
                    Rectangle(
                        rectangles[i],
                        rectangles[i+1],
                        rectangles[i+2],
                        rectangles[i+3]
                    )
                )
            }
            return storage
        }
    }
}