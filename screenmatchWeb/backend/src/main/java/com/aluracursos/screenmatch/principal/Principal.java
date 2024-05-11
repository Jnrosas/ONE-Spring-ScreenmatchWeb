package com.aluracursos.screenmatch.principal;

import com.aluracursos.screenmatch.model.*;
import com.aluracursos.screenmatch.repositorio.SerieRepository;
import com.aluracursos.screenmatch.service.ConsumoAPI;
import com.aluracursos.screenmatch.service.ConvierteDatos;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoApi = new ConsumoAPI();
    private final String URL_BASE = "https://www.omdbapi.com/?t=";
    private final String apiKey = System.getenv().get("OMDB_APIKEY");
    private final String API_KEY = "&apikey=" + apiKey;
    private ConvierteDatos conversor = new ConvierteDatos();
    private List<DatosSerie> datosSeries = new ArrayList<>();
    private SerieRepository repositorio;
    private List<Serie> series;
    private Optional<Serie> serieBuscada;

    public Principal(SerieRepository repository) {
        this.repositorio = repository;
    }

    public void muestraElMenu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    1 - Buscar series 
                    2 - Buscar episodios
                    3 - Mostrar series buscadas
                    4 - Buscar series por titulo
                    5 - Top 5 mejores series
                    6 - Buscar series por categoria
                    7 - Buscar series con x max de temporadas y un y min de evaluacion
                    8 - Buscar episodio por nombre
                    9 - Top 5 mejores episodios por serie
                    0 - Salir
                    """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    mostrarSeriesBuscadas();
                    break;
                case 4:
                    buscarSeriesPorTitulo();
                    break;
                case 5:
                    buscarTop5Series();
                    break;
                case 6:
                    buscarSeriesPorCategoria();
                    break;
                case 7:
                    filtrarSeriesPorTemporadaYEvaluacion();
                    break;
                case 8:
                    buscarEpisodioPorNombre();
                    break;
                case 9:
                    mejores5EpisodiosPorSerie();
                    break;
                case 0:
                    System.out.println("Cerrando la aplicación...");
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }

    }

    private DatosSerie getDatosSerie() {
        System.out.println("Escribe el nombre de la serie que deseas buscar");
        var nombreSerie = teclado.nextLine();
        var json = consumoApi.obtenerDatos(URL_BASE + nombreSerie.replace(" ", "+") + API_KEY);
        System.out.println(json);
        DatosSerie datos = conversor.obtenerDatos(json, DatosSerie.class);
        return datos;
    }

    private void buscarEpisodioPorSerie() {
        mostrarSeriesBuscadas();
        System.out.println("Episodios de que Serie quiere ver? ");
        var nombreSerie = teclado.nextLine();
        Optional<Serie> serie = series.stream()
              .filter(s -> s.getTitulo().toLowerCase().contains(nombreSerie.toLowerCase()))
              .findFirst();
        if (serie.isPresent()) {
            var serieEncontrada = serie.get();
            List<DatosTemporadas> temporadas = new ArrayList<>();
            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumoApi.obtenerDatos(URL_BASE + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DatosTemporadas datosTemporada = conversor.obtenerDatos(json, DatosTemporadas.class);
                temporadas.add(datosTemporada);
            }
            temporadas.forEach(System.out::println);
            List<Episodio> episodios = temporadas.stream()
                  .flatMap(t -> t.episodios().stream()
                        .map(e -> new Episodio(t.numero(), e)))
                  .collect(Collectors.toList());
            serieEncontrada.setEpisodios(episodios);
            repositorio.save(serieEncontrada);
        }
    }
    private void buscarSerieWeb() {
        DatosSerie datos = getDatosSerie();
//        datosSeries.add(datos); // comentada xq ahora ya no vamos a guardar datos en la lista sino en una bbdd
        Serie serie = new Serie(datos);
        repositorio.save(serie);
        System.out.println(datos);
    }

    private void mostrarSeriesBuscadas() {
//        // las 4 lineas sgtes seran reemplazadas por lo sucesivo porque ahora usamos bbdd y no traemos todo de API.
//        List<Serie> series = new ArrayList<>();
//        series = datosSeries.stream()
//              .map(s -> new Serie(s))
//              .collect(Collectors.toList());

        series = repositorio.findAll();
        series.stream()
              .sorted(Comparator.comparing(Serie::getGenero))
              .forEach(System.out::println);

    }

    private void buscarSeriesPorTitulo() {
        System.out.println("Escribe el nombre de la serie que deseas buscar");
        var nombreSerie = teclado.nextLine();
        serieBuscada = repositorio.findByTituloContainsIgnoreCase(nombreSerie);
        if (serieBuscada.isPresent()) {
            System.out.println("La serie buscada es: " + serieBuscada.get());
        } else System.out.println("Serie no encontrada");
    }

    private void buscarTop5Series() {
        List<Serie> topSeries = repositorio.findTop5ByOrderByEvaluacionDesc();
        topSeries.forEach(s ->
              System.out.println("Serie: " + s.getTitulo() + ", Evaluacion: " + s.getEvaluacion()));
    }

    private void buscarSeriesPorCategoria() {
        System.out.println("Escriba genero/categoria de la serie que desea buscar: ");
        var genero = teclado.nextLine();
        var categoria = Categoria.fromEspanol(genero);
        List<Serie> seriesPorCategoria = repositorio.findByGenero(categoria);
        System.out.println("Las series de la categoria " + genero);
        seriesPorCategoria.forEach(System.out::println);
    }

    // usando Derived Queries JPA
//    private void filtrarSeriesPorTemporadaYEvaluacion() {
//        System.out.println("Series con un max de cuantas temporadas? ");
//        var temporadas = teclado.nextInt();
//        teclado.nextLine();
//        System.out.println("Series evaluadas con un min de: ");
//        var evaluacion = teclado.nextDouble();
//        teclado.nextLine();
//        List<Serie> series =
//              repositorio.findByTotalTemporadasLessThanEqualAndEvaluacionGreaterThanEqual(temporadas, evaluacion);
//        series.forEach(s -> System.out.println("Titulo: " + s.getTitulo() + ", Total Temporadas: " +
//              s.getTotalTemporadas() + ", Evaluacion: " + s.getEvaluacion()));
//    }

    // usando Queries Nativas SQL
//    private void filtrarSeriesPorTemporadaYEvaluacion() {
//        System.out.println("Series con un max de cuantas temporadas? ");
//        var temporadas = teclado.nextInt();
//        teclado.nextLine();
//        System.out.println("Series evaluadas con un min de: ");
//        var evaluacion = teclado.nextDouble();
//        teclado.nextLine();
//        List<Serie> series =
//              repositorio.seriePorTemporadaYEvalauacion();
//        series.forEach(s -> System.out.println("Titulo: " + s.getTitulo() + ", Total Temporadas: " +
//              s.getTotalTemporadas() + ", Evaluacion: " + s.getEvaluacion()));
//    }

    // usando JPQL
    private void filtrarSeriesPorTemporadaYEvaluacion() {
        System.out.println("Series con un max de cuantas temporadas? ");
        var temporadas = teclado.nextInt();
        teclado.nextLine();
        System.out.println("Series evaluadas con un min de: ");
        var evaluacion = teclado.nextDouble();
        teclado.nextLine();
        List<Serie> series =
              repositorio.seriePorTemporadaYEvalauacion(temporadas, evaluacion);
        series.forEach(s -> System.out.println("Titulo: " + s.getTitulo() + ", Total Temporadas: " +
              s.getTotalTemporadas() + ", Evaluacion: " + s.getEvaluacion()));
    }

    private void buscarEpisodioPorNombre() {
        System.out.println("Ingrese el nombre del episodio a buscar: ");
        var nombreEpisodio = teclado.nextLine();
        List<Episodio> episodios = repositorio.episodiosPorNombre(nombreEpisodio);
        episodios.forEach(e -> System.out.printf("Serie: %s, Episodio: %s, Temporada: %s, Evaluacion: %s\n",
              e.getSerie().getTitulo(), e.getNumeroEpisodio(), e.getTemporada(), e.getEvaluacion()));
    }

    private void mejores5EpisodiosPorSerie() {
        buscarSeriesPorTitulo();
        if (serieBuscada.isPresent()) {
            Serie serie = serieBuscada.get();
            List<Episodio> top5Episodios = repositorio.top5EpisodiosPorSerie(serie);
            top5Episodios.forEach(e -> System.out.printf("Serie: %s, Episodio: %s, Temporada: %s, Evaluacion: %s\n",
                  e.getSerie().getTitulo(), e.getTitulo(), e.getTemporada(), e.getEvaluacion()));
        }
    }
}

