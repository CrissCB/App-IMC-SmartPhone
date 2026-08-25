package com.example.caluladoradeimc.ui.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.example.caluladoradeimc.data.AppDatabase
import com.example.caluladoradeimc.data.HistoryEntity

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).historyDao()

    val historial: LiveData<List<HistoryEntity>> = dao.obtenerTodos().asLiveData()
}