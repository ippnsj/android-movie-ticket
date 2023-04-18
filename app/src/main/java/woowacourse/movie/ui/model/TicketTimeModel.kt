package woowacourse.movie.ui.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import woowacourse.movie.domain.TicketTime
import java.time.LocalDateTime

fun mapToTicketTimeModel(ticketTime: TicketTime): TicketTimeModel {
    return TicketTimeModel(
        ticketTime.dateTime
    )
}

@Parcelize
data class TicketTimeModel(val dateTime: LocalDateTime) : Parcelable
