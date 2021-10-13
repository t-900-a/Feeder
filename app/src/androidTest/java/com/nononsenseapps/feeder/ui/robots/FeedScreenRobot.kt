package com.nononsenseapps.feeder.ui.robots

fun <R> feedScreen(block: FeedScreenRobot.() -> R): R {
    return FeedScreenRobot().block()
}

class FeedScreenRobot: AndroidRobot() {
    fun openOverflowMenu(): FeedScreenMenuRobot {
        TODO("Not yet implemented")
    }
}
