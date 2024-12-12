package com.example.pintiatour

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ShowCodeActivity : AppCompatActivity() {

    private var idiomaSeleccionado: String? = "esp"
    private var codigo: BooleanArray? = null
    private lateinit var btnVolver: Button
    private lateinit var btnSiguiente: Button
    private lateinit var textViewCodigo: TextView
    private lateinit var audioIntent: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        audioIntent = Intent(this, AudioService::class.java)
        audioIntent.putExtra("AUDIO_RES_ID", R.raw.softpiano) // Recurso de audio
        audioIntent.putExtra("ACTION", "PLAY_BACKGROUND")
        audioIntent.putExtra("IS_LOOPING", true)
        startService(audioIntent)
        enableEdgeToEdge()
        setContentView(R.layout.activity_show_code)
        getSessionData()
        initComponents()
        initListeners()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    /**
     * Libera los recursos del MediaPlayer cuando la actividad es destruida.
     * Este método se llama cuando la actividad está a punto de ser destruida, asegurándose de que
     * los recursos del MediaPlayer sean liberados correctamente para evitar fugas de memoria.
     */
    override fun onDestroy() {
        super.onDestroy()
        audioIntent.putExtra("ACTION", "STOP") // Libera los recursos del MediaPlayer
        startService(intent)
    }

    /**
     * Reanuda la reproducción del audio cuando la actividad vuelve a primer plano.
     * Verifica si el reproductor no está reproduciendo y lo inicia.
     */
    override fun onResume() {
        super.onResume()
        audioIntent.putExtra("ACTION", "RESUME")
        startService(audioIntent)
    }

    /**
     * Detiene la reproducción y libera los recursos del MediaPlayer cuando la actividad pasa a segundo plano.
     * Este método se llama cuando la actividad entra en pausa, asegurándose de que el MediaPlayer se detenga
     * y libere sus recursos si estaba en uso.
     */
    override fun onPause() {
        super.onPause()
        // Enviar la señal para pausar la música
        audioIntent.putExtra("ACTION", "PAUSE")
        startService(audioIntent)
    }

    /**
     * Obtiene los datos enviados desde la actividad previa.
     * Esta función extrae el idioma seleccionado y el tipo de visita personalizada
     * desde los extras del Intent que inicia esta actividad.
     */
    private fun getSessionData(){
        idiomaSeleccionado = intent.extras?.getString("idiomaSeleccionado")
        codigo = intent.extras?.getBooleanArray("codigo")
    }


    /**
     * Inicializa las referencias a los elementos de la interfaz y configura el idioma de la interfaz según el idioma seleccionado.
     * Se asignan los componentes de la vista (como botones, textos, y cardViews) a sus respectivas variables.
     * También se ajusta el idioma de la interfaz utilizando el método `changeLanguage` y se resalta el botón correspondiente al idioma actual.
     */
    private fun initComponents(){
        btnVolver = findViewById(R.id.boton_regresar_visita_guiada)
        btnSiguiente = findViewById(R.id.boton_siguiente_visita_guiada)
        textViewCodigo = findViewById(R.id.texto_codigo)
        textViewCodigo.text = convertirBooleanArrayABase16(codigo)

        changeLanguage()
    }

    /**
     * Este método cambia los textos de la interfaz de usuario al idioma seleccionado por el usuario.
     * Según el valor de `idiomaSeleccionado`, se actualizan los textos de los elementos visuales de la pantalla,
     * como botones, etiquetas, y opciones, con las traducciones correspondientes.
     * Los idiomas soportados son Español (esp), Inglés (eng), Alemán (deu), y Francés (fra).
     *
     * - Cambia los textos estáticos de la interfaz como títulos, botones y etiquetas a los valores traducidos.
     * - Modifica las opciones de visita según el idioma seleccionado.
     *
     * Esto permite ofrecer una experiencia de usuario personalizada y accesible según el idioma preferido.
     */
    private fun changeLanguage() {
        when (this.idiomaSeleccionado) {
            "esp" -> {
                btnVolver.text = getString(R.string.texto_boton_regresar)
                btnSiguiente.text = getString(R.string.texto_boton_siguiente)
            }
            "eng" -> {
                btnVolver.text = getString(R.string.texto_boton_regresar_eng)
                btnSiguiente.text = getString(R.string.texto_boton_siguiente_eng)
            }
            "deu" -> {
                btnVolver.text = getString(R.string.texto_boton_regresar_deu)
                btnSiguiente.text = getString(R.string.texto_boton_siguiente_deu)
            }
            "fra" -> {
                btnVolver.text = getString(R.string.texto_boton_regresar_fra)
                btnSiguiente.text = getString(R.string.texto_boton_siguiente_fra)
            }
        }
    }

    /**
     * Configura los eventos de los botones y otros componentes de la interfaz.
     *
     * Este método establece los manejadores de eventos para cada uno de los botones en la interfaz de usuario.
     * Los botones permiten al usuario modificar la duración de la visita, elegir temas de visita,
     * y navegar entre pantallas. También hay validaciones para mostrar mensajes y actualizaciones en la interfaz
     * dependiendo de las selecciones realizadas por el usuario.
     */
    private fun initListeners() {
        btnVolver.setOnClickListener {
            val siguientePantalla = Intent(this, SelectRolActivity::class.java)
            navigateToNextScreen(siguientePantalla)
        }

        btnSiguiente.setOnClickListener {
            val siguientePantalla = Intent(this, QuickAdviseActivity::class.java)
            siguientePantalla.putExtra("codigo", codigo)
            navigateToNextScreen(siguientePantalla)
        }
    }


    /**
     * Este método se encarga de enviar la información necesaria a la siguiente pantalla.
     *
     * Se crea un `Intent` que contiene los siguientes datos:
     * - `idiomaSeleccionado`: El idioma actualmente seleccionado por el usuario.
     * - `visitaPersonalizada`: La etiqueta que indica que la visita es personalizada.
     *
     * Todos estos datos son enviados mediante el método `putExtra()` al `Intent`, para que puedan ser recuperados
     * por la siguiente actividad.
     *
     * Finalmente, el método `startActivity()` se llama para iniciar la nueva actividad a la cual se enviarán los datos.
     *
     * @param siguientePantalla El `Intent` que representa la siguiente pantalla a la que se navega.
     */
    private fun navigateToNextScreen(siguientePantalla: Intent) {
        siguientePantalla.putExtra("idiomaSeleccionado", this.idiomaSeleccionado) // Idioma seleccionado
        siguientePantalla.putExtra("visitaGuiada", "Visita Guiada") // Tipo de visita
        startActivity(siguientePantalla) // Inicia la nueva actividad
    }

    private fun convertirBooleanArrayABase16(seleccionados: BooleanArray?): String {
        if (seleccionados == null || seleccionados.isEmpty()) return ""

        // Convertir el BooleanArray a una cadena binaria
        val binario = seleccionados.joinToString("") { if (it) "1" else "0" }

        // Asegurar que la longitud del binario sea un múltiplo de 4
        val binarioAjustado = binario.padStart((binario.length + 3) / 4 * 4, '0')

        // Convertir el binario ajustado a base 16 (Hexadecimal)
        val base16 = StringBuilder()
        for (i in 0 until binarioAjustado.length step 4) {
            val group = binarioAjustado.substring(i, i + 4)
            val hexDigit = when (group) {
                "0000" -> "0"
                "0001" -> "1"
                "0010" -> "2"
                "0011" -> "3"
                "0100" -> "4"
                "0101" -> "5"
                "0110" -> "6"
                "0111" -> "7"
                "1000" -> "8"
                "1001" -> "9"
                "1010" -> "a"
                "1011" -> "b"
                "1100" -> "c"
                "1101" -> "d"
                "1110" -> "e"
                "1111" -> "f"
                else -> "0" // Default case, though this should not occur
            }
            base16.append(hexDigit)
        }
        return base16.toString()
    }

}