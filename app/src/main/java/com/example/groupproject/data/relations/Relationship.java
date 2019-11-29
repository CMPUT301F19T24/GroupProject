package com.example.groupproject.data.relations;

import com.example.groupproject.data.user.User;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class Relationship implements Comparable{
    private User sender;
    private User recipiant;
    private RelationshipStatus status;
    private QueryDocumentSnapshot document;

    public Relationship(User sender, User recipiant, RelationshipStatus status)
    {
        this.sender = sender;
        this.recipiant = recipiant;
        this.status = (status == null) ? RelationshipStatus.INVISIBLE : status;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }

    @Override
    public String toString()
    {
        return "Sender: " + this.getSender().getUserName() + "\nReceiver: " + this.getRecipiant().getUserName() + "\nStatus: " + this.getStatus().toString();
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

    public void setSender(User sender) {
        this.sender = sender;
    }

    public void setRecipiant(User recipiant) {
        this.recipiant = recipiant;
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
        return this.status == RelationshipStatus.FOLLOWING;
    };

    public boolean isPending()
    {
        /**
         * See RelationshipStatus for details.
         */
        return this.status == RelationshipStatus.PENDING;
    }

    public String getDocumentId() {
        if (document != null){
            return document.getId();
        }
        return null;
    }

    public QueryDocumentSnapshot getDocument() {
        return document;
    }

    public void setDocument(QueryDocumentSnapshot document) {
        this.document = document;
    }
}
