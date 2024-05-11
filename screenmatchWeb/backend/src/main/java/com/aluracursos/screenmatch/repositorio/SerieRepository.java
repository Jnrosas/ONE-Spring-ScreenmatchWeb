package com.aluracursos.screenmatch.repositorio;

import com.aluracursos.screenmatch.model.Categoria;
import com.aluracursos.screenmatch.model.Episodio;
import com.aluracursos.screenmatch.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SerieRepository extends JpaRepository<Serie, Long> {
   Optional<Serie> findByTituloContainsIgnoreCase(String nombreSerie);
   List<Serie> findTop5ByOrderByEvaluacionDesc();
   List<Serie> findByGenero(Categoria categoria); // abajo esta usando JPQL findSeriesByGenero()

   // realizado con Derived Queries JPA
//   List<Serie> findByTotalTemporadasLessThanEqualAndEvaluacionGreaterThanEqual(int totalTemporadas, Double evaluacion);

   // realizado con Queries Nativas (sql)
//   @Query(value = "select * from series where total_temporadas <= 3 and evaluacion >= 8.4", nativeQuery = true)
//   List<Serie> seriePorTemporadaYEvalauacion();

   // realizado con JPQL
   @Query("select s from Serie s where s.totalTemporadas <= :totalTemporadas and s.evaluacion >= :evaluacion")
   List<Serie> seriePorTemporadaYEvalauacion(int totalTemporadas, Double evaluacion);

   @Query("select e from Serie s join s.episodios e where e.titulo ilike %:nombre%")
   List<Episodio> episodiosPorNombre(String nombre);

   @Query("select e from Serie s join s.episodios e where s = :serie order by e.evaluacion desc limit 5")
   List<Episodio> top5EpisodiosPorSerie(Serie serie);

   @Query("select s from Serie s join s.episodios e group by s order by max(e.fechaDeLanzamiento) desc limit 5")
   List<Serie> ultimosLanzamientos();

   @Query("select e from Serie s join s.episodios e where s.id = :id and e.temporada = :numeroTemporada")
   List<Episodio> obtenerTemporadaPorNumero(Long id, Long numeroTemporada);

   @Query("select s from Serie s where s.genero = :genero")
   List<Serie> findSeriesByGenero(Categoria genero); // arriba esta la version Derived Query findByGenero()
}
