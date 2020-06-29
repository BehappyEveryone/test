package com.example.chatground2.model

object Pattern {
    const val nicknamePattern = "^[a-zA-Z0-9가-힣]{4,12}$"//영문,한글,숫자 아무렇게나 4~12자리
    const val passwordPattern = "^(?=.*\\d{1,20})(?=.*[a-zA-Z]{1,20}).{8,20}$"//영문 최소 1개이상, 숫자 최소 1개 이상, 8~20자리
}