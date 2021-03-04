package subway.app

import org.jgrapht.alg.shortestpath.DijkstraShortestPath
import org.jgrapht.graph.WeightedMultigraph
import subway.domain.WeightedEdge
import subway.domain.dto.Path
import subway.repository.SectionRepository
import subway.repository.StationRepository

class PathService (
    private val departure: String,
    private val destination: String,
    private val graph: WeightedMultigraph<String, WeightedEdge> =
        WeightedMultigraph<String, WeightedEdge>(WeightedEdge::class.java)
        ) {
    fun path(select: Int): Path { // 1: Distance, 2: Time
        getGraph(graph, select)

        DijkstraShortestPath(graph).getPath(departure, destination).let { it ->
            return Path(
                distance = it.weight.toInt(),
                time = it.edgeList.map { it.subWeight }.sum(),
                route = it.vertexList
            )
        }
    }

    private fun getGraph(
        graph: WeightedMultigraph<String, WeightedEdge>,
        select: Int
    ) {
        val stations = StationRepository.findAll()
        val sections = SectionRepository.findAll()

        stations.map { graph.addVertex(it.name) }
        sections.map {
            setGraphEdge(
                edge = graph.addEdge(it.upwardStation.name, it.downwardStation.name),
                weight = if(select == 1) it.distance!! else it.time!!,
                subWeight = if(select == 1) it.time!! else it.distance!!
            )
        }
    }

    fun setGraphEdge(
        edge: WeightedEdge,
        weight: Int,
        subWeight: Int
    ) {
        edge.subWeight = subWeight
        graph.setEdgeWeight(edge, weight.toDouble())
    }

}