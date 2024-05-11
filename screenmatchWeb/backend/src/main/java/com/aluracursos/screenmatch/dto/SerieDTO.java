package com.aluracursos.screenmatch.dto;

import com.aluracursos.screenmatch.model.Categoria;

// esto es lo que vamos a mostrar al frontend
public record SerieDTO(
      Long id,
      String titulo,
      Integer totalTemporadas,
      Double evaluacion,
      String poster,
      Categoria genero,
      String actores,
      String sinopsis
) {
}
