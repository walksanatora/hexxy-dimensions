package net.walksanator.hexdim.util

import net.walksanator.hexdim.HexxyDimStorage
import net.walksanator.hexdim.casting.mishap.MishapInvalidRoom
import kotlin.random.Random

class Room(val rect: Rectangle,val height: Int, var key: Int?, var isDone: Boolean) {
    companion object {
        const val ARGC: Int = 7 //this is the number of args
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
        part[6] > 0
    )

    init {
        if (key == null) {
            key = Random.nextInt()
        }
    }

    fun keyCheck(other: Int) {
        if (!isDone) {throw MishapInvalidRoom(this,false)}
        if (other != key) {throw MishapInvalidRoom(this,true)}
    }
    fun keyCheckNoCarveCheck(other: Int) {
        if (other != key) {throw MishapInvalidRoom(this,true)}
    }

    fun toIntArray(): IntArray {
        val doneInt = if (isDone) {1} else {0}
        return intArrayOf(rect.x,rect.y,rect.w,rect.h,height,key!!, doneInt)
    }

    fun getX(): Int {
        return rect.x + (HexxyDimStorage.X_PADDING/2)
    }

    fun getY(): Int {
        return rect.y + (HexxyDimStorage.Y_PADDING/2)
    }

    fun getW(): Int {
        return rect.w - HexxyDimStorage.X_PADDING
    }

    fun getH(): Int {
        return rect.h - HexxyDimStorage.Y_PADDING
    }

    fun internalToRect(): Rectangle = Rectangle(
        getX(),getY(),getW(),getH()
    )

}

fun toRectList(rooms: List<Room>): List<Rectangle> {
    return rooms.map { it.rect }
}