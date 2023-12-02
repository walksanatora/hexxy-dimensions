package net.walksanator.hexdim

import at.petrak.hexcasting.api.item.IotaHolderItem
import com.mojang.brigadier.arguments.IntegerArgumentType.getInteger
import com.mojang.brigadier.arguments.IntegerArgumentType.integer
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.minecraft.block.Block
import net.minecraft.command.CommandException
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.BlockItem
import net.minecraft.item.Item.Settings
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.registry.Registry
import net.minecraft.world.TeleportTarget
import net.minecraft.world.World
import net.walksanator.hexdim.blocks.BlockRegistry
import net.walksanator.hexdim.iotas.IotaTypes
import net.walksanator.hexdim.iotas.RoomIota
import net.walksanator.hexdim.util.Rectangle
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
        Registry.register(Registry.BLOCK, Identifier(MOD_ID, "skybox"), BlockRegistry.SKYBOX)
        Registry.register(Registry.ITEM, Identifier(MOD_ID, "skybox"), BlockItem(BlockRegistry.SKYBOX, Settings()))

        ServerLifecycleEvents.SERVER_STARTED.register { server ->
            STORAGE = Optional.of(HexxyDimStorage.getServerState(server))
        }
        ServerLifecycleEvents.SERVER_STOPPING.register {
            if (STORAGE.isPresent) {
                STORAGE.get().markDirty() // we are shutting down to, so we should prepare to save data
            }
        }

        CommandRegistrationCallback.EVENT.register { dispatch, _, _ ->
            run {
                dispatch.register(literal("hexdim")
                    .then(literal("allocate")
                        .requires { src -> src.hasPermissionLevel(4) }
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
                                .then(argument("z", integer(0))
                                    .executes {
                                        val w = getInteger(it, "x")
                                        val h = getInteger(it, "z")
                                        val height = getInteger(it, "y")
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
                        .then(literal("carveQueue")
                            .executes {
                                val storage = STORAGE.get()
                                val indexes = storage.getCarveQueueIdxs()
                                it.source.sendMessage(
                                    Text.literal(
                                        "Rooms that need carving: %s\nx16: %s\nx32: %s\nx64: %s\nx128: %s".format(
                                            storage.all.filter {room -> !room.isDone} .map { room -> storage.all.indexOf(room) }.toString(),
                                            indexes[0],
                                            indexes[1],
                                            indexes[2],
                                            indexes[3],
                                        )
                                    )
                                )
                                1
                            }
                        )
                        .then(argument("index", integer(0))
                            .executes {
                                val idx = getInteger(it, "index")
                                val storage = STORAGE.get()
                                if (idx >= storage.all.size) {
                                    it.source.sendError(
                                        Text.literal(
                                            "index is out of bounds range 0..%s".format(
                                                storage.all.size
                                            )
                                        )
                                    )
                                } else {
                                    val room = storage.all[idx]
                                    val rect = room.rect
                                    val rect2 = room.internalToRect()
                                    it.source.sendMessage(Text.literal("Information for room: %s".format(idx)))
                                    it.source.sendMessage(
                                        Text.literal(
                                            "Perimeter: (xywh) %s, %s, %s, %s".format(
                                                rect.x,
                                                rect.y,
                                                rect.w,
                                                rect.h
                                            )
                                        )
                                    )
                                    it.source.sendMessage(
                                        Text.literal(
                                            "Room Size: (xyz) %s, %s, %s,".format(
                                                room.getW(),
                                                room.height,
                                                room.getH()
                                            )
                                        )
                                    )
                                    it.source.sendMessage(Text.literal("Carved?: %s".format(room.isDone)))
                                }

                                1
                            }
                        )
                        .executes {
                            val storage = STORAGE.get()
                            it.source.sendMessage(Text.literal("Room Stats,\n allocated: %s\n open: %s\n free: %s\n toCarve: %s".format(
                                storage.all.size, storage.open.size, storage.free.size,
                                storage.all.filter { room -> !room.isDone }.size
                            )))
                            1
                        }
                    )
                    .then(literal("warp")
                        .requires { src -> src.hasPermissionLevel(4) }
                        .then(argument("index", integer(0))
                            .executes {
                                val idx = getInteger(it, "index")
                                val storage = STORAGE.get()
                                if (idx >= storage.all.size) {
                                    it.source.sendError(
                                        Text.literal(
                                            "index is out of bounds range 0..%s".format(
                                                storage.all.size
                                            )
                                        )
                                    )
                                } else {
                                    val room = storage.all[idx]
                                    val ent = it.source.entity!!
                                    FabricDimensions.teleport(
                                        ent, storage.world, TeleportTarget(
                                            Vec3d(
                                                room.getX().toDouble() + 0.5,
                                                0.0,
                                                room.getY().toDouble() + 0.5
                                            ),
                                            Vec3d.ZERO,
                                            0F, 0F
                                        )
                                    )
                                }
                                1
                            }
                        )
                    )
                    .then(literal("dump")
                        .requires { src -> src.hasPermissionLevel(4) }
                        .then(argument("idx", integer(0))
                            .executes {
                                val idx = getInteger(it, "idx")
                                val storage = STORAGE.get()
                                if (idx >= storage.all.size) {
                                    it.source.sendError(
                                        Text.literal(
                                            "index is out of bounds range 0..%s".format(
                                                storage.all.size
                                            )
                                        )
                                    )
                                } else {
                                    val plr = (it.source.entity!! as PlayerEntity)
                                    val hand = plr.mainHandStack;
                                    if (hand.item is IotaHolderItem) {
                                        (hand.item as IotaHolderItem).writeDatum(
                                            hand,
                                            RoomIota(Pair(idx, storage.all[idx].key!!))
                                        )
                                    } else {
                                        val ohand = plr.offHandStack
                                        if (ohand.item is IotaHolderItem) {
                                            (ohand.item as IotaHolderItem).writeDatum(
                                                hand,
                                                RoomIota(Pair(idx, storage.all[idx].key!!))
                                            )
                                        }
                                    }
                                }
                                1
                            }
                        )
                    )
                    .then(literal("queue")
                        .requires { src -> src.hasPermissionLevel(4) }
                        .then(literal("restart")
                            .executes {
                                val storage = STORAGE.get()
                                storage.restartQueueJobs()
                                1
                            }
                        )
                        .then(literal("recarve")
                            .requires { src -> src.hasPermissionLevel(4) }
                            .then(argument("index", integer(0))
                                .executes {
                                    try {
                                        val idx = getInteger(it, "index")
                                        val storage = STORAGE.get()
                                        if (idx >= storage.all.size) {
                                            it.source.sendError(
                                                Text.literal(
                                                    "index is out of bounds range 0..%s".format(
                                                        storage.all.size
                                                    )
                                                )
                                            )
                                        } else {
                                            val room = storage.all[idx]
                                            room.isDone = false
                                            storage.enqueRoomCarving(room)
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                    1
                                }
                            )
                        )
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