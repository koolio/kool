package test.kool.stream

import io.kool.stream.*
import org.junit.Test as test

class JoinWindowsTest {

    class WithdrawalEvent(val accountNumber: String, val amount: Double) {
        fun toString() = "WithdrawalEvent($accountNumber, $amount)"
    }

    class FraudWarningEvent(val accountNumber: String, val warning: String) {
        fun toString() = "FraudWarningEvent($accountNumber, $warning)"
    }

    test fun joinWindows() {
        var withdrawList = arrayList(
                WithdrawalEvent("ABC", 10.0),
                WithdrawalEvent("DEF", 20.0),
                WithdrawalEvent("ABC", 10.0),
                WithdrawalEvent("ABC", 30.0),
                WithdrawalEvent("DEF", 10.0),
                WithdrawalEvent("ABC", 50.0),
                WithdrawalEvent("DEF", 10.0)
        )

        val fraudWarningList = arrayList(
                FraudWarningEvent("ABC", "Oh dear"),
                FraudWarningEvent("ABC", "Another issue")
        )

        val withdrawStream = withdrawList.toStream().window(4)
        val fraudStream = fraudWarningList.toStream().window(4)
        val joinStream = withdrawStream.and(fraudStream)

        joinStream.open {
            val withdraws = it.first
            val frauds = it.second
            for (withdraw in withdraws) {
                for (fraud in frauds) {
                    if (withdraw.accountNumber == fraud.accountNumber) {
                        println("${withdraw.accountNumber} has withdrew ${withdraw.amount} with warning ${fraud.warning}")
                    }
                }
            }
        }
        Thread.sleep(10000)
    }
}