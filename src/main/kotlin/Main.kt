package org.coliver.enterprise

import dev.inmo.tgbotapi.bot.ktor.telegramBot
import dev.inmo.tgbotapi.extensions.api.bot.getMe
import dev.inmo.tgbotapi.extensions.api.bot.setMyCommands
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.buildBehaviourWithLongPolling
import dev.inmo.tgbotapi.extensions.behaviour_builder.expectations.waitText
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import dev.inmo.tgbotapi.requests.send.SendTextMessage
import dev.inmo.tgbotapi.types.BotCommand
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock
import org.coliver.enterprise.dao.dao
import org.coliver.enterprise.databasse.ConnectionParams
import org.coliver.enterprise.databasse.DatabaseFactory
import org.coliver.enterprise.model.Product


val TOKEN = "6950263144:AAFwyPILBFwomEAt84qOOiMHBMS00zOJv8k"

val products = mutableListOf<Product>()
val databaseParams = ConnectionParams(
    url = "jdbc:postgresql://193.149.190.120:5432/tgbot",
    user = "postgres",
    password = "402012Ehodas"
)

suspend fun main() {
    DatabaseFactory.init(databaseParams)

    val bot = telegramBot(TOKEN)
    bot.setMyCommands(
        listOf(
            BotCommand("add", "Добавить продукт"),
            BotCommand("delete", "Удалить продукт"),
            BotCommand("all", "Вывести все продукты")
        )
    )
    bot.buildBehaviourWithLongPolling {
        println(getMe())
        onCommand("start") {
            val text = """
                Всем привет! Этот бот создан для контроля за свежестью продуктов в вашем холодильнике
                Доступные комманды
                1) /add - добавление продукта
                2) /delete - удаление продукта
                3) /all - посмотреть список продуктов текущих
            """.trimIndent()
            sendTextMessage(it.chat.id, text)
        }

        onCommand("add") {
            val name = waitText(
                SendTextMessage(it.chat.id, "Напишите название продукта")
            ).first()
            val shelfLifeDays = waitText(
                SendTextMessage(it.chat.id, "Напишите сколько хранится продукт (в днях)")
            ).first()
            val date = Clock.System.now()
            val product = Product(
                chatId = it.chat.id.chatId.long,
                productName = name.text,
                startUsingDate = date,
                shelfLifeDays = shelfLifeDays.text.toLong()
            )

            val addedProduct = dao.addNewProduct(product)
            if (addedProduct != null) {
                sendTextMessage(it.chat.id, "Добавили продукт:\n${addedProduct.print()}")
            } else {
                sendTextMessage(it.chat.id, "Не удалось добавить продукт")
            }
        }
        onCommand("delete") {
            val id = waitText(
                SendTextMessage(it.chat.id, "Напишите номер продукта для удаления")
            ).first()

            if (dao.deleteProduct(it.chat.id.chatId.long, id.text.toLong()))
                sendTextMessage(it.chat.id, "Продукт с номером ${id.text.toLong()} удален")
        }
        onCommand("all") {
            val allProducts = dao.allProductsByChatId(it.chat.id.chatId.long)
            if(allProducts.isEmpty()) {
                sendTextMessage(it.chat.id, "Продуктов нет")
            } else {
                sendTextMessage(it.chat.id, allProducts.print())
            }
        }
    }.join()
}


fun List<Product>.print(): String {
    var answer = ""

    this.forEach {
        answer += it.print()
    }
    return answer
}
