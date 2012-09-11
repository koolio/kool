package io.kool.stream.support

import java.util.concurrent.Executor

/**
* A simple synchronous implementation of [[Executor]]
*/
class SynchronousExecutor : Executor {
    public override fun execute(command: Runnable?) {
        if (command != null) {
            command.run()
        }
    }

}