package pe.andy.bookholic.util

class CollectionUtil {

    companion object {

        @JvmStatic
        fun <T> listOf(vararg ts: T): List<T> {
            return kotlin.collections.listOf(*ts)
        }

        @JvmStatic
        fun <T> mutableListOf(vararg ts: T): List<T> {
            return kotlin.collections.mutableListOf(*ts)
        }

        @JvmStatic
        fun <K, V> mapOf(k1: K, v1: V): Map<K, V> {
            return kotlin.collections.mapOf(k1 to v1)
        }

        @JvmStatic
        fun <K, V> mapOf(k1: K, v1: V,
                         k2: K, v2: V): Map<K, V> {
            return kotlin.collections.mapOf(
                    k1 to v1,
                    k2 to v2)
        }

        @JvmStatic
        fun <K, V> mapOf(k1: K, v1: V,
                         k2: K, v2: V,
                         k3: K, v3: V): Map<K, V> {
            return kotlin.collections.mapOf(
                    k1 to v1,
                    k2 to v2,
                    k3 to v3)
        }

        @JvmStatic
        fun <K, V> mapOf(k1: K, v1: V,
                         k2: K, v2: V,
                         k3: K, v3: V,
                         k4: K, v4: V): Map<K, V> {
            return kotlin.collections.mapOf(
                    k1 to v1,
                    k2 to v2,
                    k3 to v3,
                    k4 to v4)
        }

        @JvmStatic
        fun <K, V> mapOf(k1: K, v1: V,
                         k2: K, v2: V,
                         k3: K, v3: V,
                         k4: K, v4: V,
                         k5: K, v5: V): Map<K, V> {
            return kotlin.collections.mapOf(
                    k1 to v1,
                    k2 to v2,
                    k3 to v3,
                    k4 to v4,
                    k5 to v5)
        }

        @JvmStatic
        fun <K, V> mapOf(k1: K, v1: V,
                         k2: K, v2: V,
                         k3: K, v3: V,
                         k4: K, v4: V,
                         k5: K, v5: V,
                         k6: K, v6: V): Map<K, V> {
            return kotlin.collections.mapOf(
                    k1 to v1,
                    k2 to v2,
                    k3 to v3,
                    k4 to v4,
                    k5 to v5,
                    k6 to v6)
        }
    }

}