package com.example.pintiatour

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class CreateGuidedTourActivity : AppCompatActivity() {

    private lateinit var textoVisitaGuiada: TextView
    private lateinit var textoLugaresGuiada: TextView
    private lateinit var btnVolver: Button
    private lateinit var btnSiguiente: Button
    private lateinit var checkBoxContainer: LinearLayoutCompat
    private lateinit var audioIntent: Intent
    private var idiomaSeleccionado: String? = "esp"

    /**
     * Método principal que se ejecuta cuando se crea la actividad.
     * Aquí se configuran los componentes de la interfaz, se obtienen los datos de la sesión
     * y se configuran los eventos de los botones.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        audioIntent = Intent(this, AudioService::class.java)
        audioIntent.putExtra("AUDIO_RES_ID", R.raw.softpiano) // Recurso de audio
        audioIntent.putExtra("ACTION", "PLAY_BACKGROUND")
        audioIntent.putExtra("IS_LOOPING", true)
        startService(audioIntent)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_guided_tour)
        getSessionData() // Obtiene los datos de la sesión actual
        initComponents() // Inicializa los componentes visuales
        initListeners() // Configura los eventos de los botones
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
    }

    /**
     * Inicializa las referencias a los elementos de la interfaz y configura el idioma de la interfaz según el idioma seleccionado.
     * Se asignan los componentes de la vista (como botones, textos, y cardViews) a sus respectivas variables.
     * También se ajusta el idioma de la interfaz utilizando el método `changeLanguage` y se resalta el botón correspondiente al idioma actual.
     */
    private fun initComponents(){
        textoVisitaGuiada = findViewById(R.id.texto_visita_guiada)
        textoLugaresGuiada = findViewById(R.id.texto_lugares_visita_guiada)
        btnVolver = findViewById(R.id.boton_regresar_visita_guiada)
        btnSiguiente = findViewById(R.id.boton_siguiente_visita_guiada)
        checkBoxContainer = findViewById(R.id.linear_layout_checkbox_container)

        // Lista de textos para los Checkboxes
        val checkboxTexts = crearListaLugares()

        // Crear y agregar los CardView dinámicamente
        for (texto in checkboxTexts) {
            // Crear el CardView
            val cardView: CardView
            cardView = CardView(this).apply {
                layoutParams = LinearLayoutCompat.LayoutParams(
                    LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                    225 // Altura del CardView en pixels
                ).apply {
                    setMargins(40, 10, 40, 10) // Margen del CardView
                }
                radius = 20f // Esquinas redondeadas
                setCardBackgroundColor(resources.getColor(R.color.brown_normal, theme))
            }

            // Crear el LinearLayout interno del CardView
            val linearLayout = LinearLayoutCompat(this).apply {
                layoutParams = LinearLayoutCompat.LayoutParams(
                    LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                    LinearLayoutCompat.LayoutParams.MATCH_PARENT
                )
                orientation = LinearLayoutCompat.HORIZONTAL
                gravity = android.view.Gravity.CENTER
            }

            // Crear el CheckBox
            val checkBox = CheckBox(this).apply {
                layoutParams = LinearLayoutCompat.LayoutParams(
                    0, // Usar peso para distribuir el espacio
                    LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                    5f // Peso relativo
                )
                text = texto
                textSize = 21f
                setTextColor(resources.getColor(R.color.black, theme))
                isAllCaps = false
                gravity = android.view.Gravity.CENTER_VERTICAL
            }

            // Crear el ImageView
            val imageView: ImageView
            imageView = ImageView(this).apply {
                layoutParams = LinearLayoutCompat.LayoutParams(
                    0,
                    90, // Altura en pixels
                    1f // Peso relativo
                )
                setImageResource(R.drawable.clickar_aqui)
                scaleType = ImageView.ScaleType.CENTER_INSIDE
                setColorFilter(resources.getColor(R.color.black, theme))
            }

            // Agregar el CheckBox y el ImageView al LinearLayout
            linearLayout.addView(checkBox)
            linearLayout.addView(imageView)

            // Agregar el LinearLayout al CardView
            cardView.addView(linearLayout)

            // Agregar el CardView al contenedor principal
            checkBoxContainer.addView(cardView)
        }
        changeLanguage()
    }


    private fun crearListaLugares(): MutableList<String> {
        val sufijoIdioma = if (idiomaSeleccionado == "esp") "" else "_$idiomaSeleccionado"
        val checkboxTexts= mutableListOf<String>()
        for (i in 0 until 19){
            val textoContenidoTematica = resources.getIdentifier(
                "texto_nombre_punto_contenido${sufijoIdioma}_$i",
                "string", packageName
            )
            val nombre = getString(textoContenidoTematica)
            checkboxTexts.add(i, nombre)
        }
        return checkboxTexts

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

            val seleccionados = checkBoxSeleccionados()
            if (seleccionados.all {!it}) {
                textoLugaresGuiada.setTextColor(getColor(R.color.red_pure))
            } else {
                val siguientePantalla = Intent(this, ShowCodeActivity::class.java)
                siguientePantalla.putExtra("codigo", seleccionados)
                navigateToNextScreen(siguientePantalla)
            }
        }
    }



    private fun checkBoxSeleccionados(): BooleanArray {
        // Verificamos si el tamaño no es divisible entre 4
        val incremento = 4 - (checkBoxContainer.childCount % 4)
        var count = BooleanArray(checkBoxContainer.childCount + incremento) { false }

        for (i in 0 until checkBoxContainer.childCount) {
            val cardView = checkBoxContainer.getChildAt(i)
            if (cardView is CardView) {
                val linearLayout = cardView.getChildAt(0) as? LinearLayoutCompat
                linearLayout?.let {
                    for (j in 0 until it.childCount) {
                        val view = it.getChildAt(j)
                        if (view is CheckBox && view.isChecked) {
                            count[i] = true
                        }
                    }
                }
            }
        }
        return count
    }


    /**
     * Este método se encarga de enviar la información necesaria a la siguiente pantalla.
     *
     * Se crea un `Intent` que contiene los siguientes datos:
     * - `idiomaSeleccionado`: El idioma actualmente seleccionado por el usuario.
     * - `guiada`: La etiqueta que indica que la visita es guiada.
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
                textoVisitaGuiada.text = getString(R.string.texto_select_visit_visita_guiada)
                textoLugaresGuiada.text = getString(R.string.texto_lugares_visita_guiada)
                btnVolver.text = getString(R.string.texto_boton_regresar)
                btnSiguiente.text = getString(R.string.texto_boton_siguiente)
            }
            "eng" -> {
                textoVisitaGuiada.text = getString(R.string.texto_select_visit_visita_guiada_eng)
                textoLugaresGuiada.text = getString(R.string.texto_lugares_visita_guiada_eng)
                btnVolver.text = getString(R.string.texto_boton_regresar_eng)
                btnSiguiente.text = getString(R.string.texto_boton_siguiente_eng)
            }
            "deu" -> {
                textoVisitaGuiada.text = getString(R.string.texto_select_visit_visita_guiada_deu)
                textoLugaresGuiada.text = getString(R.string.texto_lugares_visita_guiada_deu)
                btnVolver.text = getString(R.string.texto_boton_regresar_deu)
                btnSiguiente.text = getString(R.string.texto_boton_siguiente_deu)
            }
            "fra" -> {
                textoVisitaGuiada.text = getString(R.string.texto_select_visit_visita_guiada_fra)
                textoLugaresGuiada.text = getString(R.string.texto_lugares_visita_guiada_fra)
                btnVolver.text = getString(R.string.texto_boton_regresar_fra)
                btnSiguiente.text = getString(R.string.texto_boton_siguiente_fra)
            }
        }
    }

}