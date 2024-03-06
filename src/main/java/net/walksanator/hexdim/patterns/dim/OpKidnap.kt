package net.walksanator.hexdim.patterns.dim

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.EntityIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.misc.MediaConstants
import com.mojang.datafixers.util.Either
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions
import net.minecraft.entity.Entity
import net.minecraft.text.Text
import net.minecraft.util.math.Vec3d
import net.minecraft.world.TeleportTarget
import net.walksanator.hexdim.HexxyDimensions
import net.walksanator.hexdim.casting.VarMediaOutputAction
import net.walksanator.hexdim.iotas.RoomIota


class OpKidnap : VarMediaOutputAction {
    override val argc = 2
    override fun execute(args: List<Iota>, env: CastingEnvironment): VarMediaOutputAction.CastResult {
        val room = args[0]
        val mediacost: Long
        if (room !is RoomIota) {throw MishapInvalidIota(room,1,Text.translatable("hexdim.iota.roomlike"))}
        if (!(room).permissions[0]) {throw MishapInvalidIota(room,1, Text.translatable("hexdim.iota.permissions.read"))}
        val iota = args[1]

        val target: Either<EntityIota,ListIota> = when (iota.type) {
            ListIota.TYPE -> {
                mediacost = MediaConstants.SHARD_UNIT * (iota as ListIota).list.size()
                Either.right(iota)
            }
            EntityIota.TYPE -> {
                mediacost = MediaConstants.SHARD_UNIT
                Either.left(iota as EntityIota)
            }
            else -> {
                throw MishapInvalidIota(iota, 1, Text.literal("Iota is not a list of entities or entity"))
            }
        }
        return Spell(mediacost,room,target)
    }

    class Spell(cost: Long, val room: RoomIota,val targets: Either<EntityIota, ListIota>) : VarMediaOutputAction.CastResult(cost, listOf()) {
        override fun run(env: CastingEnvironment): List<Iota> {
            targets.ifRight {iota ->
                val iotas = iota.list.filter { value -> value.type == EntityIota.TYPE }
                if (iotas.isEmpty()) {
                    throw MishapInvalidIota(iota, 1, Text.literal("List contains no entities"))
                }
                for (entity in iotas) {
                    val target = (entity as EntityIota).entity
                    env.assertEntityInRange(target)
                    kidnap(room.getTeleportPosition(), target)
                }
            }
            targets.ifLeft { iota ->
                val target = (iota as EntityIota).entity
                env.assertEntityInRange(target)
                kidnap(room.getTeleportPosition(), target)
            }
            return listOf()
        }

    }
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