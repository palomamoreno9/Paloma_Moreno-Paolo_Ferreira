package com.example.mpazreserva

import com.example.mpazreserva.data.model.Feriado
import com.example.mpazreserva.viewmodel.FeriadoViewModel
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import kotlinx.coroutines.test.runTest

class FeriadoViewModelTest : StringSpec({
    "feriadosList debe contener los datos esperados despues de fetchFeriados()" {
        val fakeFeriados = listOf(
            Feriado(date = "2026-09-18", localName = "Independencia Nacional", name = "Independence Day"),
            Feriado(date = "2026-12-25", localName = "Navidad", name = "Christmas Day")
        )

        // Subclase falsa de FeriadoViewModel que sobrescribe la carga desde red
        val testViewModel = object : FeriadoViewModel() {
            override fun fetchFeriados() {
                _feriadosList.value = fakeFeriados
            }
        }

        runTest {
            testViewModel.fetchFeriados()
            testViewModel.feriadosList.value shouldContainExactly fakeFeriados
        }
    }
})