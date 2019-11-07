package ru.vyakhirev_m.myapplication


import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private val TAG = "HomeActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val adapter = TrainsListAdapter()

        trainsRecyclerView.layoutManager = LinearLayoutManager(this)
        trainsRecyclerView.adapter = adapter


        fun LoadTrains(){
            adapter.clearAll()
            GlobalScope.launch {
                val response = getData()

                withContext(Dispatchers.Main) {
                    for (train in response) {
                        if (train.departure!! > (SimpleDateFormat("HH:mm").format(Date())))
                            adapter.add(
                                TrainItem(
                                    train.departure,
                                    train.arrival,
                                    train.thread?.transport_subtype?.title,
                                    train.thread?.title,
                                    train.thread?.express_type
                                )
                            )
                    }
                }
            }
        }
        SearchButton.setOnClickListener {LoadTrains()}
    }

    private fun makeService(): TrainsInterface {
        val builder = Retrofit.Builder()
            .baseUrl("https://api.rasp.yandex.net/v3.0/")
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())

        return builder.build().create(TrainsInterface::class.java)

    }

    suspend fun getData(): List<Segment> {
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val currentDate = sdf.format(Date())
        Log.d(TAG, SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()))

        val fromStation:String=FromEditText.text.toString()
        val toStation:String=ToEditText.text.toString()

        val response = makeService().getElektrichki(
            BuildConfig.YandexApiKey,//get your token here https://tech.yandex.ru/rasp/raspapi/
            fromStation,//Odintsovo station
            toStation,//Belorusskiy railway station
            currentDate,
            "suburban",
            150
        ).await()
        return response.segments!!
    }
}
