package com.aluracursos.screenmatch.service;

import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.service.OpenAiService;

// Clase para traducir la sinopsis de las series.
public class ConsultaChatGPT {
   private final String openai_apiKey = System.getenv().get("OPENAI_APIKEY");
   public static String obtenerTraduccion(String texto) {
      OpenAiService service = new OpenAiService(openai_apikey);

      CompletionRequest requisicion = CompletionRequest.builder()
            .model("gpt-3.5-turbo-instruct")
            .prompt("Traduce al espaniol el siguiente texto: " + texto)
            .maxTokens(1000)
            .temperature(0.7)
            .build();

      var respuesta = service.createCompletion(requisicion);
      return respuesta.getChoices().get(0).getText();

   }
}
