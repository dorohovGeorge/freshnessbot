package org.coliver.enterprise.dao

import kotlinx.coroutines.runBlocking
import org.coliver.enterprise.databasse.DatabaseFactory.dbQuery
import org.coliver.enterprise.model.Product
import org.coliver.enterprise.model.Products
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

class DAOFacadeImpl : DAOFacade {
    private fun resultRowToProduct(row: ResultRow) = Product(
        id = row[Products.id],
        chatId = row[Products.chatId],
        productName = row[Products.productName],
        startUsingDate = row[Products.startUsingDate],
        shelfLifeDays = row[Products.shelfLifeDays]
    )

    override suspend fun allProducts(): List<Product> = dbQuery {
        transaction {
            Products.selectAll().map(::resultRowToProduct)
        }
    }

    override suspend fun allProductsByChatId(chatId: Long): List<Product> = dbQuery {
        transaction {
            Products.select(Products.chatId eq chatId)
                .map(::resultRowToProduct)
        }
    }

    override suspend fun getProduct(id: Long): Product? {
        TODO("Not yet implemented")
    }

    override suspend fun addNewProduct(product: Product): Product? = dbQuery {
        transaction {
            val insertStatement = Products.insert {
                it[chatId] = product.chatId
                it[productName] = product.productName
                it[shelfLifeDays] = product.shelfLifeDays
                it[startUsingDate] = product.startUsingDate
            }
            insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToProduct)
        }
    }

    override suspend fun editProduct(product: Product): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun deleteProduct(chatId: Long, id: Long, ): Boolean = dbQuery {
        transaction {
            Products.deleteWhere { (Products.chatId eq chatId) and (Products.id eq id) } > 0
        }
    }
}

val dao: DAOFacade = DAOFacadeImpl().apply {
    runBlocking {
//        val product = Product(
//            chatId = 437706618,
//            productName = "Mayonese",
//            startUsingDate = LocalDateTime.now(),
//            shelfLifeDays = 10
//        )
//        if (allProducts().isEmpty()) {
//            addNewProduct(product)
//        }
    }
}
