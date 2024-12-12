package com.example.pintiatour

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SelectRolActivity : AppCompatActivity() {

    private var idiomaSeleccionado: String? = "esp"
    private lateinit var mp: MediaPlayer
    private lateinit var textoSelectUser : TextView
    private lateinit var textoVisitante: TextView
    private lateinit var textoGuia: TextView
    private lateinit var textoVolver: TextView
    private lateinit var cardViewVisitante: CardView
    private lateinit var cardViewGuia: CardView
    private lateinit var cardViewVolver: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mp = MediaPlayer.create(this, R.raw.softpiano)
        mp.isLooping = true
        enableEdgeToEdge()
        setContentView(R.layout.activity_select_rol)
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
     * Recupera los datos de sesión, como el idioma seleccionado, desde el Intent.
     */
    private fun getSessionData() {
        // Asigna el idioma seleccionado desde los extras del Intent si está disponible
        idiomaSeleccionado = intent.extras?.getString("idiomaSeleccionado") ?: idiomaSeleccionado
    }

    /**
     * Inicializa las referencias a los elementos de la interfaz y configura el idioma de la interfaz según el idioma seleccionado.
     * Se asignan los componentes de la vista (como botones, textos, y cardViews) a sus respectivas variables.
     * También se ajusta el idioma de la interfaz utilizando el método `changeLanguage` y se resalta el botón correspondiente al idioma actual.
     */
    private fun initComponents() {
        cardViewGuia = findViewById(R.id.CardViewGuia)
        cardViewVisitante = findViewById(R.id.CardViewVisitante)
        cardViewVolver = findViewById(R.id.CardViewVolver)
        textoGuia = findViewById(R.id.texto_select_guia)
        textoVisitante = findViewById(R.id.texto_select_visitante)
        textoSelectUser = findViewById(R.id.texto_select_rol)
        textoVolver = findViewById(R.id.texto_volver)

        changeLanguage()
    }

    /**
     * Configura los listeners para los elementos interactivos de la interfaz.
     * Los listeners de los "cardViews" de guia y viistante navegan a las actividades correspondientes.
     */
    private fun initListeners() {
        cardViewGuia.setOnClickListener {
            var siguientePantalla = Intent(this, CreateGuidedTourActivity::class.java)
            siguientePantalla.putExtra("idiomaSeleccionado", this.idiomaSeleccionado)
            startActivity(siguientePantalla)
        }

        cardViewVisitante.setOnClickListener {
            var siguientePantalla = Intent(this, RecieveGuidedTourActivity::class.java)
            siguientePantalla.putExtra("idiomaSeleccionado", this.idiomaSeleccionado)
            startActivity(siguientePantalla)
        }

        cardViewVolver.setOnClickListener {
            var siguientePantalla = Intent(this, SelectVisitActivity::class.java)
            siguientePantalla.putExtra("idiomaSeleccionado", this.idiomaSeleccionado)
            startActivity(siguientePantalla)
        }


    }

    /**
     * Método que cambia los textos de la interfaz según el idioma seleccionado por el usuario.
     * Además, resalta el botón del idioma actual y desactiva los demás.
     * Los textos de la interfaz para las visitas express y personalizadas, así como el contacto,
     * se actualizan dinámicamente según el idioma seleccionado.
     */
    private fun changeLanguage() {
        when (this.idiomaSeleccionado) {
            "esp" -> {
                textoSelectUser.text = getString(R.string.texto_select_user)
                textoGuia.text = getString(R.string.texto_guia)
                textoVisitante.text = getString(R.string.texto_visitante)
                textoVolver.text = getString(R.string.texto_boton_regresar)
            }
            "eng" -> {
                textoSelectUser.text = getString(R.string.texto_select_user_eng)
                textoGuia.text = getString(R.string.texto_guia_eng)
                textoVisitante.text = getString(R.string.texto_visitante_eng)
                textoVolver.text = getString(R.string.texto_boton_regresar_eng)
            }
            "deu" -> {
                textoSelectUser.text = getString(R.string.texto_select_user_deu)
                textoGuia.text = getString(R.string.texto_guia_deu)
                textoVisitante.text = getString(R.string.texto_visitante_deu)
                textoVolver.text = getString(R.string.texto_boton_regresar_deu)
            }
            "fra" -> {
                textoSelectUser.text = getString(R.string.texto_select_user_fra)
                textoGuia.text = getString(R.string.texto_guia_fra)
                textoVisitante.text = getString(R.string.texto_visitante_fra)
                textoVolver.text = getString(R.string.texto_boton_regresar_fra)
            }
        }
    }
}