package social

import java.util.concurrent.locks.Lock

interface User {
    val userName: String
    val yearOfBirth: Int
    val bio: String
    val currentFriends: List<User>
    val lock: Lock

    fun hasFriend(possibleFriend: User): Boolean
    fun removeFriend(targetFriend: User): Boolean
    fun considerFriendRequest(candidateFriend: User): Boolean
    fun removeLongestStandingFriend(): User?
}