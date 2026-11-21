package com.example.mpazreserva.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mpazreserva.data.model.Feriado
import com.example.mpazreserva.repository.FeriadoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

open class FeriadoViewModel : ViewModel() {
    private val repository = FeriadoRepository()

    protected val _feriadosList = MutableStateFlow<List<Feriado>>(emptyList())
    open val feriadosList: StateFlow<List<Feriado>> = _feriadosList

    init {
        fetchFeriados()
    }

    open fun fetchFeriados() {
        viewModelScope.launch {
            try {
                _feriadosList.value = repository.getFeriados()
            } catch (e: Exception) {
                println("Error al obtener feriados: ${e.localizedMessage}")
            }
        }
    }
}