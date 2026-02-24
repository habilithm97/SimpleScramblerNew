package com.example.simplescramblernew.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.first

// 앱 전역에서 사용하는 Preferences DataStore
private val Context.dataStore by preferencesDataStore(name = "scramble")

// 스크램블 리스트 저장/복원 객체
object ScrambleDataStore {
    private val scrambleListKey  = stringPreferencesKey("scramble_list") // DataStore에 저장할 문자열 키
    private val gson = Gson()

    // 저장 (리스트 -> JSON)
    suspend fun save(context: Context, list: List<String>) {
        val json = gson.toJson(list) // 기본 타입만 저장 가능하기 때문에 변환
        context.dataStore.edit { prefs -> // DataStore 수정 블록
            prefs[scrambleListKey] = json // scrambleListKey에 json 값 저장
        }
    }

    // 복원 (JSON -> List<String>)
    suspend fun load(context: Context): List<String> {
        val prefs = context.dataStore.data.first() // Flow에서 현재 Preferences 값 한 번만 가져오기
        // 저장된 JSON 문자열 가져오기
        val json = prefs[scrambleListKey] ?: "[]" // 값이 없으면 빈 리스트 JSON 사용
        return gson.fromJson(json, object : TypeToken<List<String>>() {}.type) // 제네릭 타입 명시
    }
}