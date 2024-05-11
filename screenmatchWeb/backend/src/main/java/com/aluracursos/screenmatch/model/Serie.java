package com.aluracursos.screenmatch.model;

import jakarta.persistence.*;

import java.util.List;
import java.util.OptionalDouble;

@Entity // hace que esta clase se convierta en una tabla en la BD
@Table(name = "series") // cambia el nmbre de la tabla, ya que suele tener el mismo de la clase
public class Serie {
   @Id // genera el campo id en la tabla como primary key
   @GeneratedValue(strategy = GenerationType.IDENTITY) // autoincrementable
   private long id;

   @Column(unique = true) // hace el campo unico
   private String titulo;

   private Integer totalTemporadas;
   private Double evaluacion;
   private String poster;

   @Enumerated(EnumType.STRING) // enumera los enum
   private Categoria genero;

   private String actores;
   private String sinopsis;

//   @Transient // si por el momento no lo usaria o no lo agregaria a la bbdd.
   // relacion tabla serie y episodio. 1 serie -> muchos episodios.
   // Cascade hace que (en este caso ALL) cambios en la serie se reflejen en epsisodios.
   // Fetch lazy = no trae datos a menos que se lo pida. eager, trae todo junto.
   @OneToMany(mappedBy = "serie", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
   private List<Episodio> episodios;

   // default constructor
   public Serie() {
   };

   public Serie(DatosSerie datosSerie){
      this.titulo = datosSerie.titulo();
      this.totalTemporadas = datosSerie.totalTemporadas();
      this.evaluacion = OptionalDouble.of(Double.valueOf(datosSerie.evaluacion())).orElse(0);
      this.poster = datosSerie.poster();
      this.genero = Categoria.fromString(datosSerie.genero().split(",")[0].trim());
      this.actores = datosSerie.actores();
      // atributo comentado ya que la apikey de chatgpt no tiene credito para traducir la sinopsis.
//      this.sinopsis = ConsultaChatGPT.obtenerTraduccion(datosSerie.sinopsis());
      this.sinopsis = datosSerie.sinopsis();
   }

   public long getId() {
      return id;
   }

   public void setId(long id) {
      this.id = id;
   }

   public String getTitulo() {
      return titulo;
   }

   public void setTitulo(String titulo) {
      this.titulo = titulo;
   }

   public Integer getTotalTemporadas() {
      return totalTemporadas;
   }

   public void setTotalTemporadas(Integer totalTemporadas) {
      this.totalTemporadas = totalTemporadas;
   }

   public Double getEvaluacion() {
      return evaluacion;
   }

   public void setEvaluacion(Double evaluacion) {
      this.evaluacion = evaluacion;
   }

   public String getPoster() {
      return poster;
   }

   public void setPoster(String poster) {
      this.poster = poster;
   }

   public Categoria getGenero() {
      return genero;
   }

   public void setGenero(Categoria genero) {
      this.genero = genero;
   }

   public String getActores() {
      return actores;
   }

   public void setActores(String actores) {
      this.actores = actores;
   }

   public String getSinopsis() {
      return sinopsis;
   }

   public void setSinopsis(String sinopsis) {
      this.sinopsis = sinopsis;
   }

   public List<Episodio> getEpisodios() {
      return episodios;
   }

   public void setEpisodios(List<Episodio> episodios) {
      episodios.forEach(e -> e.setSerie(this));
      this.episodios = episodios;
   }

   @Override
   public String toString() {
      return "titulo=" + titulo +
            ", genero=" + genero +
            ", totalTemporadas=" + totalTemporadas +
            ", evaluacion=" + evaluacion +
            ", poster=" + poster +
            ", actores=" + actores +
            ", sinopsis=" + sinopsis +
            ", episodios=" + episodios;
   }
}
