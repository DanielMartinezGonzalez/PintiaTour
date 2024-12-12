package com.example.pintiatour

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.NestedScrollView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.random.Random

class GuidedTourContentActivity : AppCompatActivity() {
    private lateinit var mp : MediaPlayer
    private var idiomaSeleccionado: String? = "esp"
    private var tour: IntArray? = IntArray(0)
    private var posicion: Int? = 0
    private lateinit var checkBoxContainer: LinearLayoutCompat
    private lateinit var btnVolver: Button
    private lateinit var btnSalir: FloatingActionButton
    private lateinit var btnSiguiente: Button
    private lateinit var textoTitulo: TextView
    private lateinit var textoConsejo: TextView
    private lateinit var imageViewContenido: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val i: Int = Random.nextInt(1, 16)  // Genera un número aleatorio entre 1 y 9.

        // Construye dinámicamente el identificador del recurso
        val resId = resources.getIdentifier("music$i", "raw", packageName)

        mp=MediaPlayer.create(this, resId)
        mp.start()
        mp.isLooping = true
        enableEdgeToEdge()
        setContentView(R.layout.activity_guided_tour_content)
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
        posicion = intent.extras?.getInt("posicion")
    }


    /**
     * Inicializa las referencias a los elementos de la interfaz y configura el idioma de la interfaz según el idioma seleccionado.
     * Se asignan los componentes de la vista (como botones, textos, y cardViews) a sus respectivas variables.
     * También se ajusta el idioma de la interfaz utilizando el método `changeLanguage` y se resalta el botón correspondiente al idioma actual.
     */
    private fun initComponents() {
        btnVolver = findViewById(R.id.boton_regresar_visita_guiada)
        btnSiguiente = findViewById(R.id.boton_siguiente_visita_guiada)
        textoTitulo = findViewById(R.id.texto_visita_express_o_personalizada_punto_siguiente_contenido)
        textoConsejo = findViewById(R.id.texto_consejo_usar_lista)
        imageViewContenido = findViewById(R.id.imagen_contenido_punto)
        imageViewContenido.setImageResource(encontrarImagen(tour, posicion))
        checkBoxContainer = findViewById(R.id.linear_layout_checkbox_container)
        btnSalir = findViewById(R.id.boton_salir)

        // Variable 'posicion' que contiene la posición del elemento deseado
        val posicionCentral = posicion ?: 0

        // Crear la lista de CardViews
        val cardViewTexts = crearListaLugares()

        for ((index, texto) in cardViewTexts.withIndex()) {
            // Crear el CardView
            val cardView = CardView(this).apply {
                layoutParams = LinearLayoutCompat.LayoutParams(
                    LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                    225 // Altura del CardView en pixels
                ).apply {
                    setMargins(40, 10, 40, 10) // Margen del CardView
                }
                radius = 20f // Esquinas redondeadas
                setCardBackgroundColor(
                    if (index == posicionCentral) {
                        resources.getColor(R.color.green_dark, theme)
                    } else {
                        resources.getColor(R.color.brown_normal, theme)
                    }
                )
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

            // Crear el TextView
            val textView = TextView(this).apply {
                layoutParams = LinearLayoutCompat.LayoutParams(
                    0,
                    LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                    5f
                )
                this.text = texto
                textSize = 21f
                setTextColor(resources.getColor(R.color.black, theme))
                isAllCaps = false
                gravity = android.view.Gravity.CENTER
            }

            // Crear el ImageView
            val imageView = ImageView(this).apply {
                layoutParams = LinearLayoutCompat.LayoutParams(
                    0,
                    90, // Altura en pixels
                    1f
                )
                setImageResource(R.drawable.clickar_aqui)
                scaleType = ImageView.ScaleType.CENTER_INSIDE
                setColorFilter(resources.getColor(R.color.black, theme))
            }

            // Agregar el TextView y el ImageView al LinearLayout
            linearLayout.addView(textView)
            linearLayout.addView(imageView)

            // Agregar el LinearLayout al CardView
            cardView.addView(linearLayout)

            // Configurar el clic del CardView
            cardView.setOnClickListener {
                // Actualizar la posición correctamente
                posicion = index
                val siguientePantalla = Intent(this, GuidedTourContentActivity::class.java)
                    .putExtra("posicion", posicion)
                    .putExtra("tour", tour)

                navigateToNextScreen(siguientePantalla)
            }

            // Agregar el CardView al contenedor principal

            checkBoxContainer.addView(cardView)
        }

        // Ajustar el NestedScrollView para centrar el elemento especificado
        checkBoxContainer.post {
            val childHeight = 225 + 20 // Altura del CardView + márgenes
            val visibleItemCount = 3 // Elementos visibles en el layout
            val scrollOffset = (posicionCentral * childHeight) - (childHeight * (visibleItemCount / 2))

            (checkBoxContainer.parent as? NestedScrollView)?.scrollTo(0, scrollOffset.coerceAtLeast(0))
        }


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
                textoTitulo.text = getString(R.string.texto_select_visit_visita_guiada)
                textoConsejo.text = getString(R.string.texto_consejo_usar_lista)
            }
            "eng" -> {
                btnVolver.text = getString(R.string.texto_boton_regresar_eng)
                btnSiguiente.text = getString(R.string.texto_boton_siguiente_eng)
                textoTitulo.text = getString(R.string.texto_select_visit_visita_guiada_eng)
                textoConsejo.text = getString(R.string.texto_consejo_usar_lista_eng)
            }
            "deu" -> {
                btnVolver.text = getString(R.string.texto_boton_regresar_deu)
                btnSiguiente.text = getString(R.string.texto_boton_siguiente_deu)
                textoTitulo.text = getString(R.string.texto_select_visit_visita_guiada_deu)
                textoConsejo.text = getString(R.string.texto_consejo_usar_lista_eng)
            }
            "fra" -> {
                btnVolver.text = getString(R.string.texto_boton_regresar_fra)
                btnSiguiente.text = getString(R.string.texto_boton_siguiente_fra)
                textoTitulo.text = getString(R.string.texto_select_visit_visita_guiada_fra)
                textoConsejo.text = getString(R.string.texto_consejo_usar_lista_eng)
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
            mp.stop()
            val siguientePantalla: Intent= if (posicion == 0){
                Intent(this, SelectRolActivity::class.java)
            } else {
                posicion = posicion!! - 1
                Intent(this, GuidedTourContentActivity::class.java)
                    .putExtra("posicion", posicion)
                    .putExtra("tour", tour)
            }

            navigateToNextScreen(siguientePantalla)
        }

        btnSiguiente.setOnClickListener {
            mp.stop()
            val siguientePantalla: Intent = if (tour?.size == (posicion!! + 1)) {
                Intent(this, ShowFinalGuidedPointActivity::class.java)
            } else {
                posicion = posicion!! + 1
                Intent(this, GuidedTourContentActivity::class.java)

            }
            siguientePantalla.putExtra("posicion", posicion)
            siguientePantalla.putExtra("tour", tour)
            navigateToNextScreen(siguientePantalla)
        }

        btnSalir.setOnClickListener{
            mp.stop()
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

    private fun encontrarImagen(tour: IntArray?, posicion: Int?): Int {
        val numeroPantallaContenidoActual = posicion?.let { tour?.get(it) }

        val imgContenidoTematica = resources.getIdentifier(
            "img_punto_contenido_$numeroPantallaContenidoActual", "drawable", packageName
        )
        return imgContenidoTematica
    }

    private fun crearListaLugares(): MutableList<String> {
        val sufijoIdioma = if (idiomaSeleccionado == "esp") "" else "_$idiomaSeleccionado"
        val cardViewTexts = mutableListOf<String>()

        // Iterar sobre la lista 'tour' y mapear identificadores a nombres

        tour?.forEach { id ->
            // Construir el nombre del recurso basado en el idioma y el identificador
            val textoContenidoTematica = resources.getIdentifier(
                "texto_nombre_punto_contenido${sufijoIdioma}_$id",
                "string", packageName
            )

            // Verificar que el recurso existe antes de agregarlo
            if (textoContenidoTematica != 0) {
                val nombre = getString(textoContenidoTematica)
                cardViewTexts.add(nombre)
            } else {
                // Manejar el caso en que el recurso no exista (opcional)
                cardViewTexts.add("Recurso no encontrado para ID: $id")
            }
        }

        return cardViewTexts
    }



}