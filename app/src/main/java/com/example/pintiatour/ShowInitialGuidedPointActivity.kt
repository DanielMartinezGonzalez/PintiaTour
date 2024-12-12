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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.random.Random

class ShowInitialGuidedPointActivity : AppCompatActivity() {

    private var idiomaSeleccionado: String? = "esp"
    private var codigo: BooleanArray? = null
    private lateinit var tour: IntArray
    private lateinit var btnVolver: Button
    private lateinit var btnSiguiente: Button
    private lateinit var btnSalir: FloatingActionButton
    private lateinit var textoInicial: TextView
    private lateinit var textoTitulo: TextView
    private lateinit var audioIntent: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Crea y reproduce el MediaPlayer
        audioIntent = Intent(this, AudioService::class.java)
        audioIntent.putExtra("AUDIO_RES_ID", R.raw.flute) // Recurso de audio
        audioIntent.putExtra("ACTION", "PLAY_BACKGROUND")// Indica que debe reproducirse en bucle
        audioIntent.putExtra("IS_LOOPING", true)
        startService(audioIntent)
        enableEdgeToEdge()
        setContentView(R.layout.activity_show_initial_guided_point)
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
     * Libera los recursos del MediaPlayer al destruir la actividad.
     * Este método asegura que el MediaPlayer se libere correctamente cuando la actividad se destruye
     * para evitar fugas de memoria.
     */
    override fun onDestroy() {
        super.onDestroy()
        audioIntent.putExtra("ACTION", "STOP")
        startService(audioIntent)
    }

    /**
     * Detiene la reproducción del audio y libera los recursos del MediaPlayer al pausar la actividad.
     * Este método asegura que el audio se detenga y se liberen los recursos utilizados por el MediaPlayer
     * cuando la actividad pasa a segundo plano.
     */
    override fun onPause() {
        super.onPause()
        audioIntent.putExtra("ACTION", "PAUSE")
        startService(audioIntent)
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
        textoTitulo = findViewById(R.id.texto_visita_express_o_personalizada_punto_inicial)
        textoInicial = findViewById(R.id.texto_punto_inicial)
        btnSalir= findViewById(R.id.boton_salir)

        tour = codigo?.let { prepareTour(it) }!!
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
                textoTitulo.text = getString(R.string.texto_visita_express)
                btnVolver.text = getString(R.string.texto_boton_regresar)
                btnSiguiente.text = getString(R.string.texto_boton_siguiente)
                textoInicial.text = getString(R.string.texto_inicial_visita_guiada)
            }
            "eng" -> {
                textoTitulo.text = getString(R.string.texto_visita_express_eng)
                btnVolver.text = getString(R.string.texto_boton_regresar_eng)
                btnSiguiente.text = getString(R.string.texto_boton_siguiente_eng)
                textoInicial.text = getString(R.string.texto_inicial_visita_guiada_eng)
            }
            "deu" -> {
                textoTitulo.text = getString(R.string.texto_visita_express_deu)
                btnVolver.text = getString(R.string.texto_boton_regresar_deu)
                btnSiguiente.text = getString(R.string.texto_boton_siguiente_deu)
                textoInicial.text = getString(R.string.texto_inicial_visita_guiada_deu)
            }
            "fra" -> {
                textoTitulo.text = getString(R.string.texto_visita_express_fra)
                btnVolver.text = getString(R.string.texto_boton_regresar_fra)
                btnSiguiente.text = getString(R.string.texto_boton_siguiente_fra)
                textoInicial.text = getString(R.string.texto_inicial_visita_guiada_fra)
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
            audioIntent.putExtra("ACTION", "STOP")
            startService(audioIntent)
            val siguientePantalla = Intent(this, SelectRolActivity::class.java)
            navigateToNextScreen(siguientePantalla)
        }

        btnSiguiente.setOnClickListener {
            audioIntent.putExtra("ACTION", "STOP")
            startService(audioIntent)
            val siguientePantalla = Intent(this, GuidedTourContentActivity::class.java)
            siguientePantalla.putExtra("tour", tour)
            siguientePantalla.putExtra("posicion", 0)
            navigateToNextScreen(siguientePantalla)
        }

        btnSalir.setOnClickListener{
            audioIntent.putExtra("ACTION", "STOP")
            startService(audioIntent)
            var siguientePantalla = Intent(this, SelectVisitActivity::class.java)
            navigateToNextScreen(siguientePantalla) // Finaliza l
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
        startActivity(siguientePantalla) // Inicia la nueva actividad
    }


    private fun prepareTour(codigo: BooleanArray): IntArray {
        val posiciones = mutableListOf<Int>()
        for (index in codigo.indices) {
            if (codigo[index]) {
                posiciones.add(index)
            }
        }
        return posiciones.toIntArray()
    }





}