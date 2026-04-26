package jobplace.courseproject.monitorstorm.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import jobplace.courseproject.monitorstorm.viewmodel.StormViewModel

@Composable
fun StormScreen(vm: StormViewModel) {

    val list by vm.data.collectAsState()

    val latest = list.firstOrNull()?.value ?: 0.0

    Column(
        Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (latest.toInt() == 1) {
            Text("Сейчас : $latest балл",color = Color.Gray)
        }
        if (latest.toInt() == 2 || latest.toInt() == 3 || latest.toInt() == 4) {
            Text("Сейчас : $latest балла", color = Color.DarkGray)
        }
        if (latest.toInt() == 5 || latest.toInt() == 6 || latest.toInt() == 7||
            latest.toInt() == 8 || latest.toInt() == 9) {
            if(latest.toInt() == 5 || latest.toInt() == 6 ) {
                Text("Сейчас : $latest баллов", color = Color.Yellow)
            }
            if(latest.toInt() == 7 || latest.toInt() == 8 ) {
                Text("Сейчас : $latest баллов", color = Color.Red)
            }
            if(latest.toInt() == 9) {
                Text("Сейчас : $latest баллов", color = Color.Magenta)
            }

        }
        Spacer(modifier = Modifier.height(20.dp))
        ChartView(list)
    }
}
