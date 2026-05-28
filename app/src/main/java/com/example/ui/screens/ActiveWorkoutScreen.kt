package com.example.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import androidx.navigation.NavController
import com.example.ui.NexusViewModel
import com.example.utils.TTSHelper
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle

import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.rotate
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.LinearEasing

@Composable
fun ActiveWorkoutScreen(
    presetId: Int,
    navController: NavController,
    viewModel: NexusViewModel
) {
    val presets by viewModel.presets.collectAsStateWithLifecycle()
    val preset = presets.find { it.id == presetId }
    
    if (preset == null) {
        LaunchedEffect(Unit) { navController.popBackStack() }
        return
    }

    val exercises = preset.exercisesJson.split("|").filter { it.isNotBlank() }
    
    var currentIndex by remember { mutableStateOf(0) }
    var skipCount by remember { mutableStateOf(0) }
    var elapsedTime by remember { mutableStateOf(0) }
    var isTimerRunning by remember { mutableStateOf(true) }
    
    val context = LocalContext.current
    val ttsHelper = remember { TTSHelper(context) }

    LaunchedEffect(currentIndex) {
        if (currentIndex < exercises.size) {
            ttsHelper.speak("Next task. ${exercises[currentIndex]}")
        }
    }
    
    LaunchedEffect(isTimerRunning) {
        while(isTimerRunning) {
            delay(1000)
            elapsedTime++
        }
    }
    
    DisposableEffect(Unit) {
        onDispose {
            ttsHelper.shutdown()
        }
    }

    if (currentIndex >= exercises.size) {
        // Quest Complete
        LaunchedEffect(Unit) {
            isTimerRunning = false
            ttsHelper.speak("Quest complete. Leveling up system activated.")
            val skipRate = if (exercises.isEmpty()) 0 else (skipCount * 100) / exercises.size
            viewModel.completeWorkout(preset.id, skipRate, elapsedTime, preset.name)
        }
        
        Column(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("QUEST COMPLETED", color = MaterialTheme.colorScheme.primary, fontSize = 28.sp, fontWeight = FontWeight.Black, fontStyle = FontStyle.Italic, letterSpacing = 2.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Duration: ${elapsedTime / 60}m ${elapsedTime % 60}s", color = MaterialTheme.colorScheme.secondary)
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { navController.popBackStack(route = "dashboard", inclusive = false) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("RETURN TO SYSTEM", color = Color.White, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            }
        }
        return
    }

    val currentTask = exercises[currentIndex]

    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(animation = tween(4000, easing = LinearEasing))
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Row(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface).padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.Close, "Quit", tint = MaterialTheme.colorScheme.error)
                }
                Text("${elapsedTime / 60}:${(elapsedTime % 60).toString().padStart(2, '0')}", color = MaterialTheme.colorScheme.secondary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
                    .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha=0.3f), RoundedCornerShape(24.dp))
            ) {
                // Top Right Badge
                Box(modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)) {
                    Text(
                        text = "QUEST: IN PROGRESS",
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primary.copy(alpha=0.2f), RoundedCornerShape(6.dp))
                            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha=0.3f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    
                    // Voice guiding visualizer
                    Box(modifier = Modifier.size(120.dp).padding(bottom = 24.dp), contentAlignment = Alignment.Center) {
                        Box(modifier = Modifier.fillMaxSize().rotate(rotation).border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha=0.5f), CircleShape))
                        Box(modifier = Modifier.size(90.dp).shadow(20.dp, RoundedCornerShape(16.dp), spotColor = MaterialTheme.colorScheme.primary.copy(alpha=0.5f)).background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp)).border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha=0.5f), RoundedCornerShape(16.dp)), contentAlignment = Alignment.Center) {
                            Text("AI VOICE", color = MaterialTheme.colorScheme.secondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Text("TASK ${currentIndex+1}/${exercises.size}", color = MaterialTheme.colorScheme.secondary, fontSize = 12.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Black)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    AnimatedContent(targetState = currentTask, label = "task_anim") { task ->
                        Text(
                            text = task,
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            fontStyle = FontStyle.Italic,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(30.dp)).padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { skipCount++; currentIndex++ }, modifier = Modifier.background(MaterialTheme.colorScheme.surface, CircleShape)) {
                    Icon(Icons.Default.Close, contentDescription = "Skip", tint = MaterialTheme.colorScheme.secondary)
                }
                
                Button(
                    onClick = { currentIndex++ },
                    modifier = Modifier.weight(1f).padding(horizontal = 16.dp).height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("COMPLETE SET", color = Color.White, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                }

                IconButton(onClick = {}, modifier = Modifier.background(MaterialTheme.colorScheme.surface, CircleShape)) {
                    // Decorative for symmetry
                    Box(modifier = Modifier.size(8.dp).background(MaterialTheme.colorScheme.secondary, CircleShape))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
