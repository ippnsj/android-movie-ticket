package woowacourse.movie.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import woowacourse.movie.R
import woowacourse.movie.domain.MovieTicket
import woowacourse.movie.domain.PeopleCount
import woowacourse.movie.domain.TicketTime
import woowacourse.movie.domain.TimesGenerator
import woowacourse.movie.domain.mapToDomainMovie
import woowacourse.movie.ui.dto.Movie
import woowacourse.movie.ui.dto.mapToUIMovieTicket
import woowacourse.movie.ui.getSerializable
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class MovieDetailActivity : AppCompatActivity() {
    private var peopleCount = PeopleCount()
    private lateinit var dateSpinner: Spinner
    private lateinit var timeSpinner: Spinner
    private lateinit var timeSpinnerAdapter: ArrayAdapter<LocalTime>
    private val times = mutableListOf<LocalTime>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        intent.getSerializable<Movie>("movie")?.let {
            setMovieInfo(it)
            setDateSpinner(it)
            setBookingButton(it)
        }
        setTimeSpinner()
        setPeopleCountController()

        loadSavedData(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)

        outState.putInt("date_position", dateSpinner.selectedItemPosition)
        outState.putInt("time_position", timeSpinner.selectedItemPosition)
        outState.putInt("count", peopleCount.count)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setMovieInfo(movie: Movie) {
        findViewById<ImageView>(R.id.detail_poster).setImageResource(movie.poster)
        findViewById<TextView>(R.id.detail_title).text = movie.title
        findViewById<TextView>(R.id.detail_date).text = movie.getScreenDate()
        findViewById<TextView>(R.id.detail_running_time).text = movie.getRunningTime()
        findViewById<TextView>(R.id.detail_description).text = movie.description
    }

    private fun Movie.getScreenDate(): String = getString(R.string.screen_date, startDate.format(), endDate.format())

    private fun LocalDate.format(): String = format(DateTimeFormatter.ofPattern(getString(R.string.date_format)))

    private fun Movie.getRunningTime(): String = getString(R.string.running_time, runningTime)

    private fun setDateSpinner(movie: Movie) {
        dateSpinner = findViewById(R.id.detail_date_spinner)
        val dateSpinnerAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            mapToDomainMovie(movie).getDatesBetweenTwoDates()
        )
        dateSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dateSpinner.adapter = dateSpinnerAdapter
        dateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                timeSpinner.setSelection(0)
                times.clear()
                times.addAll(TimesGenerator.getTimesByDate(dateSpinner.selectedItem as LocalDate))
                timeSpinnerAdapter.notifyDataSetChanged()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    private fun setTimeSpinner() {
        timeSpinner = findViewById(R.id.detail_time_spinner)
        times.addAll(TimesGenerator.getTimesByDate(dateSpinner.selectedItem as LocalDate))
        timeSpinnerAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            times
        )
        timeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        timeSpinner.adapter = timeSpinnerAdapter
    }

    private fun setPeopleCountController() {
        val peopleCountView = findViewById<TextView>(R.id.detail_people_count)
        setPeopleCountView(peopleCountView)
        setMinusButton(peopleCountView)
        setPlusButton(peopleCountView)
    }

    private fun setPeopleCountView(peopleCountView: TextView) {
        peopleCountView.text = "${peopleCount.count}"
    }

    private fun setMinusButton(peopleCountView: TextView) {
        val minusButton = findViewById<Button>(R.id.detail_minus_button)
        minusButton.setOnClickListener {
            peopleCount = peopleCount.minusCount()
            setPeopleCountView(peopleCountView)
        }
    }

    private fun setPlusButton(peopleCountView: TextView) {
        val plusButton = findViewById<Button>(R.id.detail_plus_button)
        plusButton.setOnClickListener {
            peopleCount = peopleCount.plusCount()
            setPeopleCountView(peopleCountView)
        }
    }

    private fun setBookingButton(movie: Movie) {
        val bookingButton = findViewById<Button>(R.id.detail_booking_button)

        bookingButton.setOnClickListener {
            moveToTicketActivity(movie)
        }
    }

    private fun moveToTicketActivity(movie: Movie) {
        val ticket = MovieTicket(
            movie.title,
            TicketTime(LocalDateTime.of(dateSpinner.selectedItem as LocalDate, timeSpinner.selectedItem as LocalTime)),
            peopleCount
        )

        val intent = Intent(this, MovieTicketActivity::class.java)
        intent.putExtra("ticket", mapToUIMovieTicket(ticket))
        startActivity(intent)
    }

    private fun loadSavedData(savedInstanceState: Bundle?) {
        val datePosition = savedInstanceState?.getInt("date_position") ?: 0
        val timePosition = savedInstanceState?.getInt("time_position") ?: 0
        val count = savedInstanceState?.getInt("count") ?: 1
        dateSpinner.setSelection(datePosition)
        timeSpinner.setSelection(timePosition)
        peopleCount = PeopleCount(count)
    }
}