package com.example.tiptime

import junit.framework.Assert.assertEquals
import org.junit.Test
import java.text.NumberFormat
import java.util.Locale

class TipCalculatorTests {


    //@Test 주석을 달면 컴파일러에 메서드가 테스트 메서드임을 알리고 적절하게 실행된다
    //함수 이름에 테스트의 내용과 예상 결과가 명확하게 설명되어 있어야 한다
    @Test
    fun calculationTip_20PercentNoRoundUp() {
        val amount = 10.00
        val tipPercent = 20.00
        //실제로 계산했을 때 나와야 하는 값 (10 * 0.2 = 2)
        val expectedTip = NumberFormat.getCurrencyInstance(Locale.US).format(2)
        //내가 만든 함수로 계산한 값
        val actualTip = calculateTip(amount = amount, tipPercent = tipPercent, roundUp = false)
        //두 값이 같은지 확인, JUnit 라이브러리에 있다
        assertEquals(expectedTip, actualTip)
    }

}