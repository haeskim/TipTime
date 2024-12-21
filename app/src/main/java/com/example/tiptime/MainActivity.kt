/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.tiptime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tiptime.ui.theme.TipTimeTheme
import java.text.NumberFormat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            TipTimeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    TipTimeLayout()
                }
            }
        }
    }
}

@Composable
fun TipTimeLayout() {
    var amountInput by remember { mutableStateOf("")}//청구 금액에 관한 앱의 상태
    var tipInput by remember { mutableStateOf("") }

    val tipPercent = tipInput.toDoubleOrNull() ?: 0.0
    val amount = amountInput.toDoubleOrNull() ?: 0.0//문자열을 double 숫자로 파싱한 결과 또는 null을 반환
    val tip = calculateTip(amount, tipPercent)

    Column(
        modifier = Modifier
            .statusBarsPadding()
            .padding(horizontal = 40.dp)
            .safeDrawingPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.calculate_tip),
            modifier = Modifier
                .padding(bottom = 16.dp, top = 40.dp)
                .align(alignment = Alignment.Start)
        )
        EditNumberField(
            label = R.string.bill_amount,//EditNumberField를 재사용하기 위해 label을 매개변수로 준다
            value = amountInput,
            onValueChange = { amountInput = it },
            modifier = Modifier
                .padding(bottom = 32.dp)
                .fillMaxWidth()
        )
        EditNumberField(
            label = R.string.how_was_the_service,
            value = tipInput,
            onValueChange = { tipInput = it },
            modifier = Modifier
                .padding(bottom = 32.dp)
                .fillMaxWidth()
        )
        Text(
            text = stringResource(R.string.tip_amount, tip),
            style = MaterialTheme.typography.displaySmall
        )
        Spacer(modifier = Modifier.height(150.dp))
    }
}

/**
 * Calculates the tip based on the user input and format the tip amount
 * according to the local currency.
 * Example would be "$10.00".
 */
private fun calculateTip(amount: Double, tipPercent: Double = 15.0): String {
    val tip = tipPercent / 100 * amount
    return NumberFormat.getCurrencyInstance().format(tip)
}

/**
 * Compose는 처음 composable을 실행할 때 composition을 생성한다
 * 상태가 변경되면 composition이 업데이트 되는데 이를 recomposition이라 한다
 * compose가 추적할 상태(state)를 알아야 업데이트 될 때 recomposition을 예약할 수 있다
 * State, MutableState 유형을 사용해 앱의 상태를 compose가 관찰,추적 가능한 상태로 설정할 수 있다
 * State 유형은 변경할 수 없다(Read only) -> StateOf()
 * MutableState 유형은 변경할 수 있다 -> mutableStateOf()
 * 이 함수는 초깃값을 State 객체에 래핑된 매개변수로 수신한 다음, value의 값을 관찰 가능한 상태로 만든다
 */
@Composable
fun EditNumberField(
    @StringRes label: Int,//매개변수가 문자열 리소스 참조여아 함을 나타내기 위한 주석
    value: String,//표시할 현재 값
    onValueChange: (String) -> Unit,//사용자가 텍스트를 입력하는 경우 등 값이 변경될 때 상태가 업데이트될 수 있도록 트리거되는 콜백 람다
    modifier: Modifier = Modifier) {

    TextField(//import androidx.compose.material3.TextField
        value = value,//여기에서 전달하는 문자열 값을 표시하는 텍스트 상자
        onValueChange = onValueChange,//사용자가 상자에 텍스트를 입력할 때 트리거되는 람다 콜백
        label = { Text(stringResource(label)) },//텍스트 입력란에 라벨을 추가한다
        singleLine = true,//텍스트 상자가 여러 줄에서 가로로 스크롤 가능한 하나의 줄로 압축된다
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),//화면에 표시되는 키보드를 구성한다, 숫자 유형 키보드
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun TipTimeLayoutPreview() {
    TipTimeTheme {
        TipTimeLayout()
    }
}
