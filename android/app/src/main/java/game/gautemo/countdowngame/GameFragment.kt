package game.gautemo.countdowngame


import android.icu.util.Calendar
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.math.abs

class GameFragment : Fragment() {
    private val firestoreHandler = FirestoreHandler()
    private var gameId = ""
    private var game: GameObject? = null
    var started: Long = -1
    var uuid: String = "123"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val addView = inflater.inflate(R.layout.fragment_game, container, false)

        val getGame: (game: GameObject) -> Unit = {
            game = it
            addView.findViewById<TextView>(R.id.count_number).text = it.time.toString()
        }

        firestoreHandler.getGame(gameId, getGame)

        started = Calendar.getInstance().timeInMillis

        addView.findViewById<Button>(R.id.done_button).setOnClickListener { stopTimer() }

        return addView
    }

    private fun stopTimer(){
        game?.let{
            val guess = Calendar.getInstance().timeInMillis - started
            val diff = abs(guess - (it.time * 1000))
            Toast.makeText(context, "guess: $diff", Toast.LENGTH_SHORT).show()
            firestoreHandler.stopTime(gameId, diff, uuid)
        }
    }

    fun initRef(id: String, userId: String){
        gameId = id
        uuid = userId
    }

}
