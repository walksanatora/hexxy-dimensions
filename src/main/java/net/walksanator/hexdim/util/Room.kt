package net.walksanator.hexdim.util

import net.walksanator.hexdim.HexxyDimStorage
import kotlin.random.Random

class Room(val rect: Rectangle,val height: Int, var key: Int?) {
    init {
        if (key == null) {
            key = Random.nextInt()
        }
    }

    fun keyCheck(other: Int): Boolean = other == key
    fun toIntArray(): IntArray {
        return intArrayOf(rect.x,rect.y,rect.w,rect.h,height,key!!)
    }

    fun getX(): Int {
        return rect.x + (HexxyDimStorage.xPad/2)
    }

    fun getY(): Int {
        return rect.y + (HexxyDimStorage.yPad/2)
    }

    fun getW(): Int {
        return rect.w - HexxyDimStorage.xPad
    }

    fun getH(): Int {
        return rect.h - HexxyDimStorage.yPad
    }

    fun internalToRect(): Rectangle = Rectangle(
        getX(),getY(),getW(),getH()
    )

}

fun toRectList(rooms: List<Room>): List<Rectangle> {
    return rooms.map { it.rect }
}