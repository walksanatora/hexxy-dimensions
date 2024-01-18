package net.walksanator.hexdim.util

import net.minecraft.util.math.BlockPos
import net.walksanator.hexdim.HexxyDimStorage
import net.walksanator.hexdim.casting.mishap.MishapInvalidRoom
import kotlin.math.max
import kotlin.random.Random

class Room(val rect: Rectangle, val height: Int, var key: Int?, var isDone: Boolean, var blocksCarved: Int) {
    companion object {
        val argc: Int = 7 //this is the number of args
    }

    constructor(part: IntArray) : this(
        Rectangle(
            part[0],
            part[1],
            part[2],
            part[3]
        ),
        part[4],
        part[5],
        part[6] < 0,
        part[6]
    )

    init {
        if (key == null) {
            key = Random.nextInt()
        }
    }

    fun keyCheck(other: Int) {
        if (!isDone) {
            throw MishapInvalidRoom(this, false)
        }
        if (other != key) {
            throw MishapInvalidRoom(this, true)
        }
    }

    fun keyCheckNoCarveCheck(other: Int) {
        if (other != key) {
            throw MishapInvalidRoom(this, true)
        }
    }

    fun toIntArray(): IntArray {
        return intArrayOf(
            rect.x, rect.y, rect.w, rect.h, height, key!!, if (isDone) {
                -1
            } else {
                blocksCarved
            }
        )
    }

    fun getX(): Int {
        return rect.x + (HexxyDimStorage.X_PADDING / 2)
    }

    fun getY(): Int {
        return rect.y + (HexxyDimStorage.Y_PADDING / 2)
    }

    fun getW(): Int {
        return rect.w - HexxyDimStorage.X_PADDING
    }

    fun getH(): Int {
        return rect.h - HexxyDimStorage.Y_PADDING
    }

    fun stream(): Iterator<BlockPos> {
        val iter: MutableList<BlockPos> = mutableListOf()
        BlockPos.iterate(
            getX(),0,getY(),
             getX()+max(getW()-1,0),max(height-1,0),getY()+ max(getH()-1,0)
        ).forEach { iter.add(it.up(0)) }
        return iter.drop(blocksCarved).iterator()
    }

}

fun toRectList(rooms: List<Room>): List<Rectangle> {
    return rooms.map { it.rect }
}