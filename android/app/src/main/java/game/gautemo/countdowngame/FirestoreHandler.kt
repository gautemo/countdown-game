package game.gautemo.countdowngame

import android.icu.util.Calendar
import android.util.Log
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class FirestoreHandler{
    private val db = FirebaseFirestore.getInstance()
    private val games = db.collection("games")

    fun saveGame(game: GameObject, callback: (doc: DocumentReference) -> Unit, error: (e: Exception) -> Unit){
        games.add(game)
            .addOnSuccessListener(callback)
            .addOnFailureListener(error)
    }

    fun getGame(id: String, retGame: (game: GameObject) -> Unit){
        games.document(id).get().addOnSuccessListener {
            val game = it?.toObject(GameObject::class.java)
            game?.let(retGame)
        }
    }

    fun listenGames(added: (id: String, name: String) -> Unit, removed: (id: String) -> Unit, error: (e: Exception) -> Unit){
        val lastHour = Calendar.getInstance().timeInMillis - (1000*60*60)
        games
            .whereEqualTo("state", "created")
            .whereGreaterThan("created", lastHour)
            .addSnapshotListener { snapshots, exception ->
                if(exception != null){
                    //Toast.makeText(baseContext, "Could not get games, please restart the app", Toast.LENGTH_LONG).show()
                    error(exception)
                    return@addSnapshotListener
                }
                for(doc in snapshots!!.documentChanges){
                    when(doc.type){
                        DocumentChange.Type.ADDED -> {
                            val game = doc.document.toObject(GameObject::class.java)
                            added(doc.document.id, game.name?: "Unknown game")
                        }
                        DocumentChange.Type.REMOVED -> {
                            removed(doc.document.id)
                        }
                        DocumentChange.Type.MODIFIED -> Log.d("Firestore", "Modified: ${doc.document.data}")
                    }
                }
            }
    }

    fun listenGame(id: String, started: () -> Unit, updatePlayers: (game: MutableMap<String, String>) -> Unit, error: (e: Exception) -> Unit){
        val docRef = games.document(id)
        docRef.addSnapshotListener { snapshot, ex ->
            if (ex != null) {
                error(ex)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val game = snapshot.toObject(GameObject::class.java)
                game?.let {
                    if(it.state == "started"){
                        started()
                    }else{
                        updatePlayers(it.players)
                    }
                }
            } else {
                Log.d("Lobby", "Current data: null")
            }
        }
    }

    fun startGame(id: String){
        db.collection("games").document(id).update("state", "started")
    }

    fun stopTime(gameId: String, time: Long, uuid: String){
        db.runTransaction { transaction ->
            val snapshot = transaction.get(games.document(gameId))
            val game = snapshot.toObject(GameObject::class.java)
            game?.let {
                it.playerGuesses[uuid] = time
                transaction.update(snapshot.reference, "playerGuesses", game.playerGuesses)
            }
        }
    }
}