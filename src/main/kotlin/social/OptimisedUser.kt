package social

import java.util.concurrent.locks.ReentrantLock

class OptimisedUser(
    override val userName: String,
    override val yearOfBirth: Int,
    override val bio: String,
    val befriendingStrategy: (User, User) -> Boolean = ::standardStrategy,
) : User {
    // list to ensure order of establishments known
    private val friends: OrderedMap<String, User> = HashMapLinked()
    override val lock = ReentrantLock()

    init {
        if (yearOfBirth !in 1900..2100) {
            throw IllegalArgumentException()
        }
    }

    override val currentFriends
        get() = friends.values

    override fun hasFriend(possibleFriend: User): Boolean =
        possibleFriend.userName in currentFriends.map { it.userName }

    override fun removeFriend(targetFriend: User): Boolean =
        hasFriend(targetFriend) && friends.remove(targetFriend.userName) != null

    override fun considerFriendRequest(candidateFriend: User): Boolean =
        befriendingStrategy(this, candidateFriend) && run {
            friends[candidateFriend.userName] = candidateFriend
            true
        }

    override fun removeLongestStandingFriend(): User? =
        friends.removeLongestStandingEntry()?.second
}
