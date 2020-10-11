package com.github.anvo.sds.logic

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import com.github.anvo.sds.Log
import com.github.anvo.sds.model.FinishTime
import com.github.anvo.sds.model.Game
import com.github.anvo.sds.model.Player
import java.lang.Thread.sleep
import kotlin.concurrent.thread

@ExperimentalUnsignedTypes
class GameUseCase {

    companion object {
        private const val LOADING_TIMEOUT = 15000L
    }

    var games: List<Game> = listOf()
    private var gameListeners: MutableMap<Game,List<Triple<Player, UShort, GameListener>>> = mutableMapOf()
    private var gameStateListener: Set<Pair<UShort, GameStateListener>> = setOf()

    private var playersLoaded: MutableMap<Game, List<Player>> = mutableMapOf()
    private var playersFinished: MutableMap<Game, List<Pair<Player, FinishTime>>> = mutableMapOf()

    private var loadingTimeout: Thread? = null

    interface GameListener {
        fun playerJoined(game: Game, player: Player)

        // Host has started the game. Now waiting for all players to load the track.
        fun gameStarted(game: Game, track: String)

        // All players have loaded - the game is running
        fun gameFinishedLoading(game: Game)

        // All players finished the game
        fun gameFinished(game: Game)

        // A player has finished the game
        fun playerFinished(game: Game, player: Player, time: FinishTime)

        // Menu pause pressed
        fun menuPause()

        // Menu continue pressed
        fun menuContinue()

        // Host has quit the game
        fun gameQuit(game: Game)

        // Player has left the game
        fun playerLeft(game: Game, player: Player, position: Int)
    }

    interface GameStateListener {
        fun gameStateAvailable(game: Game, player: Player, data: ByteArray)
    }

    fun newGame(game: Game) {
        games = games.plus(game)
        Log.game {"New $game"}
    }

    fun joinGame(game: Game, player: Player, connectionId: UShort, listener: GameListener) {
        game.players.add(player)
        Log.game {"$player joined $game"}

        val currentListeners = this.gameListeners[game].orEmpty()
        this.gameListeners[game] = currentListeners.plus(Triple(player, connectionId, listener))

        this.notify(game) { it.playerJoined(game, player) }
    }

    fun startGame(game: Game, track: String) {
        this.playersLoaded[game] = listOf()
        this.playersFinished[game] = listOf()

        Log.game {"Starting race for $game"}

        this.loadingTimeout = thread {
            sleep(LOADING_TIMEOUT)
            if(this.loadingTimeout != null) {
                val playersMissing = game.players.subtract(this.playersLoaded[game].orEmpty())
                Log.game { "Timeout hit for $playersMissing" }
                playersMissing.forEach {
                    this.leaveGame(game, it)
                }
                this.notify(game) { it.gameFinishedLoading(game) }
            }
        }

        this.notify(game) { it.gameStarted(game, track) }
    }

    fun gameLoaded(game: Game, player: Player) {
        val playersLoaded = this.playersLoaded[game].orEmpty().plus(player)
        this.playersLoaded[game] = playersLoaded;

        Log.game {"$player finished loading"}

        if(playersLoaded.size == game.players.size) {
            Log.game {"All players loaded. Starting race."}
            this.loadingTimeout = null
            this.notify(game) { it.gameFinishedLoading(game) }
        } else {
            Log.game {"Waiting for ${game.players.subtract(playersLoaded)}"}
        }
    }

    //Player left
    fun leaveGame(game: Game, player: Player) {
        val playerPosition = game.players.indexOf(player) + 1;
        val hostLeft = game.players.indexOf(player) == 0
        game.players.remove(player)
        Log.game {"$player left $game"}

        // Remove listener
        val listeners = this.gameListeners[game].orEmpty()
        this.gameListeners[game] = listeners.filter { it.first != player }

        if(hostLeft || game.players.isEmpty()) {
            // Remove game
            this.games = this.games - game
            Log.game {"Host has left the game. Quitting $game"}

            this.notify(game) { it.gameQuit(game) }
            this.gameListeners.remove(game)
        } else {
            //Update player list
            this.notify(game) { it.playerLeft(game, player, playerPosition) }
        }
    }

    fun deliverGameState(connectionId: UShort, data: ByteArray) {
        // Get Game
        val game: Game = this.gameListeners.entries.first {
            it.value.any { it.second == connectionId }
        }.key

        // Get Player
        val player = this.gameListeners[game]!!.first { it.second == connectionId }.first

        runBlocking {
            gameListeners[game]!!.forEach {
                if(connectionId != it.second) { // Only notify other people
                    launch {
                        gameStateListener.first { l -> l.first == it.second }.second.gameStateAvailable(
                            game,
                            player,
                            data
                        )
                    }

                }
            }
        }
    }

    fun playerFinished(game: Game, player: Player, time: FinishTime) {

        val playersFinished = this.playersFinished[game].orEmpty().plus(Pair(player, time))
        this.playersFinished[game] = playersFinished;

        Log.game {"$player finished race in ${time.total} seconds"}
        this.notify(game) { it.playerFinished(game, player, time)}

        if(playersFinished.size == game.players.size) {
            Log.game {"All players finished race"}
            this.calculateStats(game, playersFinished)
            this.notify(game) { it.gameFinished(game) }
        } else {
            Log.game {"Waiting finish of ${game.players.subtract(playersFinished)}"}
        }

    }

    fun menuPause(game: Game) {
        Log.game {"Race pause"}
        this.notify(game) {it.menuPause()}
    }

    fun menuContinue(game: Game) {
        Log.game {"Race continue."}
        this.notify(game) {it.menuContinue()}
    }

    fun addGameStateListener(connectionId: UShort, gameStateListener: GameStateListener) {
        this.gameStateListener = this.gameStateListener + Pair(connectionId, gameStateListener)
    }

    fun removeGameStateListener(connectionId: UShort, gameStateListener: GameStateListener) {
        this.gameStateListener = this.gameStateListener - Pair(connectionId, gameStateListener)
    }


    private fun notify(game: Game, f: (it:GameListener) -> Unit) {
        runBlocking {
            gameListeners[game]?.forEach {
                launch {
                    f(it.third)
                }
            }
        }

    }

    // Calculates the stats at the end of the game
    private fun calculateStats(game: Game, finishTimes: List<Pair<Player, FinishTime>>) {
        finishTimes.sortedByDescending { it.second.total }.forEachIndexed { place, p ->
            val player = p.first
            val time = p.second
            if (!game.stats.containsKey(player)) {
                game.stats[player] = Game.StatsEntry(player, FinishTime(0f,0f,0f),0,0)
            }
            val stats = game.stats[player]!! // Has been created previously
            stats.time = time
            stats.points = when(place) {
                0 -> 10 1 -> 6 2 -> 4 3 -> 3 4 -> 2 5 -> 1 else -> 0
            }
            stats.totalPoints += stats.points
            Log.game { "${place +1}. ${stats.time.total}\t ${stats.points}\t ${stats.totalPoints}\t ${stats.player.name}" }
        }
    }
}