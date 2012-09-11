package io.kool.collection

/**
* Represents an observable collection
*/
public trait ObservableCollection<T>: MutableCollection<T> {

    public fun addCollectionEventListener(listener: CollectionEventListener<T>): Unit

    public fun removeCollectionEventListener(listener: CollectionEventListener<T>): Unit

}

