package social

interface OrderedMap<K, V> {
    val values: List<V>

    val size: Int

    operator fun set(key: K, value: V): V?

    fun containsKey(key: K): Boolean

    fun remove(key: K): V?

    fun removeLongestStandingEntry(): Pair<K, V>?
}
