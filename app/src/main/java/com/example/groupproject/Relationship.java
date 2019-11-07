package com.example.groupproject;

class Relationship implements Comparable{
    User sender;
    User recipiant;
    RelationshipStatus status;

    public Relationship(User sender, User recipiant, RelationshipStatus status)
    {
        this.sender = sender;
        this.recipiant = recipiant;
        this.status = (status == null) ? RelationshipStatus.VISIBLE : status;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }

    public User getSender()
    {
        return this.sender;
    }

    public User getRecipiant()
    {
        return this.recipiant;
    }

    public RelationshipStatus getStatus()
    {
        return this.status;
    }

    public boolean isVisible()
    {
        /**
         * See RelationshipStatus for details.
         */
        return this.status.compareTo(RelationshipStatus.VISIBLE) >= 0;
    };
}
