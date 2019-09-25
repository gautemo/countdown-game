package game.gautemo.countdowngame


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class LobbyFragment : Fragment() {
    private val firestoreHandler = FirestoreHandler()
    private var playersView: LinearLayout? = null
    private var gameId = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val addView = inflater.inflate(R.layout.fragment_lobby, container, false)
        addView.findViewById<Button>(R.id.begin_game).setOnClickListener {
            firestoreHandler.startGame(gameId)
        }
        playersView = addView.findViewById(R.id.players)
        return addView
    }

    fun listenLobby(id: String, creator: Boolean){
        gameId = id
        val started: () -> Unit = {
            if(isAdded) {
                (activity as MainActivity).beginGame(id)
            }
        }
        val updatePlayers: (MutableMap<String, String>) -> Unit = {
            if(!isDetached) {
                playersView?.removeAllViews()
                for (player in it) {
                    val tv = TextView(context)
                    tv.text = player.value
                    playersView?.addView(tv)
                }
            }
        }

        firestoreHandler.listenGame(id, started, updatePlayers, { (activity as MainActivity).toast("Could not join game, retry")})
    }


}
