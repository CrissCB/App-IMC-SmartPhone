package com.example.caluladoradeimc

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

/**
 * Punto de entrada de toda la app. Es la única Activity del proyecto —
 * todo lo demás (Calculadora, Historial, Acerca de) son Fragments que
 * se muestran e intercambian dentro de esta misma Activity, gestionados
 * por Navigation Component.
 *
 * Responsabilidades, y solo estas dos:
 * 1. Mostrar la animación del Splash Screen al arrancar la app.
 * 2. Cargar `activity_main.xml`, que contiene el `NavHostFragment`
 *    (el "contenedor" donde Navigation Component intercambia los Fragments).
 *
 * No contiene lógica de negocio, ni ViewModel, ni observa nada — toda esa
 * responsabilidad vive en los Fragments y sus respectivos ViewModels.
 *
 * ## Conexiones con el resto del proyecto:
 * - **`activity_main.xml`**: layout cargado con `setContentView`, contiene
 *   únicamente el `FragmentContainerView` con `app:navGraph="@navigation/nav_graph"`.
 * - **`nav_graph.xml`**: definido dentro de `activity_main.xml`, determina
 *   que [com.example.caluladoradeimc.ui.calculator.CalculatorFragment] es
 *   lo primero que se muestra tras el Splash Screen.
 * - **`themes.xml`** (`Theme.CaluladoraDeImc.Splash`): tema aplicado a esta
 *   Activity en `AndroidManifest.xml`, requerido por `installSplashScreen()`.
 *   Ese tema define `postSplashScreenTheme`, que es el tema "normal"
 *   (`Theme.CaluladoraDeImc`) al que se cambia automáticamente una vez
 *   terminada la animación.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Debe llamarse ANTES de super.onCreate(): es un requisito de la
        // API de Splash Screen. Instala el tema de splash configurado en
        // el manifest y gestiona automáticamente cuándo ocultarlo (por
        // defecto, en cuanto la primera vista se dibuja en pantalla).
        installSplashScreen()

        super.onCreate(savedInstanceState)

        // Carga activity_main.xml, que a su vez arranca el NavHostFragment
        // con calculatorFragment como primera pantalla visible.
        setContentView(R.layout.activity_main)
    }
}