package game.gautemo.countdowngame

import android.content.Context
import android.content.SharedPreferences
import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private var name = "Random Human"
    private var uuid = "123"
    private val gameViews = mutableMapOf<String, TextView>()
    private var sharedPreferences: SharedPreferences? = null
    private val firestoreHandler = FirestoreHandler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences("data", Context.MODE_PRIVATE)

        initPlayer()
        findViewById<Button>(R.id.random_name).setOnClickListener { changeName()}

        findViewById<Button>(R.id.create_game).setOnClickListener { createGame() }

        initGamesListener()
    }

    private fun initPlayer(){
        name = sharedPreferences?.getString("username", "Random Human") ?: "Random Human"
        findViewById<TextView>(R.id.username).text = name

        if(sharedPreferences?.contains("uuid") == true){
            uuid = sharedPreferences?.getString("uuid", "321") ?: "321"
        }else{
            uuid = UUID.randomUUID().toString()
            sharedPreferences?.edit()?.putString("uuid", uuid)?.apply()
        }
    }

    private fun changeName(){
        name = possibleNames[Random.nextInt(possibleNames.size)]
        findViewById<TextView>(R.id.username).text = name
        sharedPreferences?.edit()?.putString("username", name)?.apply()
    }

    private fun createGame(){
        val now = Calendar.getInstance()
        val gameName = "$name's game ${now.get(Calendar.HOUR)}:" +
                "${now.get(Calendar.MINUTE)}:${now.get(Calendar.SECOND)}"
        val game = GameObject(gameName, uuid, now.timeInMillis, mutableMapOf(Pair(uuid, name)))
        firestoreHandler.saveGame(game, { goToLobby(it.id, true) }, { toast("Could not create game, please try again later")})
    }

    private fun goToLobby(id: String, creator: Boolean){
        supportFragmentManager.inTransaction {
            val lobby = LobbyFragment()
            lobby.listenLobby(id, creator)
            add(android.R.id.content, lobby)
        }
    }

    fun toast(msg: String, length: Int = Toast.LENGTH_SHORT){
        Toast.makeText(baseContext, msg, length).show()
    }

    private fun initGamesListener(){
        val added: (String, String) -> Unit = { id, name ->
            val txtView = TextView(this)
            txtView.text = name
            gameViews[id] = txtView
            findViewById<LinearLayout>(R.id.games).addView(txtView)
        }

        val removed: (String) -> Unit = { id ->
            val v = gameViews.remove(id)
            findViewById<LinearLayout>(R.id.games).removeView(v)
        }

        firestoreHandler.listenGames(added, removed, { toast("Could not get games, please restart the app") })
    }

    fun beginGame(id: String){
        supportFragmentManager.popBackStack()
        supportFragmentManager.inTransaction {
            val game = GameFragment()
            game.initRef(id, uuid)
            add(android.R.id.content, game)
        }
    }

    inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
        beginTransaction().addToBackStack(null).func().commit()
    }

    companion object{
        val possibleNames = arrayOf("Aspect","Kraken","Bender","Lynch","Big Papa","Mad Dog","Bowser","O'Doyle","Bruise","Psycho","Cannon","Ranger","Clink","Ratchet","Cobra","Reaper","Colt","Rigs","Crank","Ripley","Creep","Roadkill","Daemon","Ronin","Decay","Rubble","Diablo","Sasquatch","Doom","Scar","Dracula","Shiver","Dragon","Skinner","Fender","Skull Crusher","Fester","Slasher","Fisheye","Steelshot","Flack","Surge","Gargoyle","Sythe","Grave","Trip","Gunner","Trooper","Hash","Tweek","Hashtag","Vein","Indominus","Void","Ironclad","Wardon","Killer","Wraith","Knuckles","Zero")
    }
}
