package net.walksanator.hexdim

data class HexxyDimConfig(val x_limit: Int, val y_limit: Int, val z_limit: Int, val BlocksPerTick: Int) {
    constructor() : this(100,50,100,1)
}
