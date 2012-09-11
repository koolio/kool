package test.kool.stream

import io.kool.stream.Cursor
import kotlin.test.assertTrue

fun Cursor.assertClosed(): Unit {
    assertTrue(this.isClosed(), "This cursor should be closed! $this")
}