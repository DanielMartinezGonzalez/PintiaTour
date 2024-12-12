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

class ShowFinalGuidedPointActivity : AppCompatActivity() {

    private lateinit var mp : MediaPlayer
    private var idiomaSeleccionado: String? = "esp"
    private var tour: IntArray? = IntArray(0)
    private lateinit var btnVolver: Button
    private lateinit var btnSiguiente: Button
    private lateinit var textoInicial: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mp= MediaPlayer.create(this,R.raw.turnhorizontal)
        mp.start()
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
     * Reanuda la reproducción del audio cuando la actividad vuelve a primer plano.
     * Verifica si el reproductor no está reproduciendo y lo inicia.
     */
    override fun onResume() {
        super.onResume()
        if (!mp.isPlaying) {
            mp.start()
        }
    }

    /**
     * Pausa la reproducción del audio cuando la actividad pasa a segundo plano.
     * Verifica si el reproductor está reproduciendo y lo detiene temporalmente.
     */
    override fun onPause() {
        super.onPause()
        if (mp.isPlaying) {
            mp.pause()
        }
    }

    /**
     * Libera los recursos del MediaPlayer al destruir la actividad.
     * Esto asegura que no haya fugas de memoria relacionadas con el reproductor.
     */
    override fun onDestroy() {
        super.onDestroy()
        mp.release()
    }

    /**
     * Obtiene los datos enviados desde la actividad previa.
     * Esta función extrae el idioma seleccionado y el tipo de visita personalizada
     * desde los extras del Intent que inicia esta actividad.
     */
    private fun getSessionData(){
        idiomaSeleccionado = intent.extras?.getString("idiomaSeleccionado")
        tour = intent.extras?.getIntArray("tour")
    }


    /**
     * Inicializa las referencias a los elementos de la interfaz y configura el idioma de la interfaz según el idioma seleccionado.
     * Se asignan los componentes de la vista (como botones, textos, y cardViews) a sus respectivas variables.
     * También se ajusta el idioma de la interfaz utilizando el método `changeLanguage` y se resalta el botón correspondiente al idioma actual.
     */
    private fun initComponents(){
        btnVolver = findViewById(R.id.boton_regresar_visita_guiada)
        btnSiguiente = findViewById(R.id.boton_siguiente_visita_guiada)
        textoInicial = findViewById(R.id.texto_punto_inicial)


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
                btnSiguiente.text = getString(R.string.texto_boton_fin_visita)
                textoInicial.text = getString(R.string.texto_fin_visita)
            }
            "eng" -> {
                btnVolver.text = getString(R.string.texto_boton_regresar_eng)
                btnSiguiente.text = getString(R.string.texto_boton_fin_visita_eng)
                textoInicial.text = getString(R.string.texto_fin_visita_eng)
            }
            "deu" -> {
                btnVolver.text = getString(R.string.texto_boton_regresar_deu)
                btnSiguiente.text = getString(R.string.texto_boton_fin_visita_deu)
                textoInicial.text = getString(R.string.texto_fin_visita_deu)
            }
            "fra" -> {
                btnVolver.text = getString(R.string.texto_boton_regresar_fra)
                btnSiguiente.text = getString(R.string.texto_boton_fin_visita_fra)
                textoInicial.text = getString(R.string.texto_fin_visita_fra)
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
            val siguientePantalla = Intent(this, GuidedTourContentActivity::class.java)
            siguientePantalla.putExtra("tour", tour)
            siguientePantalla.putExtra("posicion", tour?.size?.minus(1))
            navigateToNextScreen(siguientePantalla)
        }

        btnSiguiente.setOnClickListener {
            val siguientePantalla = Intent(this, SelectVisitActivity::class.java)
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
        startActivity(siguientePantalla) // Inicia la nueva actividad
    }


}