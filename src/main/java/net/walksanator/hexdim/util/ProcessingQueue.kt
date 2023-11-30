package net.walksanator.hexdim.util

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.function.Consumer

class ProcessingQueue<T>(private val consumer: Consumer<T>, private val sleep: Long) {
    private val queue = ConcurrentLinkedQueue<T>()
    @OptIn(DelicateCoroutinesApi::class)
    private val job = GlobalScope.launch {
        while (true) {
            if (queue.isNotEmpty()) {
                val room = queue.poll()
                consumer.accept(room)
            } else {Thread.sleep(sleep)}
        }
    }

    fun enqueue(value: T) = queue.add(value)
    fun queue(): Array<T> = (queue.toArray() as Array<T>)
    fun enqueue(values: Collection<T>) = queue.addAll(values)
    fun start(): Boolean = job.start()
    fun restart() {stop("restart");start()}
    fun stop(reason: String) = job.cancel(CancellationException(reason))
    fun running(): Boolean = job.isActive
}