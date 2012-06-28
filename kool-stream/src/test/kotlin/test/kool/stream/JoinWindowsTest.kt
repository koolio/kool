package test.kool.stream

import io.kool.stream.*
import java.util.*

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

        // TODO compile error if you miss out these unnecesasry type expressions
        // would be nice to remove them ASAP as soon as Kotlin's fixed the issue
        val withdrawStream: Stream<List<WithdrawalEvent>> = withdrawList.toStream().window(4)

        val fraudStream: Stream<List<FraudWarningEvent>> = fraudWarningList.toStream().window(4)
        val joinStream: Stream<#(List<WithdrawalEvent>, List<FraudWarningEvent>)> = withdrawStream.and(fraudStream)

         joinStream.open {
            val withdraws = it._1
            val frauds = it._2
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