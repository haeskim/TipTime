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
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tiptime.ui.theme.TipTimeTheme
import java.text.NumberFormat
import java.util.Locale

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
    var roundUp by remember { mutableStateOf(false) }

    val tipPercent = tipInput.toDoubleOrNull() ?: 0.0
    val amount = amountInput.toDoubleOrNull() ?: 0.0//문자열을 double 숫자로 파싱한 결과 또는 null을 반환
    val tip = calculateTip(amount, tipPercent, roundUp)

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
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
            leadingIcon = R.drawable.money,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            value = amountInput,
            onValueChange = { amountInput = it },
            modifier = Modifier
                .padding(bottom = 32.dp)
                .fillMaxWidth()
        )
        EditNumberField(
            label = R.string.how_was_the_service,
            leadingIcon = R.drawable.percent,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            value = tipInput,
            onValueChange = { tipInput = it },
            modifier = Modifier
                .padding(bottom = 32.dp)
                .fillMaxWidth()
        )
        RoundTheTipRow(
            roundUp = roundUp,
            onRoundUpChanged = { roundUp = it },
            modifier = Modifier.padding(bottom = 32.dp)
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
//메서드가 공개되지만 테스트 목적으로만 공개된다고 사용자에게 표시
@VisibleForTesting
internal fun calculateTip(
    amount: Double,
    tipPercent: Double = 15.0,
    roundUp: Boolean
): String {
    var tip: Double = tipPercent / 100 * amount
    if (roundUp) {
        tip = kotlin.math.ceil(tip)//주어진 정숫값을 반올림한다(10.65 -> 11.00)
    }
    return NumberFormat.getCurrencyInstance(Locale.US).format(tip)
    //예시와 다르게 double형태가 아닌 int형태로 출력되는 문제가 있어 수정함
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
    @StringRes label: Int,//매개변수가 문자열 리소스 참조여야 함을 나타내기 위한 주석
    @DrawableRes leadingIcon: Int,
    keyboardOptions: KeyboardOptions,
    value: String,//표시할 현재 값
    onValueChange: (String) -> Unit,//사용자가 텍스트를 입력하는 경우 등 값이 변경될 때 상태가 업데이트될 수 있도록 트리거되는 콜백 람다
    modifier: Modifier = Modifier) {

    TextField(
        value = value,//여기에서 전달하는 문자열 값을 표시하는 텍스트 상자
        leadingIcon = { Icon(painter = painterResource(id = leadingIcon), null) },
        onValueChange = onValueChange,//사용자가 상자에 텍스트를 입력할 때 트리거되는 람다 콜백
        label = { Text(stringResource(label)) },//텍스트 입력란에 라벨을 추가한다
        singleLine = true,//텍스트 상자가 여러 줄에서 가로로 스크롤 가능한 하나의 줄로 압축된다
        keyboardOptions = keyboardOptions,
        /**
         * ImeAction.Search -> 돋보기, 검색을 실행하려고 할 때 사용
         * ImeAction.Send -> 비행기, 입력란의 텍스트를 보내려고 할 때 사용
         * ImeAction.Go -> 화살표(->), 입력한 텍스트 대상으로 이동하려고 할 때 사용
         * ImeAction.Next -> 화살표(->|), 다음으로 이동하려고 할 때 사용
         * ImeAction.Done -> 체크, 완료
         */
        modifier = modifier
    )
}

@Composable
fun RoundTheTipRow(
    roundUp: Boolean,
    onRoundUpChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.round_up_tip)
        )
        Switch(
            modifier = modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.End),
            checked = roundUp,
            onCheckedChange = onRoundUpChanged
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TipTimeLayoutPreview() {
    TipTimeTheme {
        TipTimeLayout()
    }
}
