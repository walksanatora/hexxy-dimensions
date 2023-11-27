package net.walksanator.hexdim

import com.mojang.brigadier.arguments.IntegerArgumentType.getInteger
import com.mojang.brigadier.arguments.IntegerArgumentType.integer
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.command.CommandException
import net.minecraft.item.BlockItem
import net.minecraft.item.Item.Settings
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.server.command.CommandManager.argument
import net.minecraft.server.command.CommandManager.literal
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.TeleportTarget
import net.minecraft.world.World
import net.walksanator.hexdim.blocks.BlockRegistry
import org.slf4j.LoggerFactory
import java.lang.Exception
import java.util.Optional
import kotlin.random.Random

object HexxyDimensions : ModInitializer {
    private const val MOD_ID = "hexdim"
    val logger = LoggerFactory.getLogger("hexxy-dimensions")
    var STORAGE: Optional<HexxyDimStorage> = Optional.empty()

    override fun onInitialize() {
        //HexConfig.ServerConfigAccess.DEFAULT_DIM_TP_DENYLIST.add("hexdim:hexdim")

        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.
        logger.info("Hello Fabric world!")

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
                                    it.source.sendMessage(Text.literal("room %s has a total w/h of %s %s\nis placed at %s,%s (real corner %s,%s) "
                                        .format(idx,room.w,room.h,room.x,room.y,room.x+32,room.y+32)
                                    ))
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
                                            (room.x.toDouble() + room.w - 32 + room.x)/2,
                                            room.height.toDouble()/2,
                                            (room.y.toDouble() + room.h - 32 + room.y)/2
                                        ),
                                        Vec3d.ZERO,
                                        0F,0F
                                    ))
                                }
                                1
                            }
                        )
                    )
                )

                dispatch.register(literal("render")
                    .then(literal("bounds").executes {
                        val storage = STORAGE.get()
                        val targetRect =
                            findRectangle(it.source.position.x.toInt(), it.source.position.z.toInt(), storage.all)
                        val x = targetRect.x
                        val y = targetRect.y
                        val h = targetRect.h
                        val w = targetRect.w
                        val target = Pair(32, 32)
                        for (side in enumValues<RectSideOpen>()) {
                            val xy = when (side) {
                                RectSideOpen.Up -> Pair(x, y - target.second)
                                RectSideOpen.Down -> Pair(x, y + h)
                                RectSideOpen.Left -> Pair(x - target.first, y)
                                RectSideOpen.Right -> Pair(x + w, y)
                            }
                            val newRect = Rectangle(xy.first, xy.second, target.first, target.second, 0)
                            if (newRect.isOverlap(storage.all)) {
                                outlineRectangle(it.source.world, newRect, -57, Blocks.BLACK_WOOL)
                            } else {
                                outlineRectangle(it.source.world, newRect, -57, Blocks.GRAY_WOOL)
                            }
                        }
                        it.source.sendMessage(Text.literal("the room says it has %s open sides".format(targetRect.openSides.size)))
                        1
                    })
                    .executes {
                        val storage = STORAGE.get()
                        val world = it.source.world
                        for (rect in storage.all) {
                            fillAreaWithBlock(
                                world,
                                Pair(rect.x, rect.y),
                                Pair(rect.x + rect.w, rect.y + rect.h),
                                -60,
                                Blocks.RED_WOOL
                            )
                            outlineRectangle(world, rect, -59, Blocks.BLUE_WOOL)
                        }
                        for (closed in storage.all.filter { rect -> !storage.open.contains(rect) }) {
                            it.source.sendMessage(
                                Text.literal(
                                    "closed square at (%s,%s)".format(
                                        closed.x,
                                        closed.y
                                    )
                                )
                            )
                            outlineRectangle(world, closed, -58, Blocks.GLASS)
                        }
                        1
                    })
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