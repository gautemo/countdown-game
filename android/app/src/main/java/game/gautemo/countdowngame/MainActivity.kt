package game.gautemo.countdowngame

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    var name = "Random Human"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initName()
        findViewById<Button>(R.id.random_name).setOnClickListener {
            changeName()
        }
    }

    private fun initName(){
        name = getSharedPreferences("data", Context.MODE_PRIVATE).getString("username", "Random Human") ?: "Random Human"
        findViewById<TextView>(R.id.username).text = name
    }

    private fun changeName(){
        name = possibleNames[Random.nextInt(possibleNames.size)]
        findViewById<TextView>(R.id.username).text = name
        getSharedPreferences("data", Context.MODE_PRIVATE).edit().putString("username", name).apply()
    }

    companion object{
        val possibleNames = arrayOf("Aspect","Kraken","Bender","Lynch","Big Papa","Mad Dog","Bowser","O'Doyle","Bruise","Psycho","Cannon","Ranger","Clink","Ratchet","Cobra","Reaper","Colt","Rigs","Crank","Ripley","Creep","Roadkill","Daemon","Ronin","Decay","Rubble","Diablo","Sasquatch","Doom","Scar","Dracula","Shiver","Dragon","Skinner","Fender","Skull Crusher","Fester","Slasher","Fisheye","Steelshot","Flack","Surge","Gargoyle","Sythe","Grave","Trip","Gunner","Trooper","Hash","Tweek","Hashtag","Vein","Indominus","Void","Ironclad","Wardon","Killer","Wraith","Knuckles","Zero")
    }
}
