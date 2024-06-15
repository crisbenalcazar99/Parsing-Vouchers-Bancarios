package net.security.data.microservicesocr.messages.responses;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import net.security.data.microservicesocr.models.entities.VouchersEntity;
import org.springframework.http.HttpStatus;

@ToString
@Data
@NoArgsConstructor
@ApiModel(description = "Modelo que representa la informacion de un comprobante bancario")
public class JsonResponse {

  private Boolean respuesta;
  private HttpStatus codeStatus;
  private String mensaje;
  private Object resultado;
  private String detalleError;
  @ApiModelProperty(notes = "Modelo que representa la informacion de un comprobante bancario")
  private VouchersEntity vouchersEntity;


  public JsonResponse(Boolean respuesta, Object resultado) {
    this.respuesta = respuesta;
    this.resultado = resultado;
  }

  public JsonResponse(Boolean respuesta, String mensaje) {
    this.respuesta = respuesta;
    this.mensaje = mensaje;
  }

  public JsonResponse(Boolean respuesta, String mensaje, Object resultado) {
    this.respuesta = respuesta;
    this.mensaje = mensaje;
    this.resultado = resultado;
  }

  public JsonResponse(Boolean respuesta, HttpStatus codeStatus, Object resultado) {
    this.respuesta = respuesta;
    this.codeStatus = codeStatus;
    this.resultado = resultado;
  }

  public JsonResponse(Boolean respuesta, HttpStatus codeStatus, String mensaje) {
    this.respuesta = respuesta;
    this.codeStatus = codeStatus;
    this.mensaje = mensaje;
  }

  public JsonResponse(Boolean respuesta, HttpStatus codeStatus, String mensaje, Object resultado) {
    this.respuesta = respuesta;
    this.codeStatus = codeStatus;
    this.mensaje = mensaje;
    this.resultado = resultado;
  }

  public JsonResponse(Boolean respuesta, HttpStatus codeStatus, String mensaje, VouchersEntity vouchersEntity) {
    this.respuesta = respuesta;
    this.codeStatus = codeStatus;
    this.mensaje = mensaje;

    this.vouchersEntity = vouchersEntity;
  }

  public JsonResponse(Boolean respuesta, HttpStatus codeStatus, String mensaje, Object resultado, String detalleError) {
    this.respuesta = respuesta;
    this.codeStatus = codeStatus;
    this.mensaje = mensaje;
    this.resultado = resultado;
    this.detalleError = detalleError;
  }

  public static JsonResponse noFoundRegister(){
    return new JsonResponse(false, HttpStatus.NOT_FOUND, "Not Found Register");
  }

}
