package jobplace.courseproject.monitorstorm.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import jobplace.courseproject.monitorstorm.data.db.KpEntity
import jobplace.courseproject.monitorstorm.data.model.KpIndex

@Composable
fun ChartView(list: List<KpEntity>) {

    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(500.dp),

        factory = { context ->
            LineChart(context)
        },

        update = { chart ->
            val entries = list.mapIndexed { i, item ->
                Entry(i.toFloat(), item.value.toFloat())
            }

            val dataSet = LineDataSet(entries, "Kp Index")

            chart.data = LineData(dataSet)
            chart.notifyDataSetChanged()
            chart.invalidate()
        }
    )
}