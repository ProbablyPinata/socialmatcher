package social;

import java.util.function.BiFunction;

public final class Matchmaker {
    private final BiFunction<User, User, Boolean> compatibilityChecker;

    public Matchmaker(BiFunction<User, User, Boolean> compatibilityChecker) {
        this.compatibilityChecker = compatibilityChecker;
    }

    public void tryMatching(User user1, User user2) {
        // used to obtain a total order on users:
        User smallerUser;
        User largerUser;

        if (user1.hashCode() < user2.hashCode()) {
            smallerUser = user1;
            largerUser = user2;
        } else {
            smallerUser = user2;
            largerUser = user1;
        }

        // try finally block to ensure that the locks
        // are unlocked even if an exception is thrown
        try {
            smallerUser.getLock().lock();
            largerUser.getLock().lock();
            Boolean compatible = compatibilityChecker.apply(user1, user2);
            if (compatible) {
                user1.considerFriendRequest(user2);
                user2.considerFriendRequest(user1);
            }
        } finally {
            largerUser.getLock().unlock();
            smallerUser.getLock().unlock();
        }
    }
}
