package net.walksanator.hexdim.patterns.dim

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.EntityIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.ListIota
import at.petrak.hexcasting.api.casting.mishaps.MishapDisallowedSpell
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions
import net.minecraft.entity.Entity
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.world.TeleportTarget
import net.minecraft.world.World
import net.walksanator.hexdim.casting.HexDimComponents

class OpBanish : ConstMediaAction {
    override val argc = 1
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val ext =  env.getExtension(HexDimComponents.VecInRange.KEY)
        val envEnabled = ext != null
        if (envEnabled) {
            val iota = args[0]
            val world = env.world.server.getWorld(World.OVERWORLD)!!
            if (iota.type == ListIota.TYPE) {
                val iotas = (iota as ListIota).list.filter { value -> value.type == EntityIota.TYPE }
                if (iotas.isEmpty()) {throw MishapInvalidIota(iota,0,Text.literal("List contains no entities"))}
                for (entity in iotas) {
                    banish(env, world, (entity as EntityIota).entity)
                }
            } else if (iota.type == EntityIota.TYPE) {
                banish(env, world, (iota as EntityIota).entity)
            } else {throw MishapInvalidIota(iota,0, Text.literal("Iota is not a list of entities or entity"))}
        } else {
            throw MishapDisallowedSpell() //TODO: make mishap for not in env
        }
        return listOf()
    }

    private fun banish(env: CastingEnvironment, world: ServerWorld, target: Entity) {
        env.assertEntityInRange(target)
        val pos = world.getTopPosition(net.minecraft.world.Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, world.spawnPos)
        FabricDimensions.teleport(
            target,
            world,
            TeleportTarget(
                pos.toCenterPos(),
                target.velocity,
                target.headYaw,
                target.pitch
            )
        )
    }
}