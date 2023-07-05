package de.babiesjulius.smoothiepos.backend.objects

data class Product(var id: String?, val name: String, val price: Int, val description: String?, val image: String?, var ingredients: List<String> = listOf(), var available: Boolean = false) {
    override fun equals(other: Any?): Boolean {
        if (other !is Product) return false
        return this.id == other.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}