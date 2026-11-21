package com.example.mpazreserva

import com.example.mpazreserva.data.model.Feriado
import com.example.mpazreserva.data.remote.ApiService
import com.example.mpazreserva.repository.FeriadoRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest

class TestableFeriadoRepository(private val testApi: ApiService) : FeriadoRepository() {
    override suspend fun getFeriados(): List<Feriado> {
        return testApi.getFeriados()
    }
}

class FeriadoRepositoryTest : StringSpec({
    "getFeriados() debe retornar una lista de feriados simulada" {
        //simula el resultado de la API
        val fakeFeriados = listOf(
            Feriado(date = "2026-09-18", localName = "Independencia Nacional", name = "Independence Day"),
            Feriado(date = "2026-12-25", localName = "Navidad", name = "Christmas Day")
        )

        //mock de ApiService con MockK
        val mockApi = mockk<ApiService>()
        coEvery { mockApi.getFeriados() } returns fakeFeriados

        //inyecta el mock en el repositorio testable
        val repo = TestableFeriadoRepository(testApi = mockApi)

        //ejecuta el test con corrutinas
        runTest {
            val result = repo.getFeriados()
            result shouldContainExactly fakeFeriados
        }
    }
})