package org.coliver.enterprise.model

import kotlinx.serialization.Serializable
import org.coliver.enterprise.serialize.LocalDateTimeSerializer
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@NoArg
@Serializable
data class Product(
    val id: Long? = null,
    val chatId: Long,
    val productName: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    val startUsingDate: LocalDateTime,
    val shelfLifeDays: Long
) {
    private fun isExpired(): Boolean {
        return LocalDateTime.now() > startUsingDate.plusDays(shelfLifeDays)
    }

    private fun calcRemainDays(): Long {
        return LocalDateTime.now().until(startUsingDate.plusDays(shelfLifeDays), ChronoUnit.DAYS) + 1
    }

    fun print(): String {
        var resString = ""
        val isExpired = this.isExpired()
        val productStatus = if (isExpired) "Просрочен" else "Свежий"
        resString += """
            Номер: ${this.id}
            Название: ${this.productName}
            Срок годности: ${this.shelfLifeDays} дней
            Дата открытия: ${this.startUsingDate.toLocalDate()}
            Статус: $productStatus
        """.trimIndent()
        if (!isExpired) {
            resString += "\n"
            resString += """
            Годен до: ${this.startUsingDate.plusDays(shelfLifeDays).toLocalDate()} (дней: ${this.calcRemainDays()} )
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
    val startUsingDate =  datetime("sentAt")
    val shelfLifeDays = long("shelfLifeDays")

    override val primaryKey = PrimaryKey(id)
}
