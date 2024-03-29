package com.example.groupproject.data.relations;

import com.example.groupproject.data.user.User;

public class Relationship implements Comparable{
    private User sender;
    private User recipiant;
    private RelationshipStatus status;

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

    public void setStatus(RelationshipStatus rs)
    {
        this.status = rs;
    }

    public boolean isVisible()
    {
        /**
         * See RelationshipStatus for details.
         */
        return this.status.compareTo(RelationshipStatus.VISIBLE) >= 0;
    };

    public boolean isPending()
    {
        /**
         * See RelationshipStatus for details.
         */
        return (this.status == RelationshipStatus.PENDING_FOLLOWING || this.status == RelationshipStatus.PENDING_VISIBLE);
    };
}
