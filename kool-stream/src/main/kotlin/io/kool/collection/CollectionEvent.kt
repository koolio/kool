package io.kool.collection

import java.util.EventObject

/**
* Represents an event on a collection
*/
public class CollectionEvent<T>(val collection: ObservableCollection<T>, val element: T, val kind: Int): EventObject(collection) {

    fun isAdd() = kind == Add

    fun isUpdate() = kind == Update

    fun isRemove() = kind == Remove

    val kindText: String
    get() {
        return if (kind == Add) "Add"
        else if (kind == Update) "Update"
        else if (kind == Remove) "Remove"
        else "Unknown"
    }

    public class object {
        val Add = 1
        val Update = 2
        val Remove = 3
    }
}

