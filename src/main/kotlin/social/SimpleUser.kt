package social

import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

class SimpleUser(
    override val userName: String,
    override val yearOfBirth: Int,
    override val bio: String,
    val befriendingStrategy: (User, User) -> Boolean = ::standardStrategy,
) : User {
    // list to ensure order of establishments known
    private val friends = mutableListOf<User>()
    override val lock = ReentrantLock()

    init {
        if (yearOfBirth !in 1900..2100) {
            throw IllegalArgumentException()
        }
    }

    override val currentFriends
        get() = friends.toList()

    override fun hasFriend(possibleFriend: User): Boolean =
        possibleFriend.userName in friends.map { it.userName }

    override fun removeFriend(targetFriend: User): Boolean =
        hasFriend(targetFriend) && friends.removeAll(listOf(targetFriend))

    override fun considerFriendRequest(candidateFriend: User): Boolean =
        befriendingStrategy(this, candidateFriend) &&
                friends.add(candidateFriend)

    override fun removeLongestStandingFriend(): User? =
            friends.removeFirstOrNull()
}