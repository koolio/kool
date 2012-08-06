package io.kool.mongodb

import com.mongodb.DBCollection
import io.kool.stream.Stream
import io.kool.stream.Handler
import io.kool.stream.Cursor
import io.kool.stream.support.AbstractHandler

/**
 * Represents an active collection which is kept to date using replication events
 */
public class ActiveDbCollection(val dbCollection: DBCollection) {
    val handler = ActiveDbCollectionHandler(this)

    public fun isOpened(): Boolean = handler.isOpen()

    public fun isClosed(): Boolean = handler.isClosed()

    public fun toString(): String = "ActiveDbCollection($dbCollection)"

    public fun onReplicaEvent(event: ReplicaEvent) {
        // TODO update the in memory collection
    }
}

class ActiveDbCollectionHandler(val activeCollection: ActiveDbCollection): AbstractHandler<ReplicaEvent>() {

    public override fun toString(): String = "ActiveDbCollectionHandler($activeCollection)"

    public override fun onNext(next: ReplicaEvent) {
        activeCollection.onReplicaEvent(next)
    }

}