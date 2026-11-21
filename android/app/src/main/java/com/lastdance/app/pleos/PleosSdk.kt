package com.lastdance.app.pleos

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Pleos SDK 경계. 웹 `src/pleos/sdk.ts`와 같은 모양.
 * 실제 Pleos Playground SDK(Vehicle, Navigation, ADAS, Gleo AI, Vehicle Location, Sync)를 받으면
 * 이 인터페이스의 구현체만 새로 만들고 [Pleos.current]를 바꾸면 된다. UI는 그대로.
 */
data class VehicleInfo(val nickname: String, val model: String, val odometerKm: Int, val rangeKm: Int, val batteryPct: Int)

data class RouteStatus(
    val currentPlace: String,
    val destination: String,
    val distanceKm: Double,
    val nextTurnKm: Double,
    val nextTurnRoad: String,
    val eta: String,
    val remainMin: Int,
    val remainKm: Double,
)

data class Weather(val label: String, val tempC: Int)

enum class Reaction { Fist, Thumb, Heart, Party, Clap }

enum class CameraId(val label: String) { Left("좌측"), FrontCenter("앞 중앙 카메라"), Right("우측") }

interface VehicleSdk { fun info(): VehicleInfo }
interface NavigationSdk { fun routeAt(progress: Float): RouteStatus }
interface WeatherSdk { fun now(): Weather }
interface AdasSdk { fun broadcastFarewell(): Flow<Reaction> }
interface GleoSdk { suspend fun ask(prompt: String): String }

class PleosSdk(
    val vehicle: VehicleSdk,
    val navigation: NavigationSdk,
    val weather: WeatherSdk,
    val adas: AdasSdk,
    val gleo: GleoSdk,
)

object Pleos {
    /** TODO(Pleos): 실제 SDK 어댑터로 교체 */
    var current: PleosSdk = MockPleos.create()
}

/* ---------------- Mock ---------------- */
object MockPleos {
    private val places = listOf("용산구청 지나는 중", "반포대교 지나는 중", "반포대교 지나는 중", "신사역 지나는 중")

    fun create() = PleosSdk(
        vehicle = object : VehicleSdk {
            override fun info() = VehicleInfo("푸딩이", "2022 IONIQ 5", 10592, 287, 80)
        },
        navigation = object : NavigationSdk {
            override fun routeAt(progress: Float): RouteStatus {
                val i = (progress * places.size).toInt().coerceIn(0, places.lastIndex)
                return RouteStatus(
                    currentPlace = places[i],
                    destination = "조선팰리스 강남",
                    distanceKm = Math.round((5.2 + progress * 9.7) * 10) / 10.0,
                    nextTurnKm = 1.5,
                    nextTurnRoad = "반포대로 방면",
                    eta = "2:06 PM",
                    remainMin = 6,
                    remainKm = 2.1,
                )
            }
        },
        weather = object : WeatherSdk {
            override fun now() = Weather("맑고 구름 조금", 17)
        },
        adas = object : AdasSdk {
            override fun broadcastFarewell() = flow {
                delay(1400)
                listOf(Reaction.Fist, Reaction.Thumb, Reaction.Clap, Reaction.Heart, Reaction.Party).forEach {
                    emit(it); delay(900)
                }
            }
        },
        gleo = object : GleoSdk {
            override suspend fun ask(prompt: String): String {
                delay(700)
                return "오늘은 푸딩이와의 마지막 드라이브예요. 반포대교 야경이 가장 예쁜 시간까지 18분 남았어요."
            }
        },
    )
}
