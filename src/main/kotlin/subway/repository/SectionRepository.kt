package subway.repository

import subway.domain.Section

const val KILOMETER = "km"

const val MINUTE = "분"

const val SEPARATOR_DISTANCE_AND_TIME = " / "

object SectionRepository {

    private val sections = mutableListOf<Section>()

    fun sections() = sections.toList()

    fun addSection(section: Section) {
        changeTerminalStation(section)
        firstSection(section)

        sections.add(section)
    }

    private fun firstSection(section: Section) {
        if (!existDownwardByName(section.line.name, section.downwardStation.name) &&
            !existUpwardByName(section.line.name, section.upwardStation.name) &&
            !existDownwardByName(section.line.name, section.upwardStation.name) &&
            !existUpwardByName(section.line.name, section.downwardStation.name)
        ) {
            section.upwardStation.upwardTerminal = true
            section.downwardStation.downwardTerminal = true
        }
    }

    fun changeTerminalStation(section: Section) {
        if (downwardTerminal(section.line.name, section.upwardStation.name)) {
            sections().filter {
                it.line.name == section.line.name &&
                    it.downwardStation.name == section.upwardStation.name
            }
                .map { it.downwardStation.downwardTerminal = false }
            section.downwardStation.downwardTerminal = true
        }
    }

    fun downwardTerminal(lineName: String, stationName: String): Boolean =
        sections().any {
            it.line.name == lineName &&
                it.downwardStation.name == stationName &&
                it.downwardStation.downwardTerminal
        }

    fun firstLineInSection(name: String): Boolean = sections.none { it.line.name == name }

    fun existDownwardByName(lineName: String, stationName: String): Boolean =
        sections().any { it.downwardStation.name == stationName && it.line.name == lineName }

    fun existUpwardByName(lineName: String, stationName: String): Boolean =
        sections().any { it.upwardStation.name == stationName && it.line.name == lineName }

    fun findDownwardNameByUpwardName(name: String): String = sections
        .filter { it.upwardStation.name == name }
        .map { it.downwardStation.name }
        .toString()

    fun existStationInLine(name: String) = sections()
        .any { it.downwardStation.name == name || it.upwardStation.name == name }

    fun deleteSection(lineName: String, upwardName: String, downwardName: String) = sections
        .removeIf {
            it.line.name == lineName &&
                it.upwardStation.name == upwardName &&
                it.downwardStation.name == downwardName
        }

    fun stationCountInSection(name: String) = sections().count { it.line.name == name }

    fun continuousStation(upwardName: String, downwardName: String) = sections()
        .any { it.downwardStation.name == downwardName && it.upwardStation.name == upwardName }

    fun wholeStationsInSection(name: String): List<String> {
        val wholeTrackInLine = mutableListOf<String>()

        var section = sections().first { it.line.name == name && it.upwardStation.upwardTerminal }
        while (true) {
            val upwardName = section.upwardStation.name
            val downwardName = section.downwardStation.name
            wholeTrackInLine.add(upwardName)
            wholeTrackInLine.add(distanceAndTime(section))
            if (!existUpwardByName(section.line.name, downwardName)) {
                wholeTrackInLine.add(downwardName)
                break
            }
            section = findUpwardByName(section.line.name, downwardName)
        }
        return wholeTrackInLine
    }

    private fun distanceAndTime(section: Section): String =
        section.distance.toString() + KILOMETER + SEPARATOR_DISTANCE_AND_TIME + section.time.toString() + MINUTE
    private fun findUpwardByName(lineName: String, stationName: String): Section =
        sections().first { it.line.name == lineName && it.upwardStation.name == stationName }
}
