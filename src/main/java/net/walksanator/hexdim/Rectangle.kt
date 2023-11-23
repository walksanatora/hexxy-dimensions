package net.walksanator.hexdim

import kotlin.math.pow
import kotlin.math.sqrt

class Rectangle(var x: Int, var y:Int, var w:Int, var h:Int) {
    val openSides: MutableList<RectSideOpen> = mutableListOf(RectSideOpen.Up,RectSideOpen.Down,RectSideOpen.Left,RectSideOpen.Right)

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

    fun getPossibleSections(target: Pair<Int,Int>, all: List<Rectangle>, minimum: Pair<Int,Int>): PossiblePointsCheck {
        val locations = ArrayList<Pair<Int,Int>>()
        val toClose = ArrayList<RectSideOpen>()
        for (side in openSides) {
            val xy = when (side) {
                RectSideOpen.Up -> Pair(x,y-target.second)
                RectSideOpen.Down -> Pair(x,y+h)
                RectSideOpen.Left -> Pair(x-target.first,y)
                RectSideOpen.Right -> Pair(x+w,y)
            }
            val newRect = Rectangle(xy.first,xy.second,target.first,target.second)
            if (!newRect.isOverlap(all)) {
                locations.add(xy)
            }
            //gonna do the pos-check for possible optimisations
            val testPos = when (side) {
                RectSideOpen.Right, RectSideOpen.Down -> xy //same equation as above so why recalc?
                RectSideOpen.Up -> Pair(x,y-minimum.second)
                RectSideOpen.Left -> Pair(x-minimum.first,y)
            }
            val testRect = Rectangle(testPos.first,testPos.second,minimum.first,minimum.second)
            if (testRect.isOverlap(all)) {
                toClose.add(side) // there is not enough space for even the "minimum" so mark it as dead
            }

        }
        for (side in toClose) {
            openSides.remove(side)
            println("removing side %s of rectangle %s, %s".format(side,x,y))
        }
        return PossiblePointsCheck(locations,openSides.isEmpty())
    }
}

fun addRectangle(wh: Pair<Int,Int>, open: MutableList<Rectangle>, all:MutableList<Rectangle>, target: Pair<Int,Int>, minimum: Pair<Int, Int>): Boolean {
    if (all.isEmpty()) {//this is the first rectangle. it gets 0,0 no contest
        val newRect = Rectangle(0,0,wh.first,wh.second)
        all.add(newRect)
        open.add(newRect)
        return true
    }
    val possibilities = ArrayList<Pair<Int,Int>>()
    for (rect in open) {
        val poss = rect.getPossibleSections(wh,all,minimum)
        if (poss.remove) {
            open.remove(rect)
        } else {
            possibilities.addAll(poss.points)
        }
    }
    if (possibilities.isEmpty()) {
        return false
    }
    possibilities.sortBy { sqrt(
        (it.first - target.first).toDouble().pow(2) +
                (it.second - target.second).toDouble().pow(2)
    ) }
    val newPos = possibilities.first()
    val newRect = Rectangle(newPos.first,newPos.second,wh.first,wh.second)
    all.add(newRect)
    open.add(newRect)
    return true
}