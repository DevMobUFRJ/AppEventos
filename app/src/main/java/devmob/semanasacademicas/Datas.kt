package devmob.semanasacademicas

import java.util.*

operator fun ClosedRange<Calendar>.iterator() = CalendarRangeIterator(start, endInclusive)

class CalendarRangeIterator(val start:Calendar, val endInclusive: Calendar): Iterator<Calendar>{
    private var atual = start
    override fun next(): Calendar{
        val next = Calendar.getInstance()
        next.time = atual.time
        atual.add(Calendar.DATE, 1)
        return next
    }

    override fun hasNext() = atual <= endInclusive
}