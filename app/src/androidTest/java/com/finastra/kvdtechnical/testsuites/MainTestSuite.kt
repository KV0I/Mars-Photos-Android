package com.finastra.kvdtechnical.testsuites

import com.finastra.kvdtechnical.network.ApiIsolationTest
import org.junit.runner.RunWith
import org.junit.runners.Suite


@RunWith(Suite::class)
@Suite.SuiteClasses(
    ApiIsolationTest::class
)
class MainTestSuite {
}