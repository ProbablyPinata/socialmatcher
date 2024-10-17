package social

private const val INITIAL_BUCKETS = 8
private const val LOAD_FACTOR = 0.75

class HashMapLinked<K, V> : OrderedMap<K, V> {

    private class Node<K, V>(
        val key: K,
        var value: V,
        var prev: Node<K, V>?,
        var next: Node<K, V>? = null,
    )

    private var head: Node<K, V>? = null
    private var tail: Node<K, V>? = null

    private var buckets: MutableList<MutableList<Node<K, V>>>

    init {
        buckets = mutableListOf()
        for (i in 0..<INITIAL_BUCKETS) {
            buckets.add(mutableListOf())
        }
    }

    override var size = 0
        private set

    override val values: List<V>
        get() {
            val allNodes = mutableListOf<V>()
            var currentNode = head

            while (currentNode != null) {
                allNodes.add(currentNode.value)
                currentNode = currentNode.next
            }
            return allNodes.toList()
        }

    override fun containsKey(key: K): Boolean =
        key in getBucket(key).map { it.key }

    override fun remove(key: K): V? {
        if (!containsKey(key)) {
            return null
        }

        // non-null assertion since we know the node exists
        val nodeToRemove = getBucket(key).find { it.key == key }!!
        getBucket(key).remove(nodeToRemove)
        size--

        val prevNode = nodeToRemove.prev
        val nextNode = nodeToRemove.next

        // We recognize two cases:

        // CASE 1: nodeToRemove is neither head nor tail, that is
        // there are nodes present before and after nodeToRemove
        // Here head and tail will not be updated

        // CASE 2: nodeToRemove is head or tail, where
        // the two subcases 2A, 2B deal with head and tail
        // appropriately. Notice that 2A and 2B can both
        // occur, in the case where the nodeToRemove is
        // both head and tail

        // CASE 1: nodeToRemove is neither head nor tail
        if (prevNode != null && nextNode != null) {
            prevNode.next = nextNode
            nextNode.prev = prevNode
        } else {
            if (nodeToRemove == head) {
                // CASE 2A: node is head
                nextNode?.prev = null
                head = nextNode
            }
            if (nodeToRemove == tail) {
                // CASE 2B: node is tail
                prevNode?.next = null
                tail = prevNode
            }
        }

        return nodeToRemove.value
    }

    override fun set(key: K, value: V): V? {
        val maybeOldValue = remove(key)
        val newNode = Node(key, value, tail, null)
        getBucket(key).add(newNode)

        if (head == null) {
            head = newNode
        }
        tail?.next = newNode
        tail = newNode

        size++
        resize()

        return maybeOldValue
    }

    override fun removeLongestStandingEntry(): Pair<K, V>? {
        return if (head == null) {
            null
        } else {
            val key = head!!.key
            val maybeValue = remove(key)
            if (maybeValue != null) {
                Pair(key, maybeValue)
            } else {
                null
            }
        }
    }

    private fun getBucket(key: K) = buckets[key.hashCode().mod(buckets.size)]

    private fun resize() {
        if (size <= LOAD_FACTOR * buckets.size) {
            return
        }
        val allContent = mutableListOf<Node<K, V>>()
        for (bucket in buckets) {
            allContent.addAll(bucket)
        }

        val newNumBuckets = buckets.size * 2

        buckets = mutableListOf()
        for (i in 0..<newNumBuckets) {
            buckets.add(mutableListOf())
        }

        for (node in allContent) {
            getBucket(node.key).add(node)
        }
    }
}
