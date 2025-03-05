package contreras.roberto.myfeelings

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import contreras.roberto.myfeelings.utilities.CustomBarDrawable
import contreras.roberto.myfeelings.utilities.CustomCircleDrawable
import contreras.roberto.myfeelings.utilities.Emociones
import contreras.roberto.myfeelings.utilities.JSONFile
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    var jsonFile: JSONFile? = null
    var veryHappy = 0.0F
    var happy = 0.0F
    var neutral = 0.0F
    var sad = 0.0F
    var verySad = 0.0F
    var data: Boolean = false
    var lista = ArrayList<Emociones>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        jsonFile = JSONFile()

        fetchingData()
        if (!data) {
            var emociones = ArrayList<Emociones>()
            val fondo = CustomCircleDrawable(this, emociones)

            val gvh:View = findViewById(R.id.graphVeryHappy)
            val gh :View = findViewById(R.id.graphHappy)
            val gn :View = findViewById(R.id.graphNeutral)
            val gs :View = findViewById(R.id.graphSad)
            val gvs:View = findViewById(R.id.graphVerySad)
            val g : View = findViewById(R.id.graph)

            val evh: Emociones = Emociones("Muy felíz", 0.0F, R.color.mustard, veryHappy)
            val eh : Emociones = Emociones("Felíz", 0.0F, R.color.orange, happy)
            val en : Emociones = Emociones("Neutral", 0.0F, R.color.greenie, neutral)
            val es : Emociones = Emociones("Triste", 0.0F, R.color.blue, sad)
            val evs: Emociones = Emociones("Muy triste", 0.0F, R.color.deepBlue, verySad)

            g.background = fondo
            gvh.background= CustomBarDrawable(this, evh)
            gh.background = CustomBarDrawable(this, eh)
            gn.background = CustomBarDrawable(this, en)
            gs.background = CustomBarDrawable(this, es)
            gvs.background= CustomBarDrawable(this, evs)

        } else {
            actualizarGrafica()
            iconoMayoria()
        }

        findViewById<Button>(R.id.guardarButton).setOnClickListener {
            guardar()
        }

        findViewById<ImageButton>(R.id.veryHappyButton).setOnClickListener {
            veryHappy++
            iconoMayoria()
            actualizarGrafica()
        }

        findViewById<ImageButton>(R.id.happyButton).setOnClickListener {
            happy++
            iconoMayoria()
            actualizarGrafica()
        }

        findViewById<ImageButton>(R.id.neutralButton).setOnClickListener {
            neutral++
            iconoMayoria()
            actualizarGrafica()
        }

        findViewById<ImageButton>(R.id.sadButton).setOnClickListener {
            sad++
            iconoMayoria()
            actualizarGrafica()
        }

        findViewById<ImageButton>(R.id.verySadButton).setOnClickListener {
            verySad++
            iconoMayoria()
            actualizarGrafica()
        }
    }

    fun fetchingData() {
        try {
            var json : String = jsonFile?.getData(this) ?: ""
            if (json != "") {
                this.data = true
                var jsonArray: JSONArray = JSONArray()

                this.lista = parseJson(jsonArray)

                for (e in lista) {
                    when (e.nombre) {
                        "Muy felíz" -> veryHappy = e.total
                        "Felíz" -> happy = e.total
                        "Neutral" -> neutral = e.total
                        "Triste" -> sad = e.total
                        "Muy triste" -> verySad = e.total
                    }
                }
            } else {
                this.data = false
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun parseJson (jsonArray: JSONArray) : ArrayList<Emociones> {
        var lista = ArrayList<Emociones>()

        for (i in 0..jsonArray.length()) {
            try {
                val nombre = jsonArray.getJSONObject(i).getString("nombre")
                val porcentaje = jsonArray.getJSONObject(i).getDouble("porcentaje").toFloat()
                val color = jsonArray.getJSONObject(i).getInt("color")
                val total = jsonArray.getJSONObject(i).getDouble("total").toFloat()
                var emocion = Emociones(nombre, porcentaje, color, total)
                lista.add(emocion)
            } catch (e : JSONException) {
                e.printStackTrace()
            }
        }

        return lista
    }

    fun actualizarGrafica() {
        val total = veryHappy+happy+neutral+verySad+sad

        var pVH: Float = (veryHappy * 100 / total).toFloat()
        var pH: Float = (happy * 100 / total).toFloat()
        var pN: Float = (neutral * 100 / total).toFloat()
        var pS: Float = (sad * 100 / total).toFloat()
        var pVS: Float = (verySad * 100 / total).toFloat()

        Log.d("porcentajes", "very happy "+pVH)
        Log.d("porcentajes", "happy "+pH)
        Log.d("porcentajes", "neutral "+pN)
        Log.d("porcentajes", "sad "+pS)
        Log.d("porcentajes", "very sad "+pVS)

        val evh: Emociones = Emociones("Muy felíz", pVH, R.color.mustard, veryHappy)
        val eh : Emociones = Emociones("Felíz", pH, R.color.orange, happy)
        val en : Emociones = Emociones("Neutral", pN, R.color.greenie, neutral)
        val es : Emociones = Emociones("Triste", pS, R.color.blue, sad)
        val evs: Emociones = Emociones("Muy triste", pVS, R.color.deepBlue, verySad)

        lista.clear()
        lista.add(evh)
        lista.add(eh)
        lista.add(en)
        lista.add(es)
        lista.add(evs)

        val fondo = CustomCircleDrawable(this, lista)

        val gvh:View = findViewById(R.id.graphVeryHappy)
        val gh :View = findViewById(R.id.graphHappy)
        val gn :View = findViewById(R.id.graphNeutral)
        val gs :View = findViewById(R.id.graphSad)
        val gvs:View = findViewById(R.id.graphVerySad)
        val g : View = findViewById(R.id.graph)

        gvh.background=CustomBarDrawable(this, evh)
        gh.background =CustomBarDrawable(this, eh)
        gn.background =CustomBarDrawable(this, en)
        gs.background =CustomBarDrawable(this, es)
        gvs.background=CustomBarDrawable(this, evs)
        g.background = fondo
    }

    fun iconoMayoria() {
        val icon : ImageView = findViewById(R.id.icon)

        if (happy>veryHappy && happy>neutral && happy>sad && happy>verySad) {
            icon.setImageDrawable(resources.getDrawable(R.drawable.ic_happy))
        }
        if (veryHappy>happy && veryHappy>neutral && veryHappy>sad && veryHappy>verySad) {
            icon.setImageDrawable(resources.getDrawable(R.drawable.ic_veryhappy))
        }
        if (neutral>veryHappy && neutral>happy && neutral>sad && neutral>verySad) {
            icon.setImageDrawable(resources.getDrawable(R.drawable.ic_neutral))
        }
        if (sad>veryHappy && sad>neutral && sad>happy && sad>verySad) {
            icon.setImageDrawable(resources.getDrawable(R.drawable.ic_sad))
        }
        if (verySad>veryHappy && verySad>neutral && verySad>sad && verySad>happy) {
            icon.setImageDrawable(resources.getDrawable(R.drawable.ic_verysad))
        }
    }

    fun guardar() {
        var jsonArray = JSONArray()
        var o : Int = 0
        for (e in lista) {
            Log.d("objetos", e.toString())
            var j:JSONObject = JSONObject()
            j.put("nombre", e.nombre)
            j.put("porcentaje", e.porcentaje)
            j.put("color", e.color)
            j.put("total", e.total)

            jsonArray.put(o, j)
            o++
        }

        jsonFile?.saveData(this, jsonArray.toString())

        Toast.makeText(this, "Datos guardados", Toast.LENGTH_SHORT).show()
    }


}