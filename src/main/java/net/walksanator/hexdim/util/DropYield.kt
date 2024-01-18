package net.walksanator.hexdim.util

class DropYield<T>(val values: Iterator<T>,val drop: Int) : Iterator<T> {
    var skipped = 0;

    init {
        if (drop  < 0) throw IllegalArgumentException("drop should be zero or positive")
    }
    override fun hasNext(): Boolean = values.hasNext()

    override fun next(): T {
        while (skipped != drop) {
            values.next()
            skipped += 1
        }
        return values.next()
    }

}