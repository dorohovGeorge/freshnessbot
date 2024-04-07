package org.coliver.enterprise.model

import korlibs.time.fromDays
import kotlinx.datetime.*
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.timestamp
import kotlin.time.Duration

@NoArg
@Serializable
data class Product(
    val id: Long? = null,
    val chatId: Long,
    val productName: String,
    val startUsingDate: Instant,
    val shelfLifeDays: Long
) {
    private val timezone = TimeZone.of("Europe/Moscow")

    private fun isExpired(): Boolean {
        return Clock.System.now() > startUsingDate.plus(Duration.fromDays(shelfLifeDays))
    }

    private fun calcRemainDays(): Int {
        return Clock.System.now()
            .daysUntil(
                startUsingDate.plus(Duration.fromDays(shelfLifeDays)),
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
                .toLocalDateTime(timezone)
                .date
        }
            Статус: $productStatus
        """.trimIndent()
        if (!isExpired) {
            resString += "\n"
            resString += """
            Годен до: ${
                this.startUsingDate
                    .plus(Duration.fromDays(shelfLifeDays))
                    .toLocalDateTime(timezone)
                    .date
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
    val startUsingDate = timestamp("sentAt")
    val shelfLifeDays = long("shelfLifeDays")

    override val primaryKey = PrimaryKey(id)
}
