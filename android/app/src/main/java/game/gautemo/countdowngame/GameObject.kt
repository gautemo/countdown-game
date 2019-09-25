package game.gautemo.countdowngame

import kotlin.random.Random

data class GameObject(
    val name: String? = null,
    val creator: String? = null,
    val created: Long? = null,
    val players: MutableMap<String, String> = mutableMapOf(),
    val playerGuesses: MutableMap<String, Long> = mutableMapOf(),
    var state: String = "created",
    var time: Int = Random.nextInt(10, 20)
)