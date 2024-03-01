package net.walksanator.hexdim.iotas

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.IotaType
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier
import net.walksanator.hexdim.HexxyDimensions
import java.util.function.BiConsumer

object IotaTypes {
    @JvmStatic
    fun registerTypes() {
        val r = BiConsumer { type: IotaType<*>, id: Identifier -> Registry.register(HexIotaTypes.REGISTRY, id, type) }
        for ((key, value) in TYPES) {
            r.accept(value, key)
        }
    }

    private val TYPES: MutableMap<Identifier, IotaType<*>> = LinkedHashMap()
    val LEGACY_ENTRY_IOTA_TYPE = LegacyEntryIotaType()
    val ROOM = type("room",RoomIota.TYPE)
    val ENTRY = type("entry", LEGACY_ENTRY_IOTA_TYPE)

    private fun <U : Iota, T : IotaType<U>> type(@Suppress("SameParameterValue", "SameParameterValue") name: String, type: T): T {
        val old = TYPES.put(Identifier(HexxyDimensions.MOD_ID, name), type)
        require(old == null) { "Typo? Duplicate id $name" }
        return type
    }
}
