package org.coliver.enterprise.model

import kotlinx.datetime.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

@NoArg
@Serializable
data class Product(
    val id: Long? = null,
    val chatId: Long,
    val productName: String,
    val startUsingDate: LocalDateTime,
    val shelfLifeDays: Long
) {
    private val timezone = TimeZone.of("Europe/Moscow")

    private fun isExpired(): Boolean {
        return Clock.System.now()
            .toLocalDateTime(timezone).date >
            startUsingDate.date
                .plus(DatePeriod(days = shelfLifeDays.toInt()))
    }

    fun calcRemainDays(): Int {
        val days = DateTimePeriod(days = shelfLifeDays.toInt())
        val date = startUsingDate.toInstant(timezone).plus(days, timezone)
        return Clock.System.now()
            .daysUntil(
                date,
                timezone
            )
    }

    fun print(): String {
        var resString = ""
        val isExpired = this.isExpired()
        val productStatus = if (isExpired) "Просрочен" else "Свежий"
        resString += """
            Номер: ${this.id}
            Название: ${this.productName}
            Срок годности: ${this.shelfLifeDays} дней
            Дата открытия: ${
            this.startUsingDate
                .date
        }
            Статус: $productStatus
        """.trimIndent()
        if (!isExpired) {
            resString += "\n"
            resString += """
            Годен до: ${
                this.startUsingDate.date
                    .plus(DatePeriod(days = shelfLifeDays.toInt()))
            } (осталось дней: ${this.calcRemainDays()})
            """.trimIndent()
        }
        resString += "\n-----------------\n"
        return resString
    }
}

object Products : Table() {
    val id = long("id").autoIncrement().uniqueIndex()
    val chatId = long("chatId")
    val productName = varchar("name", 64)
    val startUsingDate = datetime("sentAt")
    val shelfLifeDays = long("shelfLifeDays")

    override val primaryKey = PrimaryKey(id)
}
