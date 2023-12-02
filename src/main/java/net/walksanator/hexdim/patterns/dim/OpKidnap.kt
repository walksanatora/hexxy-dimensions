package net.walksanator.hexdim.patterns.dim

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.EntityIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions
import net.minecraft.entity.Entity
import net.minecraft.text.Text
import net.minecraft.util.math.Vec3d
import net.minecraft.world.TeleportTarget
import net.walksanator.hexdim.HexxyDimensions
import net.walksanator.hexdim.iotas.RoomAccess

class OpKidnap : ConstMediaAction {
    override val argc = 2
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val room = args[0]
        if (room !is RoomAccess) {throw MishapInvalidIota(room,1,Text.literal("expected room-access iota"))} //TODO: make and use a translation string for room-access iota
        val iota = args[1]
        if (iota.type == ListIota.TYPE) {
            val iotas = (iota as ListIota).list.filter { value -> value.type == EntityIota.TYPE }
            if (iotas.isEmpty()) {
                throw MishapInvalidIota(iota, 1, Text.literal("List contains no entities"))
            }
            for (entity in iotas) {
                val target = (entity as EntityIota).entity
                env.assertEntityInRange(target)
                kidnap(room.getTeleportPosition(), target)
            }
        } else if (iota.type == EntityIota.TYPE) {
            val target = (iota as EntityIota).entity
            env.assertEntityInRange(target)
            kidnap(room.getTeleportPosition(), target)
        } else {
            throw MishapInvalidIota(iota, 1, Text.literal("Iota is not a list of entities or entity"))
        }
        return listOf()
    }

    fun kidnap(pos: Vec3d, entity: Entity) {
        val storage = HexxyDimensions.STORAGE.get()
        FabricDimensions.teleport(
            entity,
            storage.world,
            TeleportTarget(
                pos,
                Vec3d.ZERO,
                0F,0F
            )
        )
    }
}