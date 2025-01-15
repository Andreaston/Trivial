package com.arm.trivial

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            navegacion()
        }
    }
}

@Composable
fun navegacion(){

    val navegacionControlador = rememberNavController()

    NavHost(navController = navegacionControlador, startDestination = "Pantalla_Inicio"){
        composable("Pantalla_Inicio") { Pantalla_Inicio(navegacionControlador) }
        composable("Pantalla_Juego") { Pantalla_Juego(navegacionControlador) }
        composable("Pantalla_Final/{puntuacion}") { backStackEntry -> val puntuacion = backStackEntry.arguments?.getString("puntuacion")?.toInt() ?:0
            Pantalla_Final(navegacionControlador, puntuacion) }
    }

}

//Pantalla de inicio
@Composable
fun Pantalla_Inicio(navegacionControlador: NavHostController){

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Bienvenido al juego del Trivial")
        Spacer(modifier = Modifier.height(8.dp))
        Text("Pulsa en 'Jugar' para comenzar la partida")
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {navegacionControlador.navigate("Pantalla_Juego")}) { Text("Jugar") }

    }

}

//Pantalla del juego con las preguntas
@Composable
fun Pantalla_Juego(navegacionControlador: NavHostController){

    var indicePreguntas by remember { mutableStateOf(0) }
    var puntuacion by remember { mutableStateOf(0) }

    val preguntas = listOf(
        Pregunta(
            text = "¿2+2?",
            options = listOf("4","3","2","1"),
            correctIndex = 0
        ),
        Pregunta(
            text = "¿1+1?",
            options = listOf("4","3","2","1"),
            correctIndex = 2
        ),
        Pregunta(
            text = "¿2-1?",
            options = listOf("4","3","2","1"),
            correctIndex = 3
        ),
        Pregunta(
            text = "¿2+1?",
            options = listOf("4","3","2","1"),
            correctIndex = 1
        )
    )

    if (indicePreguntas < preguntas.size) {
        PreguntasPantalla(
            pregunta = preguntas[indicePreguntas],
            siguiente = { respuestaCorrecta ->
                if (respuestaCorrecta) {
                    puntuacion++
                }
                indicePreguntas++
            }
        )
    } else { // Si se acaban las preguntas, navegar a la pantalla final con la puntuación
        LaunchedEffect(Unit) {
            navegacionControlador.navigate("Pantalla_Final/$puntuacion")
        }
    }

}

data class Pregunta(val text: String, val options: List<String>, val correctIndex: Int) {} //Tiene que ser data la clase o no se le puede referenciar bien

//Pantalla del juego con lo que se muestra por pantalla
@Composable
fun PreguntasPantalla(pregunta: Pregunta, siguiente: (Boolean) -> Unit) {

    var opcionContestada by remember { mutableStateOf(-1) }
    var mostrarBotonSiguiente by remember { mutableStateOf(false) }

    LaunchedEffect(pregunta) {
        opcionContestada = -1
        mostrarBotonSiguiente = false
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = pregunta.text, fontSize = 20.sp, fontWeight = FontWeight.Bold)

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            pregunta.options.forEachIndexed { index, option ->
                Button(
                    onClick = {
                        if (opcionContestada == -1) {
                            opcionContestada = index
                            mostrarBotonSiguiente = true
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    enabled = opcionContestada == -1,
                    colors = if (opcionContestada == index && index == pregunta.correctIndex) {
                        ButtonDefaults.buttonColors(containerColor = Color.Green)
                    } else if (opcionContestada == index) {
                        ButtonDefaults.buttonColors(containerColor = Color.Red)
                    } else {
                        ButtonDefaults.buttonColors()
                    }
                ) {
                    Text(text = option)
                }
            }
        }
        if (mostrarBotonSiguiente) {
            Button(
                onClick = { siguiente(opcionContestada == pregunta.correctIndex) },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = "Siguiente Pregunta")
            }
        }
    }
}


//Pantalla del final
@Composable
fun Pantalla_Final(navegacionControlador: NavHostController, puntuacion: Int){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Has terminado el juego del Trivial", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Tu puntuación es: $puntuacion", fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {navegacionControlador.navigate("Pantalla_Inicio")}) { Text("Volver a jugar") }

    }
}