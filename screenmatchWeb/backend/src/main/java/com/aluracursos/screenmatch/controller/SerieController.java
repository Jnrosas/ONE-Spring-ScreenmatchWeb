package com.aluracursos.screenmatch.controller;

import com.aluracursos.screenmatch.dto.EpisodioDTO;
import com.aluracursos.screenmatch.dto.SerieDTO;
import com.aluracursos.screenmatch.model.Categoria;
import com.aluracursos.screenmatch.service.SerieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;


@RestController
@RequestMapping("/series") // para no repetir /series en cada endpoint
public class SerieController {

   @Autowired
   private SerieService servicio;

   @GetMapping() // iba /series, pero ya lo toma gracias a @requestmapping
   public List<SerieDTO> mostrarSeries() {
      return servicio.mostrarSeries();
   }

   @GetMapping("/top5")
   public List<SerieDTO> obtenerTop5() {
      return servicio.obtenerTop5();
   }

   @GetMapping("/lanzamientos")
   public  List<SerieDTO> ultimosLanzamientos() {
      return servicio.ultimosLanzamientos();
   }

   @GetMapping("/{id}") // parametro dinamico para la url
   public SerieDTO obtenerPorId(@PathVariable Long id) {
      return servicio.obtenerPorId(id);
   }

   @GetMapping("/{id}/temporadas/todas")
   public List<EpisodioDTO> obtenerTodasLasTemporadas(@PathVariable Long id) {
      return servicio.obtenerTodasLasTemporadas(id);
   }

   @GetMapping("/{id}/temporadas/{numeroTemporada}")
   public List<EpisodioDTO> obtenerTemporadaPorNumero(@PathVariable Long id, @PathVariable Long numeroTemporada) {
      return servicio.obtenerTemporadaPorNumero(id, numeroTemporada);
   }

   @GetMapping("/categoria/{genero}")
   public List<SerieDTO> seriesPorCategria(@PathVariable String genero) {
      return servicio.seriesPorCategoria(genero);
   }

   @GetMapping("/{id}/temporadas/top")
   public List<EpisodioDTO> top5EpisodiosPorSerie(@PathVariable Long id) {
      return servicio.top5EpisodiosPorSerie(id);
   }

}
