package net.walksanator.hexdim

import at.petrak.hexcasting.api.item.IotaHolderItem
import com.mojang.brigadier.arguments.IntegerArgumentType.getInteger
import com.mojang.brigadier.arguments.IntegerArgumentType.integer
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.command.CommandException
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.BlockItem
import net.minecraft.item.Item.Settings
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.TeleportTarget
import net.minecraft.world.World
import net.walksanator.hexdim.blocks.BlockRegistry
import net.walksanator.hexdim.iotas.IotaTypes
import net.walksanator.hexdim.iotas.RoomIota
import net.walksanator.hexdim.patterns.DimPatternRegistry
import net.walksanator.hexdim.util.Rectangle
import net.walksanator.hexdim.util.toRectList
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.random.Random

object HexxyDimensions : ModInitializer {
    const val MOD_ID = "hexdim"
    val logger = LoggerFactory.getLogger("hexxy-dimensions")
    var STORAGE: Optional<HexxyDimStorage> = Optional.empty()

    override fun onInitialize() {
        //HexConfig.ServerConfigAccess.DEFAULT_DIM_TP_DENYLIST.add("hexdim:hexdim")

        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
        logger.info("Hello Fabric world!")

        IotaTypes.registerTypes()
        DimPatternRegistry.registerPatterns()


        Registry.register(Registries.BLOCK, Identifier(MOD_ID, "skybox"), BlockRegistry.SKYBOX)
        Registry.register(Registries.ITEM, Identifier(MOD_ID, "skybox"), BlockItem(BlockRegistry.SKYBOX, Settings()))
        Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            Identifier(MOD_ID, "skybox_entity"),
            BlockRegistry.SKYBOX_ENTITY
        )

        ServerLifecycleEvents.SERVER_STARTED.register { server ->
            STORAGE = Optional.of(HexxyDimStorage.getServerState(server))
        }
        ServerLifecycleEvents.SERVER_STOPPING.register {
            STORAGE.get().markDirty() // we are shutting down to, so we should prepare to save data
        }

        CommandRegistrationCallback.EVENT.register { dispatch, _, _ ->
            run {
                dispatch.register(literal("hexdim")
                    .then(literal("allocate")
                        .then(literal("random")
                            .executes {
                                val w = Random.nextInt(32, 64)
                                val h = Random.nextInt(32, 64)
                                val storage = STORAGE.get()
                                try {
                                    val placed = storage.mallocRoom(Pair(w, h), 10)
                                    if (placed == null) {
                                        it.source.sendError(
                                            Text.literal(
                                                "failed to place rectangle (%s,%s)".format(
                                                    w,
                                                    h
                                                )
                                            )
                                        )
                                    } else {
                                        it.source.sendMessage(
                                            Text.literal(
                                                "placed rectangle (%s,%s)".format(
                                                    w,
                                                    h
                                                )
                                            )
                                        )
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }

                                1
                            }
                        )
                        .then(argument("x", integer(0))
                            .then(argument("y", integer(0))
                                .then(argument("z",integer(0))
                                    .executes {
                                        val w = getInteger(it, "x")
                                        val h = getInteger(it, "z")
                                        val height = getInteger(it,"y")
                                        val storage = STORAGE.get()
                                        val placed = storage.mallocRoom(Pair(w, h), height)
                                            ?: throw CommandException(
                                                Text.literal(
                                                    "failed to place rectangle (%s,%s)".format(
                                                        w,
                                                        h
                                                    )
                                                )
                                            )

                                        1
                                    }
                                )

                            )
                        )
                    )
                    .then(literal("query")
                        .then(argument("index",integer(0))
                            .executes {
                                val idx = getInteger(it,"index")
                                val storage = STORAGE.get()
                                if (idx >= storage.all.size ) {
                                    it.source.sendError(Text.literal("index is out of bounds range 0..%s".format(storage.all.size)))
                                } else {
                                    val room = storage.all[idx]
                                    val rect = room.rect
                                    val rect2 = room.internalToRect()
                                    it.source.sendMessage(Text.literal("Information for room: %s".format(idx)))
                                    it.source.sendMessage(Text.literal("Perimeter: (xywh) %s, %s, %s, %s".format(rect.x,rect.y,rect.w,rect.h)))
                                    it.source.sendMessage(Text.literal("Internal: (xywh) %s, %s, %s, %s".format(rect2.x,rect2.y,rect2.w,rect2.h)))
                                    it.source.sendMessage(Text.literal("InternalHeight: %s".format(room.height)))
                                }

                                1
                            }
                        )
                        .executes {
                            val storage = STORAGE.get()
                            it.source.sendMessage(Text.literal("there are currently %s rooms allocated with %s being open and %s being free".format(
                                storage.all.size,storage.open.size,storage.free.size
                            )))
                            1
                        }
                    )
                    .then(literal("warp")
                        .requires { src -> src.hasPermissionLevel(4) }
                        .then(argument("index",integer(0))
                            .executes {
                                val idx = getInteger(it,"index")
                                val storage = STORAGE.get()
                                if (idx >= storage.all.size ) {
                                    it.source.sendError(Text.literal("index is out of bounds range 0..%s".format(storage.all.size)))
                                } else {

                                    val room = storage.all[idx]
                                    val ent = it.source.entity!!
                                    FabricDimensions.teleport(ent,storage.world, TeleportTarget(
                                        Vec3d(
                                            (room.getX().toDouble() + (room.getW().toDouble()/2)),
                                            room.height.toDouble()/2,
                                            (room.getY().toDouble() + (room.getH().toDouble()/2))
                                        ),
                                        Vec3d.ZERO,
                                        0F,0F
                                    ))
                                }
                                1
                            }
                        )
                    )
                    .then(literal("dump")
                        .then(argument("idx",integer(0))
                            .executes {
                                val idx = getInteger(it,"idx")
                                val storage = STORAGE.get()
                                if (idx >= storage.all.size ) {
                                    it.source.sendError(Text.literal("index is out of bounds range 0..%s".format(storage.all.size)))
                                } else {
                                    val plr = (it.source.entity!! as PlayerEntity)
                                    val hand = plr.mainHandStack;
                                    if (hand.item is IotaHolderItem) {
                                        (hand.item as IotaHolderItem).writeDatum(
                                            hand,
                                            RoomIota(Pair(idx,storage.all[idx].key!!))
                                        )
                                    } else {
                                        val ohand = plr.offHandStack
                                        if (ohand.item is IotaHolderItem) {
                                            (ohand.item as IotaHolderItem).writeDatum(
                                                hand,
                                                RoomIota(Pair(idx,storage.all[idx].key!!))
                                            )
                                        }
                                    }
                                }
                                1
                            }
                        )
                        .executes {
                            val storage = STORAGE.get()
                            val world = storage.world as ServerWorld
                            for (rect in toRectList(storage.all)) {
                                fillAreaWithBlock(world,Pair(rect.x,rect.y),Pair(rect.x+rect.w,rect.y+rect.h),0,Blocks.RED_STAINED_GLASS)
                                outlineRectangle(world,rect,1,Blocks.BLUE_STAINED_GLASS)
                            }
                            for (room in storage.all) {
                                outlineRectangle(world,room.internalToRect(),1,Blocks.GREEN_STAINED_GLASS)
                            }

                            1
                        }
                    )
                )
            }
        }
    }
}

fun fillAreaWithBlock(world: World, start: Pair<Int, Int>, end: Pair<Int, Int>, y: Int, block: Block) {
    for (x in start.first until end.first) {
        for (z in start.second until end.second) {
            world.setBlockState(BlockPos(x, y, z), block.defaultState, 3) // Use 3 for block update
        }
    }
}

fun outlineRectangle(world: World, rectangle: Rectangle, yLevel: Int, block: Block) {
    for (x in rectangle.x until rectangle.x + rectangle.w) {
        world.setBlockState(BlockPos(x, yLevel, rectangle.y), block.defaultState, 3)
        world.setBlockState(BlockPos(x, yLevel, rectangle.y + rectangle.h - 1), block.defaultState, 3)
    }

    for (z in rectangle.y until rectangle.y + rectangle.h) {
        world.setBlockState(BlockPos(rectangle.x, yLevel, z), block.defaultState, 3)
        world.setBlockState(BlockPos(rectangle.x + rectangle.w - 1, yLevel, z), block.defaultState, 3)
    }
}

fun findRectangle(pointX: Int, pointY: Int, rectangles: List<Rectangle>): Rectangle {
    for (rectangle in rectangles) {
        if (pointX in rectangle.x until (rectangle.x + rectangle.w) &&
            pointY in rectangle.y until (rectangle.y + rectangle.h)
        ) {
            return rectangle
        }
    }
    return rectangles[0]
}