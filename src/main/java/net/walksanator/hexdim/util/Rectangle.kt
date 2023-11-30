package net.walksanator.hexdim.util

import net.walksanator.hexdim.HexxyDimStorage
import kotlin.math.pow
import kotlin.math.sqrt

class Rectangle(var x: Int, var y:Int, var w:Int, var h:Int) {
    val openSides: MutableList<RectSides> = mutableListOf(
        RectSides.Up,
        RectSides.Down,
        RectSides.Left,
        RectSides.Right
    )

    fun isOverlap(other: Rectangle): Boolean {
        return if (x > other.x) {
            other.x + other.w > x
        } else {
            x + w > other.x
        } && if (y > other.y) {
            other.y + other.h > y
        } else {
            y + h > other.y
        }
    }

    fun isOverlap(others: List<Rectangle>): Boolean {
        for (rect in others) {
            if (isOverlap(rect)) { return true }
        }
        return false
    }

    fun getPossibleSections(target: Pair<Int,Int>, all: List<Rectangle>, minimum: Pair<Int,Int>): PossiblePointsCheckReturn {
        val locations = ArrayList<Pair<Int,Int>>()
        val toClose = ArrayList<RectSides>()
        for (side in openSides) {
            val xy = when (side) {
                RectSides.Up -> Pair(x,y-target.second)
                RectSides.Down -> Pair(x,y+h)
                RectSides.Left -> Pair(x-target.first,y)
                RectSides.Right -> Pair(x+w,y)
            }
            val newRect = Rectangle(xy.first,xy.second,target.first,target.second)
            if (!newRect.isOverlap(all)) {
                locations.add(xy)
            }
            //gonna do the pos-check for possible optimisations
            val testPos = when (side) {
                RectSides.Right, RectSides.Down -> xy //same equation as above so why recalc?
                RectSides.Up -> Pair(x,y-minimum.second)
                RectSides.Left -> Pair(x-minimum.first,y)
            }
            val testRect = Rectangle(testPos.first,testPos.second,minimum.first,minimum.second)
            if (testRect.isOverlap(all)) {
                toClose.add(side) // there is not enough space for even the "minimum" so mark it as dead
            }

        }
        for (side in toClose) {
            openSides.remove(side)
        }
        return PossiblePointsCheckReturn(locations,openSides.isEmpty())
    }
}

fun addRectangle(wh: Pair<Int,Int>, height: Int, storage: HexxyDimStorage, target: Pair<Int,Int>, minimum: Pair<Int, Int>): Boolean {
    val all = toRectList(storage.all)
    val open = storage.open
    if (all.isEmpty()) {//this is the first rectangle. it gets 0,0 no contest
        val newRect = Rectangle(0,0,wh.first,wh.second)
        val room = Room(newRect,height,null,false)
        storage.insertRoom(room)
        return true
    }
    val possibilities = ArrayList<Pair<Int,Int>>()
    val toClose: ArrayList<Rectangle> = ArrayList()
    for (rect in toRectList(open)) {
        val poss = rect.getPossibleSections(wh,all,minimum)
        if (poss.remove) {
            toClose.add(rect)
        } else {
            possibilities.addAll(poss.points)
        }
    }
    storage.closeRoomsBulk(toClose)
    if (possibilities.isEmpty()) {
        return false
    }
    possibilities.sortBy { sqrt(
        (it.first - target.first).toDouble().pow(2) +
                (it.second - target.second).toDouble().pow(2)
    ) }
    val newPos = possibilities.first()
    val newRect = Rectangle(newPos.first,newPos.second,wh.first,wh.second)
    storage.insertRoom(
        Room(
            newRect,
            height,
            null,
            false
        )
    )
    return true
}