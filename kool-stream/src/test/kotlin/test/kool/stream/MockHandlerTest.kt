package test.kool.stream

import io.kool.stream.*
import io.kool.stream.support.DefaultCursor
import kotlin.test.*
import org.junit.Test as test

class MockHandlerTest {

    test fun mockHandlerWorks() {
        val handler = MockHandler<String>().expectReceive(2)
        val cursor = DefaultCursor()
        handler.onOpen(cursor)
        handler.assertWaitForOpen()

        handler.onNext("a")
        handler.onNext("b")
        handler.assertExpectations(10)

        handler.onComplete()
        handler.assertWaitForClose(10)
    }

    test fun mockHandlerFails() {
        val handler = MockHandler<String>().expectReceive(1)
        val cursor = DefaultCursor()
        handler.onOpen(cursor)
        handler.assertWaitForOpen()

        fails {
            handler.assertExpectations(10)
        }
    }

    test fun mockFailsToOpen() {
        val handler = MockHandler<String>().expectReceive(1)
        fails {
            handler.assertWaitForOpen(10)
        }
    }

    test fun mockFailsToClose() {
        val handler = MockHandler<String>().expectReceive(1)
        val cursor = DefaultCursor()
        handler.onOpen(cursor)
        handler.assertWaitForOpen()

        fails {
            handler.assertWaitForClose(10)
        }
    }



}