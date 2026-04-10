package com.desynatdriver.ui.home.cms.translation

data class TranslateResponse(
    val data: Data
) {
    data class Data(
        val translations: List<Translation>
    )

    data class Translation(
        val translatedText: String
    )
}