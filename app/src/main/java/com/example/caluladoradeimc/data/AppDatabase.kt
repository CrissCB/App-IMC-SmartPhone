package com.example.caluladoradeimc.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * ## Patrón Singleton
 * Una base de datos no debe abrirse más de una vez dentro de la misma app (es costoso
 * y puede causar conflictos). Por eso `getDatabase()` usa el patrón Singleton:
 * la primera vez que se llama, crea la instancia; todas las llamadas siguientes
 * devuelven esa misma instancia ya creada, sin volver a construirla.
 *
 * `@Volatile` + `synchronized` es la forma estándar de hacer este patrón seguro cuando
 * varios hilos podrían llamar a `getDatabase()` al mismo tiempo (por ejemplo, si
 * `CalculatorViewModel` e `HistoryViewModel` se crean casi simultáneamente al abrir la app).
 *
 * ## Conexiones con el resto del proyecto:
 * - [com.example.caluladoradeimc.ui.calculator.CalculatorViewModel] y
 *   [com.example.caluladoradeimc.ui.history.HistoryViewModel] llaman a
 *   `AppDatabase.getDatabase(application).historyDao()` en su inicialización,
 *   para obtener acceso al DAO y así poder guardar/leer datos.
 * - Es el único archivo del módulo `data/` que necesita el `Context` de Android
 *   (por eso recibe `application: Application` en los ViewModels que la usan,
 *   en vez de un `Context` genérico que podría causar memory leaks).
 */
@Database(entities = [HistoryEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun historyDao(): HistoryDao //Expone el DAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null //Instancia única de la base de datos

        /**
         * Si ya existe una instancia creada, la devuelve directamente (rápido).
         * Si no existe, la crea de forma segura (bloqueando momentáneamente otros hilos
         * con `synchronized`, para evitar crear dos instancias por accidente) y la guarda
         * en [INSTANCE] para las próximas veces.
         *
         * @param context normalmente el `Application` context de un ViewModel,
         * para evitar mantener referencias a Activities/Fragments que podrían destruirse.
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instancia = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "indice_app_db"
                ).build()
                INSTANCE = instancia
                instancia
            }
        }
    }
}