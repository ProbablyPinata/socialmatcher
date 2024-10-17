package social

fun standardStrategy(target: User, candidate: User): Boolean =
    !target.hasFriend(candidate)

fun unfriendlyStrategy(target: User, candidate: User): Boolean =
    false

fun limitOfFiveStrategy(target: User, candidate: User): Boolean =
    standardStrategy(target, candidate) && run {
        while (target.currentFriends.size >= 5) {
            target.removeLongestStandingFriend()
        }
        return true
    }

fun interestedInDogsStrategy(target: User, candidate: User): Boolean =
    standardStrategy(target, candidate) &&
        candidate.bio
            .split(" ")
            .map { it.uppercase() }
            .contains("DOG")