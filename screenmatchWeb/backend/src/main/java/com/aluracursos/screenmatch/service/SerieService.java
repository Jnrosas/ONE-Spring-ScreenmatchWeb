package com.aluracursos.screenmatch.service;

import com.aluracursos.screenmatch.dto.EpisodioDTO;
import com.aluracursos.screenmatch.dto.SerieDTO;
import com.aluracursos.screenmatch.model.Categoria;
import com.aluracursos.screenmatch.model.Episodio;
import com.aluracursos.screenmatch.model.Serie;
import com.aluracursos.screenmatch.repositorio.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SerieService {

   @Autowired
   private SerieRepository repository;

   public List<SerieDTO> mostrarSeries() {
      //como .findaAll me devuelve una List<Serie>, usamos el metodo convierteDatos() para castear a SerieDTO
      return convierteDatos(repository.findAll()); //findAll es un metodo built in
   }

   public List<SerieDTO> obtenerTop5() {
      return convierteDatos(repository.findTop5ByOrderByEvaluacionDesc());
   }

   public List<SerieDTO> ultimosLanzamientos() {
      return convierteDatos(repository.ultimosLanzamientos());
   }

   public SerieDTO obtenerPorId(Long id) {
      Optional<Serie> serie = repository.findById(id); //findById es un metodo built in
      if (serie.isPresent()) {
         Serie s = serie.get();
         return new SerieDTO(s.getId(), s.getTitulo(), s.getTotalTemporadas(), s.getEvaluacion(),
               s.getPoster(), s.getGenero(), s.getActores(), s.getSinopsis());
      }
      return null;
   }

   public List<EpisodioDTO> obtenerTodasLasTemporadas(Long id) {
      Optional<Serie> serie = repository.findById(id);
      if (serie.isPresent()) {
         Serie s = serie.get();
         return convierteDatosEpisodio(s.getEpisodios());
      }
      return null;
   }

   public List<EpisodioDTO> obtenerTemporadaPorNumero(Long id, Long numeroTemporada) {
      return convierteDatosEpisodio(repository.obtenerTemporadaPorNumero(id, numeroTemporada));
   }

   public List<SerieDTO> seriesPorCategoria(String genero) {
      Categoria categoria = Categoria.fromEspanol(genero);
      return convierteDatos(repository.findSeriesByGenero(categoria));
   }

   public List<EpisodioDTO> top5EpisodiosPorSerie(Long id) {
      Optional<Serie> serie = repository.findById(id);
      if (serie.isPresent()) {
         Serie s = serie.get();
         return convierteDatosEpisodio(repository.top5EpisodiosPorSerie(s));
      }
      return null;
   }

   //con stream y map casteamos de Serie a SerieDTO y con collect(Collectors.toList(),
   // lo volvemos a hacer una lista, pero de SerieDTO. usamos el metodo convierteDatos() para ahorrar codigo
   public List<SerieDTO> convierteDatos(List<Serie> serie) {
      return serie.stream()
            .map(s -> new SerieDTO(s.getId(), s.getTitulo(), s.getTotalTemporadas(), s.getEvaluacion(),
                  s.getPoster(), s.getGenero(), s.getActores(), s.getSinopsis()))
            .collect(Collectors.toList());
   }


   //con stream y map casteamos de Episodio a EpisodioDTO y con collect(Collectors.toList(),
   // lo volvemos a hacer una lista, pero de EpisodioDTO. Usamos el metodo convierteDatosEpisodio() para ahorrar codigo
   public List<EpisodioDTO> convierteDatosEpisodio(List<Episodio> episodio) {
      return episodio.stream()
            .map(e -> new EpisodioDTO(e.getTemporada(), e.getTitulo(), e.getNumeroEpisodio()))
            .collect(Collectors.toList());
   }


}
