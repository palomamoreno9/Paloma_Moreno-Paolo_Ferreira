package com.example.mpazreserva

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.mpazreserva.data.model.Feriado
import com.example.mpazreserva.ui.SolicitudReservaScreen
import com.example.mpazreserva.viewmodel.FeriadoViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class SolicitudReservaScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun formulario_y_feriados_sincronizados_deben_aparecer_en_pantalla() {
        val fakeFeriados = listOf(
            Feriado(date = "2026-09-18", localName = "Independencia Nacional", name = "Independence Day"),
            Feriado(date = "2026-12-25", localName = "Navidad", name = "Christmas Day")
        )

        //subclase falsa de FeriadoViewModel con StateFlow simulado
        val fakeViewModel = object : FeriadoViewModel() {
            override fun fetchFeriados() {}
            override val feriadosList = MutableStateFlow(fakeFeriados)
        }

        composeRule.setContent {
            SolicitudReservaScreen(
                emailSolicitante = "profesor@mpaz.cl",
                onAgregarReserva = {},
                onReservaCreada = {},
                onVolver = {},
                feriadoViewModel = fakeViewModel
            )
        }

        //valida que los textos clave se muestran en la UI
        composeRule.onNodeWithText("Formulario de Solicitud").assertIsDisplayed()
        composeRule.onNodeWithText("✓ Calendario sincronizado con 2 feriados oficiales.").assertIsDisplayed()
    }
}