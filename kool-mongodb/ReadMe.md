## Kool MongoDb

** Kool MongoDb ** provides

* a number of Kotlin extension functions for working with [MongoDb](http://www.mongodb.org/)
* support for [Kool Streams](http://kool.io/streams.html) and [MongoDb Replica Sets](http://www.mongodb.org/display/DOCS/Replica+Set+Tutorial) so that you can process database change events in MongoDb using [Kool Streams](http://kool.io/streams.html)

### Using Replica Sets

MongoDb uses [Replica Sets](http://www.mongodb.org/display/DOCS/Replica+Set+Tutorial) to be able to replicate changes from a master to slaves or other masters.

To enable replicate sets run mongodb with the **--replSet** option specified.

    mongod --replSet foo

Then in the mongo command shell type somethign like this: (depending on how many members you wish to define in your replicate set and where your master is)

    config = {_id: 'foo', members: [{_id: 0, host: 'localhost:27017'}] }
    rs.initiate(config);
