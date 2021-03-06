package subway.app

import subway.service.StationService
import subway.view.allStations
import subway.view.deletedStation
import subway.view.infoMessage
import subway.view.inputSelect
import subway.view.inputStationNameToDelete
import subway.view.inputStationNameToRegister
import subway.view.registeredStation
import subway.view.stationPage

fun adminStation() {
    stationPage()
    val select = inputSelect()
    if (select == BACK) return

    when (select.toInt()) {
        MENU_ONE -> {
            StationService.register(inputStationNameToRegister())
            infoMessage()
            registeredStation()
        }
        MENU_TWO -> {
            StationService.delete(inputStationNameToDelete())
            infoMessage()
            deletedStation()
        }
        MENU_THREE -> allStations(StationService.getStations())
    }
}
