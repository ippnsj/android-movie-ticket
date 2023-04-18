package woowacourse.movie.ui.activity

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import woowacourse.movie.R
import woowacourse.movie.ui.dto.Movie
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MovieListAdapter(
    private val context: Context,
    private val movies: List<Movie>,
    private val itemButtonClickListener: ItemButtonClickListener,
) : BaseAdapter() {
    interface ItemButtonClickListener {
        fun onClick(position: Int)
    }

    override fun getCount(): Int = movies.size

    override fun getItem(position: Int): Any = movies[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val itemView: View
        val viewHolder: ViewHolder
        if (convertView == null) {
            itemView = LayoutInflater.from(parent?.context).inflate(R.layout.movie_item, parent, false)
            viewHolder = ViewHolder(itemView)
            itemView.tag = viewHolder
        } else {
            itemView = convertView
            viewHolder = itemView.tag as ViewHolder
        }

        setViewContent(position, viewHolder)

        return itemView
    }

    private fun setViewContent(
        position: Int,
        viewHolder: ViewHolder
    ) {
        val movie = movies[position]
        viewHolder.moviePoster.setImageResource(movie.poster)
        viewHolder.movieTitle.text = movie.title
        viewHolder.movieDate.text = movie.getScreenDate()
        viewHolder.movieTime.text = movie.getRunningTime()
        viewHolder.bookingButton.setOnClickListener { itemButtonClickListener.onClick(position) }
    }

    private fun Movie.getScreenDate(): String = context.getString(R.string.screen_date, startDate.format(), endDate.format())

    private fun LocalDate.format(): String = format(DateTimeFormatter.ofPattern(context.getString(R.string.date_format)))

    private fun Movie.getRunningTime(): String = context.getString(R.string.running_time, runningTime)

    class ViewHolder(view: View) {
        val moviePoster: ImageView = view.findViewById(R.id.item_poster)
        val movieTitle: TextView = view.findViewById(R.id.item_title)
        val movieDate: TextView = view.findViewById(R.id.item_date)
        val movieTime: TextView = view.findViewById(R.id.item_running_time)
        val bookingButton: Button = view.findViewById(R.id.item_booking_button)
    }
}