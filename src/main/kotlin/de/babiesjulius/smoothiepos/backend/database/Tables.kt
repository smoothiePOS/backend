package de.babiesjulius.smoothiepos.backend.database

class Tables {
    companion object {
        @JvmStatic val PRODUCT = "product"
        @JvmStatic val INGREDIENT = "ingredient"
        @JvmStatic val INGREDIENTS = "ingredients"
        @JvmStatic val ORDER = "orders"
        @JvmStatic val ORDER_DETAIL = "order_detail"
        @JvmStatic val CASHPOINT = "cashpoint"
    }
}