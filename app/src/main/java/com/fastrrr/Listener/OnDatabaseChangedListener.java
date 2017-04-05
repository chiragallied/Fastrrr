package com.fastrrr.Listener;

/**
 * Created by Allied on 04-Apr-17.
 */

public abstract class OnDatabaseChangedListener {
    public abstract void onNewDatabaseEntryAdded();
    public abstract void onDatabaseEntryRenamed();
}
