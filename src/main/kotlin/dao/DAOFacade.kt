package org.coliver.enterprise.dao

import org.coliver.enterprise.model.Product

interface DAOFacade {
    suspend fun allProducts(): List<Product>
    suspend fun allProductsByChatId(chatId: Long): List<Product>
    suspend fun getAllChatId(): List<Long>
    suspend fun getProduct(id: Long): Product?
    suspend fun addNewProduct(product: Product): Product?
    suspend fun editProduct(product: Product): Boolean
    suspend fun deleteProduct(chatId: Long, id: Long): Boolean
}
