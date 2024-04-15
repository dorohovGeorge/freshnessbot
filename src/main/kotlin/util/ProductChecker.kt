package org.coliver.enterprise.util

import org.coliver.enterprise.model.Product

fun List<Product>.checkFreshness(): List<Product> {
    val expiredProducts = mutableListOf<Product>()
    this.forEach { product ->
        if (product.calcRemainDays() <= 1) {
            expiredProducts.add(product)
        }
    }
    return expiredProducts
}
